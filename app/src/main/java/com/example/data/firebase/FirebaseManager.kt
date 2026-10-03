package com.example.data.firebase

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * DailyLog entity representing task progress in Firestore.
 */
data class DailyLog(
    val id: String = "",
    val studentUid: String = "",
    val taskId: String = "",
    val date: String = "",
    val isCompletedByStudent: Boolean = false,
    val isApprovedByParent: Boolean = false,
    val status: String = "NOT_DONE", // "NOT_DONE", "STUDENT_DONE", "APPROVED", "REJECTED"
    val submittedAt: Long = 0L,
    val approvedAt: Long = 0L,
    val diamondsAwarded: Int = 0,
    val rejectionReason: String? = null
)

/**
 * FirebaseManager coordinating Firestore updates, transactions, and real-time listeners.
 */
object FirebaseManager {

    private const val TAG = "FirebaseManager"

    /** Firebase yapılandırılmamışsa (google-services.json yok) uygulama çevrimdışı çalışmaya devam eder. */
    @Volatile
    private var isConfigured: Boolean? = null

    fun isAvailable(): Boolean {
        isConfigured?.let { return it }
        val available = runCatching { FirebaseFirestore.getInstance(); true }.getOrElse {
            Log.w(TAG, "Firebase yapılandırılmamış, yalnızca yerel veri kullanılacak: ${it.message}")
            false
        }
        isConfigured = available
        return available
    }

