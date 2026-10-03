package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResponsibilityDao {

    // ---------------- Görev kataloğu ----------------

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE categoryId = :catId")
    fun getTasksByCategory(catId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskByIdSync(taskId: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int

    // ---------------- Günlük program ----------------

    @Query("SELECT * FROM daily_schedules WHERE dateString = :dateString AND studentId = :studentId LIMIT 1")
    fun getDailySchedule(dateString: String, studentId: String): Flow<DailyScheduleEntity?>

    @Query("SELECT * FROM daily_schedules WHERE dateString = :dateString AND studentId = :studentId LIMIT 1")
    suspend fun getDailyScheduleSync(dateString: String, studentId: String): DailyScheduleEntity?

    @Query("SELECT * FROM daily_schedules WHERE studentId = :studentId AND dateString < :dateString ORDER BY dateString DESC LIMIT 1")
    suspend fun getLatestScheduleBefore(studentId: String, dateString: String): DailyScheduleEntity?

    @Query("SELECT dateString, taskIdsCsv FROM daily_schedules WHERE studentId = :studentId ORDER BY dateString DESC LIMIT 400")
    suspend fun getScheduleSizesForStudent(studentId: String): List<DateScheduleSize>

    @Query("SELECT studentId, taskIdsCsv FROM daily_schedules WHERE dateString = :dateString")
    fun getSchedulesForDate(dateString: String): Flow<List<ScheduleSize>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySchedule(schedule: DailyScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySchedules(schedules: List<DailyScheduleEntity>)

    @Query("DELETE FROM daily_schedules WHERE studentId = :studentId")
    suspend fun deleteSchedulesForStudent(studentId: String)

    // ---------------- Görev kayıtları ----------------

    @Query("SELECT * FROM task_logs WHERE dateString = :dateString AND studentId = :studentId")
    fun getTaskLogsForDate(dateString: String, studentId: String): Flow<List<TaskLogEntity>>

    @Query(
        "SELECT * FROM task_logs WHERE studentId = :studentId AND status = 'STUDENT_DONE' " +
            "ORDER BY submittedTime DESC"
    )
    fun getWaitingApprovalLogs(studentId: String): Flow<List<TaskLogEntity>>

    @Query("SELECT * FROM task_logs WHERE studentId = :studentId AND dateString = :dateString AND status = 'STUDENT_DONE'")
    suspend fun getWaitingApprovalLogsSync(studentId: String, dateString: String): List<TaskLogEntity>

    @Query("SELECT * FROM task_logs WHERE id = :logId LIMIT 1")
    suspend fun getTaskLogById(logId: String): TaskLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskLog(log: TaskLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskLogs(logs: List<TaskLogEntity>)

    @Update
    suspend fun updateTaskLog(log: TaskLogEntity)

    @Query("DELETE FROM task_logs WHERE studentId = :studentId")
    suspend fun deleteLogsForStudent(studentId: String)

    @Query(
        "SELECT dateString, COUNT(*) AS approvedCount FROM task_logs " +
            "WHERE studentId = :studentId AND status = 'APPROVED' " +
            "GROUP BY dateString ORDER BY dateString DESC LIMIT 400"
    )
    suspend fun getApprovedCountsByDate(studentId: String): List<DateApprovedCount>

    @Query(
        "SELECT studentId, COUNT(*) AS approvedCount FROM task_logs " +
            "WHERE dateString = :dateString AND status = 'APPROVED' GROUP BY studentId"
    )
    fun getApprovedCountsForDate(dateString: String): Flow<List<StudentApprovedCount>>

    @Query("SELECT COUNT(*) FROM task_logs WHERE studentId = :studentId AND status = 'APPROVED'")
    suspend fun getTotalApprovedCount(studentId: String): Int

    // ---------------- Öğrenci profilleri ----------------

    @Query("SELECT * FROM student_profiles WHERE id = :studentId LIMIT 1")
    fun getStudentProfile(studentId: String): Flow<StudentProfileEntity?>

    @Query("SELECT * FROM student_profiles ORDER BY name ASC")
    fun getAllStudentProfiles(): Flow<List<StudentProfileEntity>>

    @Query("SELECT * FROM student_profiles ORDER BY name ASC")
    suspend fun getAllStudentProfilesSync(): List<StudentProfileEntity>

    @Query("SELECT * FROM student_profiles WHERE id = :studentId LIMIT 1")
    suspend fun getStudentProfileSync(studentId: String): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentProfile(profile: StudentProfileEntity)

    @Query("UPDATE student_profiles SET diamonds = MAX(0, diamonds + :addDiamonds) WHERE id = :studentId")
    suspend fun incrementDiamonds(studentId: String, addDiamonds: Int)

    @Query("UPDATE student_profiles SET streakDays = :streak, lastApprovedDate = :dateString WHERE id = :studentId")
    suspend fun updateStreak(studentId: String, streak: Int, dateString: String)

    @Query("UPDATE student_profiles SET monthlyRewardTitle = :title, monthlyTargetDiamonds = :target WHERE id = :studentId")
    suspend fun updateMonthlyReward(studentId: String, title: String, target: Int)

    @Query("UPDATE student_profiles SET isRewardClaimed = :claimed, rewardClaimedMonth = :month WHERE id = :studentId")
    suspend fun updateRewardClaim(studentId: String, claimed: Boolean, month: String)

    @Query("DELETE FROM student_profiles WHERE id = :studentId")
    suspend fun deleteStudentProfile(studentId: String)

    // ---------------- Sınıf ayarları ----------------

    @Query("SELECT * FROM classroom_config WHERE id = :id LIMIT 1")
    fun getClassroomConfig(id: String = "default_class"): Flow<ClassroomConfigEntity?>

    @Query("SELECT * FROM classroom_config WHERE id = :id LIMIT 1")
    suspend fun getClassroomConfigSync(id: String = "default_class"): ClassroomConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassroomConfig(config: ClassroomConfigEntity)

    // ---------------- Kayıtlı kullanıcılar ----------------

    @Query("SELECT * FROM registered_users ORDER BY registeredDate DESC")
    fun getRegisteredUsers(): Flow<List<RegisteredUserEntity>>

    @Query("SELECT * FROM registered_users")
    suspend fun getRegisteredUsersSync(): List<RegisteredUserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegisteredUser(user: RegisteredUserEntity)

    @Query("DELETE FROM registered_users WHERE id = :userId")
    suspend fun deleteRegisteredUser(userId: String)

    @Query("DELETE FROM registered_users WHERE linkedStudentId = :studentId")
    suspend fun deleteRegisteredUsersForStudent(studentId: String)

    // ---------------- Etkinlikler ----------------

    @Query("SELECT * FROM activities ORDER BY isActive DESC, startDate DESC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun deleteActivity(activityId: String)

    @Query("UPDATE activities SET isActive = CASE WHEN id = :activityId THEN 1 ELSE 0 END")
    suspend fun setActiveActivity(activityId: String)

    @Query("SELECT * FROM activities WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveActivitySync(): ActivityEntity?

    @Query("SELECT COUNT(*) FROM activities")
    suspend fun getActivityCount(): Int

    @Query("SELECT * FROM activities WHERE id = :activityId LIMIT 1")
    suspend fun getActivityById(activityId: String): ActivityEntity?

    // ---------------- Bileşik işlemler ----------------

    /** Bir öğrencinin tüm izlerini tek transaction'da siler. */
    @Transaction
    suspend fun purgeStudent(studentId: String) {
        deleteLogsForStudent(studentId)
        deleteSchedulesForStudent(studentId)
        deleteRegisteredUsersForStudent(studentId)
        deleteStudentProfile(studentId)
    }
}
