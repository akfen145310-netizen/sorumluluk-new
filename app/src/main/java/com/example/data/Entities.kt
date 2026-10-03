package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val categoryId: String,
    val subCategoryId: String? = null,
    val diamondReward: Int = 5
)

@Entity(
    tableName = "daily_schedules",
    primaryKeys = ["dateString", "studentId"],
    indices = [Index(value = ["studentId"], name = "index_daily_schedules_studentId")]
)
data class DailyScheduleEntity(
    val dateString: String,
    val studentId: String,
    val taskIdsCsv: String,
    val isCustom: Boolean = false,
    val sourceTitle: String = "Öğretmen Standart Listesi"
)

@Entity(
    tableName = "task_logs",
    indices = [
        Index(value = ["studentId", "dateString"], name = "index_task_logs_studentId_dateString"),
        Index(value = ["studentId", "status"], name = "index_task_logs_studentId_status")
    ]
)
data class TaskLogEntity(
    @PrimaryKey val id: String,
    val dateString: String,
    val studentId: String,
    val taskId: String,
    val status: String, // "NOT_DONE", "STUDENT_DONE", "APPROVED", "REJECTED"
    val isCompletedByStudent: Boolean = false,
    val isApprovedByParent: Boolean = false,
    val submittedTime: Long = 0L,
    val approvedTime: Long = 0L,
    val diamondsAwarded: Int = 0,
    val rejectionReason: String? = null
)

@Entity(tableName = "student_profiles")
data class StudentProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val className: String,
    val diamonds: Int,
    val streakDays: Int,
    val monthlyRewardTitle: String,
    val monthlyTargetDiamonds: Int,
    val isRewardClaimed: Boolean = false,
    /** Seri (streak) hesabının son güncellendiği gün. */
    val lastApprovedDate: String = "",
    /** Ödülün hangi ay için talep edildiği (yyyy-MM). Yeni ayda kutlama tekrar açılır. */
    val rewardClaimedMonth: String = ""
)

/**
 * Öğretmen ekranları için türetilmiş görünüm modeli.
 * Artık kalıcı bir tablodan değil, student_profiles + task_logs birleşiminden üretilir.
 */
@Entity(tableName = "classmate_stats")
data class ClassmateStatEntity(
    @PrimaryKey val id: String,
    val name: String,
    val completedToday: Int,
    val totalToday: Int = 20,
    val diamonds: Int,
    val streakDays: Int
)

@Entity(tableName = "classroom_config")
data class ClassroomConfigEntity(
    @PrimaryKey val id: String = "default_class",
    val className: String = "5-A Sınıfı Değerler & Sorumluluk",
    val subtitle: String = "Öğretmen: Zeynep Öğretmen • Değerler Eğitimi ve Sorumluluk Takibi",
    val activityName: String = "Haftalık Sorumluluk Hareketi",
    val joinCode: String = "DEGERLER-5A-2026",
    val teacherPin: String = "1234",
    val dailyAnnouncement: String = "🌟 Bugünün görevlerini tamamla, parlayan elmasları topla ve ay sonundaki büyük ödülü yakala! Unutma, her sorumluluk seni daha güçlü yapar!"
)

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val joinCode: String,
    val startDate: String,
    val targetDays: Int = 30,
    val isActive: Boolean = false,
    val customTasksJson: String = "",
    val activeTaskIdsCsv: String = ""
)

data class ActivityCustomTask(
    val slotIndex: Int,
    val title: String = "",
    val isActive: Boolean = false,
    val diamondReward: Int = 5
)

@Entity(
    tableName = "registered_users",
    indices = [Index(value = ["linkedStudentId"], name = "index_registered_users_linkedStudentId")]
)
data class RegisteredUserEntity(
    @PrimaryKey val id: String,
    val role: String, // "STUDENT", "PARENT", "TEACHER"
    val fullName: String,
    /** Öğrenci kaydında kendi profil kimliği, veli kaydında bağlı olduğu öğrencinin kimliği. */
    val linkedStudentId: String? = null,
    val linkedStudentName: String? = null,
    val phoneOrContact: String? = null,
    val classId: String = "default_class",
    val activityName: String = "Haftalık Sorumluluk Hareketi",
    val registeredDate: String
)

/* --- DAO projeksiyonları (Room bu sınıfları sorgu sonucuna eşler) --- */

data class DateApprovedCount(val dateString: String, val approvedCount: Int)
data class StudentApprovedCount(val studentId: String, val approvedCount: Int)
data class ScheduleSize(val studentId: String, val taskIdsCsv: String)
data class DateScheduleSize(val dateString: String, val taskIdsCsv: String)