    /**
     * Firestore kuralları `request.auth != null` şartı aradığı için, oturum açılmadan
     * yapılan tüm yazma işlemleri sessizce reddedilir. Anonim oturum bunu çözer.
     */
    suspend fun ensureSignedIn(): Boolean = withContext(Dispatchers.IO) {
        if (!isAvailable()) return@withContext false
        runCatching {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) auth.signInAnonymously().await()
            true
        }.getOrElse {
            Log.w(TAG, "Anonim oturum açılamadı: ${it.message}")
            false
        }
    }

    /**
     * Updates daily_logs document for that studentUid, date, and taskId.
     * Sets isCompletedByStudent = true, isApprovedByParent = false, and status = 'STUDENT_DONE'.
     */
    suspend fun markTaskAsStudentDone(
        studentUid: String,
        taskId: String,
        date: String,
        firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val docId = "${date}_${studentUid}_$taskId"
            val logRef = firestore.collection("daily_logs").document(docId)

            val data = hashMapOf<String, Any?>(
                "id" to docId,
                "studentUid" to studentUid,
                "taskId" to taskId,
                "date" to date,
                "isCompletedByStudent" to true,
                "isApprovedByParent" to false,
                "status" to "STUDENT_DONE",
                "submittedAt" to System.currentTimeMillis(),
                "updatedAt" to Timestamp.now()
            )

            logRef.set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "markTaskAsStudentDone başarısız", e)
            Result.failure(e)
        }
    }

    /**
     * Parent approves daily log in Firestore via atomic Transaction:
     * 1) Updates daily_logs -> isApprovedByParent = true, status = 'APPROVED', diamondsAwarded.
     * 2) Atomically increments diamonds in student_profiles collection by diamondReward.
     */
    suspend fun approveDailyLog(
        studentUid: String,
        taskId: String,
        date: String,
        diamondReward: Int = 5,
        firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val docId = "${date}_${studentUid}_$taskId"
            val logRef = firestore.collection("daily_logs").document(docId)
            val profileRef = firestore.collection("student_profiles").document(studentUid)

            firestore.runTransaction { transaction ->
                val logData = hashMapOf<String, Any?>(
                    "status" to "APPROVED",
                    "isCompletedByStudent" to true,
                    "isApprovedByParent" to true,
                    "approvedAt" to System.currentTimeMillis(),
                    "diamondsAwarded" to diamondReward,
                    "updatedAt" to Timestamp.now()
                )
                transaction.set(logRef, logData, SetOptions.merge())

                val profileSnap = transaction.get(profileRef)
                val currentDiamonds = if (profileSnap.exists()) (profileSnap.getLong("diamonds") ?: 0L) else 0L
                transaction.set(
                    profileRef,
                    mapOf(
                        "id" to studentUid,
                        "diamonds" to currentDiamonds + diamondReward,
                        "lastActiveDate" to date,
                        "updatedAt" to Timestamp.now()
                    ),
                    SetOptions.merge()
                )
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "approveDailyLog başarısız", e)
            Result.failure(e)
        }
    }

    /**
     * Parent rejects / sends back daily log with feedback note.
     */
    suspend fun rejectDailyLog(
        studentUid: String,
        taskId: String,
        date: String,
        reason: String,
        firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val docId = "${date}_${studentUid}_$taskId"
            val logRef = firestore.collection("daily_logs").document(docId)
            val data = hashMapOf<String, Any?>(
                "status" to "REJECTED",
                "isCompletedByStudent" to false,
                "isApprovedByParent" to false,
                "rejectionReason" to reason,
                "updatedAt" to Timestamp.now()
            )
            logRef.set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Saves custom task preferences to user_task_prefs collection.
     */
    suspend fun saveUserTaskPrefs(
        studentUid: String,
        taskIds: List<String>,
        isCustom: Boolean,
        firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val prefRef = firestore.collection("user_task_prefs").document(studentUid)
            val data = hashMapOf<String, Any?>(
                "studentUid" to studentUid,
                "taskIds" to taskIds,
                "isCustom" to isCustom,
                "taskCount" to taskIds.size,
                "updatedAt" to Timestamp.now()
            )
            prefRef.set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Real-time listener for student's daily_logs for instant parent dashboard synchronization.
     */
    fun listenToStudentDailyLogs(
        studentUid: String,
        date: String,
        firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
        onUpdate: (List<DailyLog>) -> Unit
    ): ListenerRegistration? {
        return try {
            firestore.collection("daily_logs")
                .whereEqualTo("studentUid", studentUid)
                .whereEqualTo("date", date)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "daily_logs dinleyicisi hata verdi", error)
                        return@addSnapshotListener
                    }
                    if (snapshot == null) return@addSnapshotListener
                    val logs = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(DailyLog::class.java)
                    }
                    onUpdate(logs)
                }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Real-time listener for classroom_config/dailyAnnouncement
     */
    fun listenToDailyAnnouncement(
        classId: String = "default_class",
        firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
        onUpdate: (String) -> Unit
    ): ListenerRegistration? {
        return try {
            firestore.collection("classroom_config").document(classId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                    val announcement = snapshot.getString("dailyAnnouncement") ?: ""
                    onUpdate(announcement)
                }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Real-time listener for student diamond vault & rewards
     */
    fun listenToStudentProfile(
        studentUid: String,
        firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
        onUpdate: (diamonds: Int, streak: Int, monthlyTarget: Int, rewardTitle: String) -> Unit
    ): ListenerRegistration? {
        return try {
            firestore.collection("student_profiles").document(studentUid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                    val diamonds = snapshot.getLong("diamonds")?.toInt() ?: 0
                    val streak = snapshot.getLong("currentStreak")?.toInt() ?: 0
                    val target = snapshot.getLong("monthlyTargetDiamonds")?.toInt() ?: 300
                    val reward = snapshot.getString("monthlyRewardTitle") ?: "Aylık Ödül"
                    onUpdate(diamonds, streak, target, reward)
                }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Module 6: Awards badge in Firestore 'Badges' and 'student_profiles' collection.
     * Records badge document with unlock timestamp, studentUid, and badgeId.
     */
    suspend fun awardBadgeToStudent(
        studentUid: String,
        badgeId: String,
        badgeTitle: String,
        firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val badgeDocId = "${studentUid}_$badgeId"
            val badgeRecord = hashMapOf(
                "studentUid" to studentUid,
                "badgeId" to badgeId,
                "badgeTitle" to badgeTitle,
                "unlockedAt" to Timestamp.now(),
                "isUnlocked" to true
            )
            firestore.collection("Badges").document(badgeDocId)
                .set(badgeRecord, SetOptions.merge()).await()

            // Update unlocked badges list in student profile
            val profileRef = firestore.collection("student_profiles").document(studentUid)
            firestore.runTransaction { tx ->
                val snapshot = tx.get(profileRef)
                if (snapshot.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    val existing = snapshot.get("unlockedBadges") as? List<String> ?: emptyList()
                    if (!existing.contains(badgeId)) {
                        tx.update(profileRef, "unlockedBadges", existing + badgeId)
                    }
                }
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
