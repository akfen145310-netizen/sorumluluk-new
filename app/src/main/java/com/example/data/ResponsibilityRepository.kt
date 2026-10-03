package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max

data class DailyTaskUiItem(
    val task: TaskEntity,
    val log: TaskLogEntity
)

class ResponsibilityRepository(
    private val dao: ResponsibilityDao
) {

    companion object {
        private val TR = Locale.forLanguageTag("tr")

        /** Bir günün "başarılı" sayılması için gereken onay oranı. */
        private const val STREAK_SUCCESS_RATIO = 0.6

        fun getTodayDateString(): String =
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        fun getCurrentMonthKey(): String =
            SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

        fun normalizeName(raw: String?): String =
            raw?.trim()?.lowercase(TR)?.replace(Regex("\\s+"), " ") ?: ""
    }

    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val classroomConfig: Flow<ClassroomConfigEntity?> = dao.getClassroomConfig()
    val registeredUsers: Flow<List<RegisteredUserEntity>> = dao.getRegisteredUsers()
    val allActivities: Flow<List<ActivityEntity>> = dao.getAllActivities()

    fun getStudentProfile(studentId: String): Flow<StudentProfileEntity?> =
        dao.getStudentProfile(studentId)

    fun getAllStudentProfiles(): Flow<List<StudentProfileEntity>> =
        dao.getAllStudentProfiles()

    fun getWaitingApprovalLogs(studentId: String): Flow<List<TaskLogEntity>> =
        dao.getWaitingApprovalLogs(studentId)

    fun getDailySchedule(dateString: String, studentId: String): Flow<DailyScheduleEntity?> =
        dao.getDailySchedule(dateString, studentId)

    /**
     * Öğretmen panosundaki sınıf listesi: ayrı bir tabloda tutulmak yerine
     * profiller + o güne ait onaylı kayıtlardan canlı olarak türetilir.
     * Böylece "bugün tamamlanan" sayısı her zaman gerçek veriyi gösterir.
     */
    fun getClassmateStats(dateString: String): Flow<List<ClassmateStatEntity>> =
        combine(
            dao.getAllStudentProfiles(),
            dao.getApprovedCountsForDate(dateString),
            dao.getSchedulesForDate(dateString)
        ) { profiles, approvedCounts, schedules ->
            val approvedMap = approvedCounts.associate { it.studentId to it.approvedCount }
            val totalMap = schedules.associate { it.studentId to countCsv(it.taskIdsCsv) }
            profiles
                .map { p ->
                    ClassmateStatEntity(
                        id = p.id,
                        name = p.name,
                        completedToday = approvedMap[p.id] ?: 0,
                        totalToday = (totalMap[p.id] ?: 20).coerceAtLeast(1),
                        diamonds = p.diamonds,
                        streakDays = p.streakDays
                    )
                }
                .sortedByDescending { it.diamonds }
        }.distinctUntilChanged()

    fun getDailyTaskUiItems(dateString: String, studentId: String): Flow<List<DailyTaskUiItem>> {
        return combine(
            dao.getAllTasks(),
            dao.getDailySchedule(dateString, studentId),
            dao.getTaskLogsForDate(dateString, studentId)
        ) { allTaskList, schedule, logsList ->
            if (schedule == null || allTaskList.isEmpty()) {
                emptyList()
            } else {
                val activeIds = schedule.taskIdsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val taskMap = allTaskList.associateBy { it.id }
                val logMap = logsList.associateBy { it.taskId }

                activeIds.mapNotNull { taskId ->
                    val task = taskMap[taskId] ?: return@mapNotNull null
                    val log = logMap[taskId] ?: TaskLogEntity(
                        id = logIdOf(dateString, studentId, taskId),
                        dateString = dateString,
                        studentId = studentId,
                        taskId = taskId,
                        status = "NOT_DONE"
                    )
                    DailyTaskUiItem(task = task, log = log)
                }
            }
        }.distinctUntilChanged()
    }

    // ------------------------------------------------------------------
    // Kurulum
    // ------------------------------------------------------------------

    /**
     * Uygulama açılışında çalışır.
     * - Görev kataloğu ve sınıf ayarı eksikse tamamlanır (her açılışta değil, yalnızca eksikse).
     * - Demo öğrenciler yalnızca ilk kurulumda bir kez yazılır; böylece silinen kayıtlar geri gelmez
     *   ve gerçek öğrencilerin istatistikleri her açılışta sıfırlanmaz.
     */
    suspend fun initialize(seedDemoData: Boolean): Unit = withContext(Dispatchers.IO) {
        val expectedTaskCount = DefaultTasks.ALL_TASKS.size
        if (dao.getTaskCount() != expectedTaskCount) {
            dao.insertTasks(
                DefaultTasks.ALL_TASKS.map { def ->
                    TaskEntity(def.id, def.title, def.categoryId, def.subCategoryId, def.diamondReward)
                }
            )
        }

        if (dao.getClassroomConfigSync() == null) {
            dao.insertClassroomConfig(ClassroomConfigEntity())
        }

        if (dao.getActivityCount() == 0) {
            dao.insertActivities(
                listOf(
                    ActivityEntity(
                        id = "act_default",
                        title = "Haftalık Sorumluluk Hareketi",
                        description = "Ev, okul, dini ve adabı muaşeret alanında düzenli görev takibi.",
                        joinCode = "DEGERLER-5A-2026",
                        startDate = getTodayDateString(),
                        targetDays = 30,
                        isActive = true
                    )
                )
            )
        }

        if (seedDemoData) {
            seedDemoStudents()
        }

        // Kayıtlı her öğrenci için bugünün programı hazır olsun (gün değişince liste boş kalmasın).
        dao.getAllStudentProfilesSync().forEach { profile ->
            ensureScheduleForDate(profile.id, getTodayDateString())
        }
    }

    private suspend fun seedDemoStudents() {
        val today = getTodayDateString()
        val demo = listOf(
            Triple("student_demo_1", "Ahmet Yılmaz", 145),
            Triple("student_demo_2", "Zeynep Kaya", 210)
        )
        demo.forEach { (id, name, diamonds) ->
            if (dao.getStudentProfileSync(id) == null) {
                dao.insertStudentProfile(
                    StudentProfileEntity(
                        id = id,
                        name = name,
                        className = "5-A Sınıfı",
                        diamonds = diamonds,
                        streakDays = 0,
                        monthlyRewardTitle = "Lunapark Gezisi & Kitap Seti",
                        monthlyTargetDiamonds = 300
                    )
                )
                ensureScheduleForDate(id, today)
            }
        }
    }

    /**
     * İlgili gün için program yoksa oluşturur:
     * önce öğrencinin en son listesi, sonra aktif etkinliğin görevleri, olmazsa öğretmenin standart 20 görevi.
     */
    suspend fun ensureScheduleForDate(studentId: String, dateString: String): Unit = withContext(Dispatchers.IO) {
        if (dao.getDailyScheduleSync(dateString, studentId) != null) return@withContext
        val previous = dao.getLatestScheduleBefore(studentId, dateString)
        val active = dao.getActiveActivitySync()
        val csv = when {
            previous != null && previous.taskIdsCsv.isNotBlank() -> previous.taskIdsCsv
            active != null && active.activeTaskIdsCsv.isNotBlank() -> active.activeTaskIdsCsv
            else -> DefaultTasks.DEFAULT_DAILY_20_IDS.joinToString(",")
        }
        dao.insertDailySchedule(
            DailyScheduleEntity(
                dateString = dateString,
                studentId = studentId,
                taskIdsCsv = csv,
                isCustom = previous?.isCustom ?: false,
                sourceTitle = previous?.sourceTitle ?: "Öğretmen Standart Listesi"
            )
        )
    }

    // ------------------------------------------------------------------
    // Günlük döngü
    // ------------------------------------------------------------------

    suspend fun markTaskAsDone(studentId: String, taskId: String, dateString: String): Unit = withContext(Dispatchers.IO) {
        val logId = logIdOf(dateString, studentId, taskId)
        val existing = dao.getTaskLogById(logId)
        if (existing?.status == "APPROVED") return@withContext

        val updated = (existing ?: newLog(logId, dateString, studentId, taskId)).copy(
            status = "STUDENT_DONE",
            isCompletedByStudent = true,
            isApprovedByParent = false,
            submittedTime = System.currentTimeMillis(),
            rejectionReason = null
        )
        dao.insertTaskLog(updated)
        runCatching {
            com.example.data.firebase.FirebaseManager.markTaskAsStudentDone(studentId, taskId, dateString)
        }
    }

    /** Görevi onaylar ve gerçekten kazanılan elmas sayısını döndürür (0 = işlem yapılmadı). */
    suspend fun approveTask(studentId: String, taskId: String, dateString: String): Int = withContext(Dispatchers.IO) {
        val logId = logIdOf(dateString, studentId, taskId)
        val existing = dao.getTaskLogById(logId) ?: return@withContext 0
        if (existing.status == "APPROVED") return@withContext 0

        val reward = dao.getTaskByIdSync(taskId)?.diamondReward ?: 5
        dao.updateTaskLog(
            existing.copy(
                status = "APPROVED",
                isCompletedByStudent = true,
                isApprovedByParent = true,
                approvedTime = System.currentTimeMillis(),
                diamondsAwarded = reward,
                rejectionReason = null
            )
        )
        dao.incrementDiamonds(studentId, reward)
        refreshStreak(studentId, dateString)

        runCatching {
            com.example.data.firebase.FirebaseManager.approveDailyLog(studentId, taskId, dateString, reward)
        }
        reward
    }

    /** Bekleyen tüm görevleri tek turda onaylar. Dönen değer: (görev sayısı, toplam elmas). */
    suspend fun approveAllWaiting(studentId: String, dateString: String): Pair<Int, Int> = withContext(Dispatchers.IO) {
        val waiting = dao.getWaitingApprovalLogsSync(studentId, dateString)
        if (waiting.isEmpty()) return@withContext 0 to 0

        var totalDiamonds = 0
        val now = System.currentTimeMillis()
        val updated = waiting.map { log ->
            val reward = dao.getTaskByIdSync(log.taskId)?.diamondReward ?: 5
            totalDiamonds += reward
            log.copy(
                status = "APPROVED",
                isCompletedByStudent = true,
                isApprovedByParent = true,
                approvedTime = now,
                diamondsAwarded = reward,
                rejectionReason = null
            )
        }
        dao.insertTaskLogs(updated)
        dao.incrementDiamonds(studentId, totalDiamonds)
        refreshStreak(studentId, dateString)

        updated.forEach { log ->
            runCatching {
                com.example.data.firebase.FirebaseManager.approveDailyLog(
                    studentId, log.taskId, dateString, log.diamondsAwarded
                )
            }
        }
        waiting.size to totalDiamonds
    }

    suspend fun rejectTask(studentId: String, taskId: String, dateString: String, reason: String): Unit = withContext(Dispatchers.IO) {
        val logId = logIdOf(dateString, studentId, taskId)
        val existing = dao.getTaskLogById(logId) ?: return@withContext

        // Zaten onaylanmış bir görevin elması geri alınır ki bakiye tutarlı kalsın.
        if (existing.status == "APPROVED" && existing.diamondsAwarded > 0) {
            dao.incrementDiamonds(studentId, -existing.diamondsAwarded)
        }
        dao.updateTaskLog(
            existing.copy(
                status = "REJECTED",
                isCompletedByStudent = false,
                isApprovedByParent = false,
                diamondsAwarded = 0,
                rejectionReason = reason
            )
        )
        refreshStreak(studentId, dateString)
        runCatching {
            com.example.data.firebase.FirebaseManager.rejectDailyLog(studentId, taskId, dateString, reason)
        }
    }

    /**
     * Kesintisiz gün serisini gerçek kayıtlardan hesaplar.
     * Bugün henüz tamamlanmadıysa seri bozulmuş sayılmaz, dünden geriye doğru bakılır.
     */
    private suspend fun refreshStreak(studentId: String, dateString: String) {
        val approvedByDate = dao.getApprovedCountsByDate(studentId)
            .associate { it.dateString to it.approvedCount }
        val sizeByDate = dao.getScheduleSizesForStudent(studentId)
            .associate { it.dateString to countCsv(it.taskIdsCsv) }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()
        var streak = 0

        for (dayOffset in 0 until 400) {
            val day = sdf.format(calendar.time)
            val approved = approvedByDate[day] ?: 0
            val total = (sizeByDate[day] ?: 20).coerceAtLeast(1)
            val required = max(1, ceil(total * STREAK_SUCCESS_RATIO).toInt())

            if (approved >= required) {
                streak++
            } else if (dayOffset > 0) {
                // Bugün hariç, hedefi tutmayan ilk gün seriyi bitirir.
                break
            }
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        dao.updateStreak(studentId, streak, dateString)
    }

    suspend fun syncDailyLogFromFirebase(dailyLog: com.example.data.firebase.DailyLog): Unit = withContext(Dispatchers.IO) {
        if (dailyLog.id.isBlank() || dailyLog.studentUid.isBlank()) return@withContext
        val existing = dao.getTaskLogById(dailyLog.id)
        // Yerel kayıt daha yeniyse buluttan gelen eski veriyle ezilmesin.
        if (existing != null && existing.approvedTime > dailyLog.approvedAt && existing.status == "APPROVED") {
            return@withContext
        }
        val updated = (existing ?: newLog(dailyLog.id, dailyLog.date, dailyLog.studentUid, dailyLog.taskId)).copy(
            status = dailyLog.status,
            isCompletedByStudent = dailyLog.isCompletedByStudent,
            isApprovedByParent = dailyLog.isApprovedByParent,
            submittedTime = if (dailyLog.submittedAt > 0) dailyLog.submittedAt else existing?.submittedTime ?: 0L,
            approvedTime = dailyLog.approvedAt,
            diamondsAwarded = dailyLog.diamondsAwarded,
            rejectionReason = dailyLog.rejectionReason
        )
        dao.insertTaskLog(updated)
    }

    // ------------------------------------------------------------------
    // Veli görev listesi
    // ------------------------------------------------------------------

    suspend fun saveCustomSchedule(
        studentId: String,
        dateString: String,
        selectedIds: List<String>
    ): Boolean = withContext(Dispatchers.IO) {
        if (selectedIds.size != 20) return@withContext false
        dao.insertDailySchedule(
            DailyScheduleEntity(
                dateString = dateString,
                studentId = studentId,
                taskIdsCsv = selectedIds.joinToString(","),
                isCustom = true,
                sourceTitle = "Veli Özel Listesi"
            )
        )
        runCatching {
            com.example.data.firebase.FirebaseManager.saveUserTaskPrefs(studentId, selectedIds, isCustom = true)
        }
        true
    }

    suspend fun resetToTeacherSchedule(studentId: String, dateString: String): Unit = withContext(Dispatchers.IO) {
        val active = dao.getActiveActivitySync()
        val csv = if (active != null && active.activeTaskIdsCsv.isNotBlank()) {
            active.activeTaskIdsCsv
        } else {
            DefaultTasks.DEFAULT_DAILY_20_IDS.joinToString(",")
        }
        dao.insertDailySchedule(
            DailyScheduleEntity(
                dateString = dateString,
                studentId = studentId,
                taskIdsCsv = csv,
                isCustom = false,
                sourceTitle = "Öğretmen Standart Listesi"
            )
        )
        runCatching {
            com.example.data.firebase.FirebaseManager.saveUserTaskPrefs(
                studentId, csv.split(",").filter { it.isNotBlank() }, isCustom = false
            )
        }
    }

    suspend fun updateMonthlyReward(studentId: String, title: String, targetDiamonds: Int) = withContext(Dispatchers.IO) {
        dao.updateMonthlyReward(studentId, title, targetDiamonds.coerceAtLeast(1))
    }

    suspend fun claimMonthlyReward(studentId: String) = withContext(Dispatchers.IO) {
        dao.updateRewardClaim(studentId, true, getCurrentMonthKey())
    }

    // ------------------------------------------------------------------
    // Öğretmen işlemleri
    // ------------------------------------------------------------------

    suspend fun updateDailyAnnouncement(dailyAnnouncement: String): Unit = withContext(Dispatchers.IO) {
        val current = dao.getClassroomConfigSync() ?: ClassroomConfigEntity()
        if (current.dailyAnnouncement == dailyAnnouncement) return@withContext
        dao.insertClassroomConfig(current.copy(dailyAnnouncement = dailyAnnouncement))
    }

    suspend fun updateClassroomConfig(
        className: String,
        subtitle: String,
        activityName: String,
        joinCode: String,
        teacherPin: String,
        dailyAnnouncement: String
    ): Unit = withContext(Dispatchers.IO) {
        val current = dao.getClassroomConfigSync() ?: ClassroomConfigEntity()
        dao.insertClassroomConfig(
            current.copy(
                className = className.ifBlank { current.className },
                subtitle = subtitle,
                activityName = activityName.ifBlank { current.activityName },
                joinCode = joinCode.ifBlank { current.joinCode },
                teacherPin = teacherPin.ifBlank { current.teacherPin },
                dailyAnnouncement = dailyAnnouncement.ifBlank { current.dailyAnnouncement }
            )
        )
    }

    suspend fun addActivity(
        title: String,
        description: String,
        joinCode: String,
        targetDays: Int = 30
    ): ActivityEntity = withContext(Dispatchers.IO) {
        val activity = ActivityEntity(
            id = "act_${System.currentTimeMillis()}",
            title = title.trim(),
            description = description.trim(),
            joinCode = joinCode.trim().uppercase(TR),
            startDate = getTodayDateString(),
            targetDays = targetDays.coerceIn(1, 365),
            isActive = false
        )
        dao.insertActivity(activity)
        activity
    }

    suspend fun saveActivityTaskSettings(
        activityId: String,
        selectedSystemTaskIds: List<String>,
        customTasks: List<ActivityCustomTask>
    ): Unit = withContext(Dispatchers.IO) {
        val activity = dao.getActivityById(activityId) ?: return@withContext

        val customTaskEntities = customTasks
            .filter { it.title.isNotBlank() }
            .map { ct ->
                TaskEntity(
                    id = "custom_act_${activityId}_${ct.slotIndex}",
                    title = ct.title.trim(),
                    categoryId = "activity_custom",
                    subCategoryId = activityId,
                    diamondReward = ct.diamondReward.coerceIn(1, 100)
                )
            }
        if (customTaskEntities.isNotEmpty()) dao.insertTasks(customTaskEntities)

        val activeCustomIds = customTasks
            .filter { it.isActive && it.title.isNotBlank() }
            .map { "custom_act_${activityId}_${it.slotIndex}" }

        dao.insertActivity(
            activity.copy(
                customTasksJson = ActivityCustomTaskHelper.toJson(customTasks),
                activeTaskIdsCsv = (selectedSystemTaskIds + activeCustomIds).distinct().joinToString(",")
            )
        )
    }

    suspend fun assignTasksToStudents(
        studentIds: List<String>,
        taskIds: List<String>,
        dateString: String,
        activityTitle: String
    ): Unit = withContext(Dispatchers.IO) {
        if (taskIds.isEmpty() || studentIds.isEmpty()) return@withContext
        val csv = taskIds.distinct().joinToString(",")
        dao.insertDailySchedules(
            studentIds.map { studentId ->
                DailyScheduleEntity(
                    dateString = dateString,
                    studentId = studentId,
                    taskIdsCsv = csv,
                    isCustom = true,
                    sourceTitle = activityTitle
                )
            }
        )
    }

    suspend fun completeTaskForStudents(
        studentIds: List<String>,
        taskId: String,
        dateString: String,
        rewardDiamonds: Int = 5
    ): Unit = withContext(Dispatchers.IO) {
        if (studentIds.isEmpty()) return@withContext
        val now = System.currentTimeMillis()
        val reward = rewardDiamonds.coerceIn(1, 100)
        val toInsert = ArrayList<TaskLogEntity>(studentIds.size)
        val newlyApproved = ArrayList<String>(studentIds.size)

        studentIds.distinct().forEach { studentId ->
            val logId = logIdOf(dateString, studentId, taskId)
            val existing = dao.getTaskLogById(logId)
            if (existing?.status == "APPROVED") return@forEach
            toInsert += (existing ?: newLog(logId, dateString, studentId, taskId)).copy(
                status = "APPROVED",
                isCompletedByStudent = true,
                isApprovedByParent = true,
                approvedTime = now,
                submittedTime = if ((existing?.submittedTime ?: 0L) > 0L) existing!!.submittedTime else now,
                diamondsAwarded = reward
            )
            newlyApproved += studentId
        }
        if (toInsert.isEmpty()) return@withContext

        dao.insertTaskLogs(toInsert)
        newlyApproved.forEach { studentId ->
            dao.incrementDiamonds(studentId, reward)
            refreshStreak(studentId, dateString)
            runCatching {
                com.example.data.firebase.FirebaseManager.approveDailyLog(studentId, taskId, dateString, reward)
            }
        }
    }

    suspend fun setActiveActivity(activityId: String): Unit = withContext(Dispatchers.IO) {
        dao.setActiveActivity(activityId)
        val active = dao.getActiveActivitySync() ?: return@withContext
        val current = dao.getClassroomConfigSync() ?: ClassroomConfigEntity()
        dao.insertClassroomConfig(current.copy(activityName = active.title, joinCode = active.joinCode))
    }

    suspend fun deleteActivity(activityId: String) = withContext(Dispatchers.IO) {
        dao.deleteActivity(activityId)
    }

    // ------------------------------------------------------------------
    // Kayıt
    // ------------------------------------------------------------------

    suspend fun registerUser(
        role: String,
        fullName: String,
        linkedStudentName: String?,
        phoneOrContact: String?,
        classId: String = "default_class",
        activityName: String = "Haftalık Sorumluluk Hareketi"
    ): Result<RegisteredUserEntity> = withContext(Dispatchers.IO) {
        val cleanName = fullName.trim()
        if (cleanName.length < 3) {
            return@withContext Result.failure(IllegalArgumentException("Lütfen ad ve soyadınızı eksiksiz giriniz."))
        }

        val today = getTodayDateString()
        val config = dao.getClassroomConfigSync()
        var resolvedStudentId: String? = null
        var resolvedStudentName: String? = null

        when (role) {
            "PARENT" -> {
                if (linkedStudentName.isNullOrBlank()) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Veli kaydı için bağlı olacağınız öğrencinin ad ve soyadını girmelisiniz.")
                    )
                }
                val target = normalizeName(linkedStudentName)
                val match = dao.getAllStudentProfilesSync().firstOrNull { normalizeName(it.name) == target }
                    ?: return@withContext Result.failure(
                        IllegalArgumentException(
                            "❌ Doğrulama başarısız: '$linkedStudentName' isimli bir öğrenci bu sınıfta kayıtlı değil. " +
                                "Öğrencinin önce sınıfa katıldığından ve adı doğru yazdığınızdan emin olun."
                        )
                    )
                resolvedStudentId = match.id
                resolvedStudentName = match.name
            }

            "STUDENT" -> {
                val target = normalizeName(cleanName)
                val existing = dao.getAllStudentProfilesSync().firstOrNull { normalizeName(it.name) == target }
                if (existing != null) {
                    // Aynı isimle ikinci bir profil açılmaz, mevcut profile bağlanır.
                    resolvedStudentId = existing.id
                    resolvedStudentName = existing.name
                } else {
                    val newStudentId = "student_${System.currentTimeMillis()}"
                    dao.insertStudentProfile(
                        StudentProfileEntity(
                            id = newStudentId,
                            name = cleanName,
                            className = config?.className ?: "Sınıf",
                            diamonds = 0,
                            streakDays = 0,
                            monthlyRewardTitle = "Aylık Sürpriz Ödül",
                            monthlyTargetDiamonds = 300
                        )
                    )
                    ensureScheduleForDate(newStudentId, today)
                    resolvedStudentId = newStudentId
                    resolvedStudentName = cleanName
                }
            }
        }

        val user = RegisteredUserEntity(
            id = "reg_${System.currentTimeMillis()}",
            role = role,
            fullName = cleanName,
            linkedStudentId = resolvedStudentId,
            linkedStudentName = resolvedStudentName ?: linkedStudentName?.trim(),
            phoneOrContact = phoneOrContact?.trim(),
            classId = classId,
            activityName = activityName,
            registeredDate = today
        )
        dao.insertRegisteredUser(user)
        Result.success(user)
    }

    suspend fun deleteRegisteredUser(userId: String) = withContext(Dispatchers.IO) {
        dao.deleteRegisteredUser(userId)
    }

    /** Öğrenciyi profili, programı, kayıtları ve bağlı veli kayıtlarıyla birlikte tamamen siler. */
    suspend fun deleteStudent(studentId: String) = withContext(Dispatchers.IO) {
        dao.purgeStudent(studentId)
    }

    // ------------------------------------------------------------------

    private fun newLog(logId: String, date: String, studentId: String, taskId: String) = TaskLogEntity(
        id = logId,
        dateString = date,
        studentId = studentId,
        taskId = taskId,
        status = "NOT_DONE"
    )

    private fun logIdOf(date: String, studentId: String, taskId: String) = "${date}_${studentId}_$taskId"

    private fun countCsv(csv: String): Int =
        csv.split(",").count { it.isNotBlank() }
}
