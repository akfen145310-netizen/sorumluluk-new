package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.UserRole

/**
 * Yerel oturum ve görünüm ayarlarını yönetir.
 * Kullanıcının hangi rolle kayıt olduğunu, hangi öğrenci profiline bağlı olduğunu
 * ve tipografi tercihlerini kalıcı olarak saklar.
 */
class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var registeredRole: UserRole?
        get() {
            val roleStr = prefs.getString(KEY_REGISTERED_ROLE, null) ?: return null
            return try {
                UserRole.valueOf(roleStr)
            } catch (_: IllegalArgumentException) {
                null
            }
        }
        set(value) {
            prefs.edit().apply {
                if (value == null) remove(KEY_REGISTERED_ROLE) else putString(KEY_REGISTERED_ROLE, value.name)
                apply()
            }
        }

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) { prefs.edit().putString(KEY_USER_NAME, value).apply() }

    var linkedStudentName: String
        get() = prefs.getString(KEY_LINKED_STUDENT, "") ?: ""
        set(value) { prefs.edit().putString(KEY_LINKED_STUDENT, value).apply() }

    /** Bu cihazın takip ettiği öğrenci profilinin kimliği (öğrenci kendisi, veli ise çocuğu). */
    var activeStudentId: String?
        get() = prefs.getString(KEY_ACTIVE_STUDENT_ID, null)
        set(value) {
            prefs.edit().apply {
                if (value == null) remove(KEY_ACTIVE_STUDENT_ID) else putString(KEY_ACTIVE_STUDENT_ID, value)
                apply()
            }
        }

    /** Demo/örnek veriler yalnızca ilk kurulumda bir kez yazılır. */
    var isDemoSeeded: Boolean
        get() = prefs.getBoolean(KEY_DEMO_SEEDED, false)
        set(value) { prefs.edit().putBoolean(KEY_DEMO_SEEDED, value).apply() }

    var fontOptionId: String
        get() = prefs.getString(KEY_FONT_OPTION, "lexend") ?: "lexend"
        set(value) { prefs.edit().putString(KEY_FONT_OPTION, value).apply() }

    var fontScale: Float
        get() = prefs.getFloat(KEY_FONT_SCALE, 1.0f)
        set(value) { prefs.edit().putFloat(KEY_FONT_SCALE, value).apply() }

    var dontShowIntroAgain: Boolean
        get() = prefs.getBoolean(KEY_DONT_SHOW_INTRO_AGAIN, false)
        set(value) { prefs.edit().putBoolean(KEY_DONT_SHOW_INTRO_AGAIN, value).apply() }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_REGISTERED_ROLE)
            .remove(KEY_USER_NAME)
            .remove(KEY_LINKED_STUDENT)
            .remove(KEY_ACTIVE_STUDENT_ID)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "responsibility_app_prefs"
        private const val KEY_REGISTERED_ROLE = "key_registered_role"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_LINKED_STUDENT = "key_linked_student"
        private const val KEY_ACTIVE_STUDENT_ID = "key_active_student_id"
        private const val KEY_DEMO_SEEDED = "key_demo_seeded"
        private const val KEY_FONT_OPTION = "key_font_option"
        private const val KEY_FONT_SCALE = "key_font_scale"
        private const val KEY_DONT_SHOW_INTRO_AGAIN = "key_dont_show_intro_again"

        @Volatile
        private var instance: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return instance ?: synchronized(this) {
                instance ?: AppPreferences(context.applicationContext).also { instance = it }
            }
        }
    }
}
