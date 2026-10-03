package com.example.data.firebase

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Sealed results for smart onboarding and registration.
 */
sealed interface OnboardingResult {
    data class Success(
        val userId: String,
        val role: String,
        val fullName: String,
        val classId: String,
        val activityId: String,
        val activityTitle: String,
        val linkedStudentId: String? = null,
        val linkedStudentName: String? = null
    ) : OnboardingResult

    sealed interface Failure : OnboardingResult {
        val message: String

        data class InvalidJoinCode(override val message: String = "Geçersiz veya süresi dolmuş katılım kodu.") : Failure
        data class StudentNotFound(
            override val message: String
        ) : Failure
        data class TransactionFailed(override val message: String) : Failure
    }
}

/**
 * Service managing QR generation and Firestore Smart Onboarding transactions.
 */
object FirebaseOnboardingService {

    /**
     * Teacher Side: QR Code Generation.
     * Encodes the joinCode and activity metadata into a standard Android Bitmap.
     */
    fun generateActivityQrBitmap(
        joinCode: String,
        classId: String = "default_class",
        activityId: String = "act_default",
        sizePx: Int = 512,
        darkColor: Int = AndroidColor.BLACK,
        lightColor: Int = AndroidColor.WHITE
    ): Bitmap {
        val payload = "KARAKTER_KAYIT|CLASS:$classId|ACTIVITY:$activityId|CODE:$joinCode"
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1
        )
        val bitMatrix = QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) darkColor else lightColor
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    /**
     * Normalizes string for Turkish name comparison (lowercased, spaces condensed).
     */
    fun normalizeName(name: String): String {
        val trLocale = Locale.forLanguageTag("tr")
        return name.trim().lowercase(trLocale).replace(Regex("\\s+"), " ")
    }

    /**
     * User Side: Smart Onboarding & Parent-Student Linking Transaction.
     * Executes atomic Firestore verification and registration.
     */
    suspend fun verifyAndJoinClass(
        firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
        authUid: String,
        role: String, // "STUDENT" or "PARENT"
        fullName: String,
        phone: String?,
        joinCode: String,
        linkedStudentName: String?
    ): OnboardingResult = withContext(Dispatchers.IO) {
        try {
            // 1. Resolve Activity by joinCode
            val activitySnapshot = firestore.collection("activities")
                .whereEqualTo("joinCode", joinCode.trim().uppercase(Locale.ROOT))
                .limit(1)
                .get()
                .await()

            if (activitySnapshot.isEmpty) {
                return@withContext OnboardingResult.Failure.InvalidJoinCode(
                    "'$joinCode' katılım koduna sahip aktif bir etkinlik bulunamadı. Lütfen kodu veya karekodu kontrol ediniz."
                )
            }

            val activityDoc = activitySnapshot.documents.first()
            val activityId = activityDoc.id
            val classId = activityDoc.getString("classId") ?: "default_class"
            val activityTitle = activityDoc.getString("title") ?: "Sorumluluk Etkinliği"

            var verifiedStudentUid: String? = null
            var verifiedStudentCanonicalName: String? = null

            // 2. Strict Verification for PARENT role
            if (role == "PARENT") {
                if (linkedStudentName.isNullOrBlank()) {
                    return@withContext OnboardingResult.Failure.StudentNotFound(
                        "Veli kaydı için bağlı olacağınız öğrencinin ad ve soyadını girmelisiniz."
                    )
                }

                val targetNormalized = normalizeName(linkedStudentName)

                // Query students belonging to this class
                val studentsSnapshot = firestore.collection("registered_users")
                    .whereEqualTo("classId", classId)
                    .whereEqualTo("role", "STUDENT")
                    .get()
                    .await()

                // Find matching student
                val matchedStudent = studentsSnapshot.documents.firstOrNull { doc ->
                    val sName = doc.getString("fullName") ?: ""
                    normalizeName(sName) == targetNormalized
                }

                if (matchedStudent != null) {
                    verifiedStudentUid = matchedStudent.id
                    verifiedStudentCanonicalName = matchedStudent.getString("fullName")
                } else {
                    // Also check student_profiles collection as secondary fallback
                    val profilesSnapshot = firestore.collection("student_profiles")
                        .whereEqualTo("classId", classId)
                        .get()
                        .await()

                    val matchedProfile = profilesSnapshot.documents.firstOrNull { doc ->
                        val pName = doc.getString("studentName") ?: ""
                        normalizeName(pName) == targetNormalized
                    }

                    if (matchedProfile != null) {
                        verifiedStudentUid = matchedProfile.id
                        verifiedStudentCanonicalName = matchedProfile.getString("studentName")
                    } else {
                        return@withContext OnboardingResult.Failure.StudentNotFound(
                            "Doğrulama Başarısız: '$linkedStudentName' isimli bir öğrenci '$classId' sınıfında bulunamadı. Lütfen öğrencinin önce sınıfa katıldığından ve adını doğru yazdığınızdan emin olunuz."
                        )
                    }
                }
            }

            // 3. Atomic Firestore Transaction for Registration & Group Joining
            firestore.runTransaction { transaction ->
                val userRef = firestore.collection("registered_users").document(authUid)

                val userData = hashMapOf<String, Any?>(
                    "uid" to authUid,
                    "role" to role,
                    "fullName" to fullName.trim(),
                    "phone" to phone?.trim(),
                    "classId" to classId,
                    "activityId" to activityId,
                    "activityTitle" to activityTitle,
                    "joinCode" to joinCode.trim().uppercase(Locale.ROOT),
                    "linkedStudentId" to verifiedStudentUid,
                    "linkedStudentName" to (verifiedStudentCanonicalName ?: linkedStudentName?.trim()),
                    "joinDate" to Timestamp.now(),
                    "isActive" to true
                )

                transaction.set(userRef, userData, SetOptions.merge())

                // If registering as a student, create / ensure student profile exists
                if (role == "STUDENT") {
                    val profileRef = firestore.collection("student_profiles").document(authUid)
                    val profileData = hashMapOf<String, Any?>(
                        "studentUid" to authUid,
                        "studentName" to fullName.trim(),
                        "classId" to classId,
                        "currentActivityId" to activityId,
                        "currentStreak" to 0,
                        "diamonds" to 0,
                        "monthlyTargetDiamonds" to 300,
                        "monthlyRewardTitle" to "Aylık Kitap & Akıl Oyunu Seti",
                        "lastActiveDate" to "",
                        "updatedAt" to Timestamp.now()
                    )
                    transaction.set(profileRef, profileData, SetOptions.merge())
                }
            }.await()

            OnboardingResult.Success(
                userId = authUid,
                role = role,
                fullName = fullName.trim(),
                classId = classId,
                activityId = activityId,
                activityTitle = activityTitle,
                linkedStudentId = verifiedStudentUid,
                linkedStudentName = verifiedStudentCanonicalName ?: linkedStudentName?.trim()
            )
        } catch (e: Exception) {
            OnboardingResult.Failure.TransactionFailed("Kayıt işlemi gerçekleştirilemedi: ${e.localizedMessage}")
        }
    }
}
