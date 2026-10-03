package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ActivityCustomTask
import com.example.data.ActivityEntity
import com.example.data.AppDatabase
import com.example.data.AppPreferences
import com.example.data.Badge
import com.example.data.BadgeCatalog
import com.example.data.ClassmateStatEntity
import com.example.data.ClassroomConfigEntity
import com.example.data.DailyScheduleEntity
import com.example.data.DailyTaskUiItem
import com.example.data.DefaultTasks
import com.example.data.RegisteredUserEntity
import com.example.data.ResponsibilityRepository
import com.example.data.StudentProfileEntity
import com.example.data.TaskCategory
import com.example.data.TaskEntity
import com.example.data.TaskLogEntity
import com.example.data.firebase.FirebaseManager
import com.example.ui.theme.AppFontOption
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UserRole(val titleTr: String, val subtitleTr: String) {
    STUDENT("Öğrenci", "Görevleri Tamamla & Elmas Kazan"),
    PARENT("Veli (Ebeveyn)", "Görevleri Onayla & Özel Liste Belirle"),
    TEACHER("Öğretmen (Eğitici)", "Sınıf Raporları & Standart Liste")
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ResponsibilityRepository =
        ResponsibilityRepository(AppDatabase.getInstance(application).responsibilityDao())

    private val appPrefs = AppPreferences.getInstance(application)

    /** Gün değişimini yakalar; uygulama gece açık kalsa bile liste ertesi güne geçer. */
    private val _todayDate = MutableStateFlow(ResponsibilityRepository.getTodayDateString())
    val todayDate: StateFlow<String> = _todayDate.asStateFlow()
    val todayDateString: String get() = _todayDate.value

    private val _isSessionActive = MutableStateFlow(appPrefs.registeredRole != null)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _currentRole = MutableStateFlow(appPrefs.registeredRole ?: UserRole.STUDENT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _activeUserName = MutableStateFlow(appPrefs.userName)
    val activeUserName: StateFlow<String> = _activeUserName.asStateFlow()

    private val _activeFontOption = MutableStateFlow(AppFontOption.fromId(appPrefs.fontOptionId))
    val activeFontOption: StateFlow<AppFontOption> = _activeFontOption.asStateFlow()

    private val _activeFontScale = MutableStateFlow(appPrefs.fontScale)
    val activeFontScale: StateFlow<Float> = _activeFontScale.asStateFlow()

    private val _dontShowIntroAgain = MutableStateFlow(appPrefs.dontShowIntroAgain)
    val dontShowIntroAgain: StateFlow<Boolean> = _dontShowIntroAgain.asStateFlow()

    private val _selectedChildId = MutableStateFlow(appPrefs.activeStudentId ?: "")
    val selectedChildId: StateFlow<String> = _selectedChildId.asStateFlow()

    val currentStudentId: String get() = _selectedChildId.value

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    private val _isCustomizingSchedule = MutableStateFlow(false)
    val isCustomizingSchedule: StateFlow<Boolean> = _isCustomizingSchedule.asStateFlow()

    private val _customSelectedTaskIds = MutableStateFlow<Set<String>>(emptySet())
    val customSelectedTaskIds: StateFlow<Set<String>> = _customSelectedTaskIds.asStateFlow()

    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    private var childLogsListener: ListenerRegistration? = null
    private var announcementListener: ListenerRegistration? = null

    /** Öğrenci + gün ikilisi; her ikisi de değişebildiği için akışlar bu anahtara bağlandı. */
    private val childAndDate: StateFlow<Pair<String, String>> =
        combine(_selectedChildId, _todayDate) { child, date -> child to date }
            .distinctUntilChanged()
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                _selectedChildId.value to _todayDate.value
            )

    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val classroomConfig: StateFlow<ClassroomConfigEntity?> = repository.classroomConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val registeredUsers: StateFlow<List<RegisteredUserEntity>> = repository.registeredUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allActivities: StateFlow<List<ActivityEntity>> = repository.allActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allStudentProfiles: StateFlow<List<StudentProfileEntity>> = repository.getAllStudentProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val classmateStats: StateFlow<List<ClassmateStatEntity>> = _todayDate
        .flatMapLatest { date -> repository.getClassmateStats(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val studentProfile: StateFlow<StudentProfileEntity?> = _selectedChildId
        .flatMapLatest { childId -> repository.getStudentProfile(childId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val dailyTasks: StateFlow<List<DailyTaskUiItem>> = childAndDate
        .flatMapLatest { (childId, date) -> repository.getDailyTaskUiItems(date, childId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val dailySchedule: StateFlow<DailyScheduleEntity?> = childAndDate
        .flatMapLatest { (childId, date) -> repository.getDailySchedule(date, childId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val waitingApprovalLogs: StateFlow<List<TaskLogEntity>> = _selectedChildId
        .flatMapLatest { childId -> repository.getWaitingApprovalLogs(childId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val badges: StateFlow<List<Badge>> = combine(studentProfile, dailyTasks) { profile, tasks ->
        BadgeCatalog.getBadges(
            diamonds = profile?.diamonds ?: 0,
            streakDays = profile?.streakDays ?: 0,
            approvedTaskCount = tasks.count { it.log.status == "APPROVED" },
            approvedReligionCount = tasks.count { it.task.categoryId == "religion" && it.log.status == "APPROVED" }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            repository.initialize(seedDemoData = !appPrefs.isDemoSeeded)
            appPrefs.isDemoSeeded = true

            // Oturum açık ama profil bağlanmamışsa (eski sürümden gelen kurulum) toparla.
            if (_selectedChildId.value.isBlank()) {
                resolveStudentIdForCurrentSession()
            }
            ensureTodaySchedule()

            // Firestore kuralları oturum şartı aradığı için önce anonim giriş yapılır;
            // Firebase yapılandırılmamışsa uygulama tamamen yerel çalışmaya devam eder.
            FirebaseManager.ensureSignedIn()
            startChildLogsListener(_selectedChildId.value)
            announcementListener = runCatching {
                FirebaseManager.listenToDailyAnnouncement { announcement ->
                    if (announcement.isNotBlank()) {
                        viewModelScope.launch { repository.updateDailyAnnouncement(announcement) }
                    }
                }
            }.getOrNull()
        }

        // Gün değişimi kontrolü.
        viewModelScope.launch {
            while (true) {
                delay(60_000)
                refreshToday()
            }
        }

    }

    /** Uygulama ön plana geldiğinde çağrılır. */
    fun refreshToday() {
        val today = ResponsibilityRepository.getTodayDateString()
        if (today != _todayDate.value) {
            _todayDate.value = today
            viewModelScope.launch {
                ensureTodaySchedule()
                startChildLogsListener(_selectedChildId.value)
            }
        }
    }

    private suspend fun ensureTodaySchedule() {
        val childId = _selectedChildId.value
        if (childId.isNotBlank()) {
            repository.ensureScheduleForDate(childId, _todayDate.value)
        }
    }

    /**
     * Eski kurulumlardan gelen ya da profili çözülememiş oturumlarda,
     * kayıtlı isimden öğrenci profilini bulur. Öğretmen rolünde profil bağlanmaz.
     */
    private suspend fun resolveStudentIdForCurrentSession() {
        if (_currentRole.value == UserRole.TEACHER) return
        val target = when (_currentRole.value) {
            UserRole.PARENT -> appPrefs.linkedStudentName
            else -> appPrefs.userName
        }
        if (target.isBlank()) return
        val profiles = repository.getAllStudentProfiles().first()
        val match = profiles.firstOrNull {
            ResponsibilityRepository.normalizeName(it.name) == ResponsibilityRepository.normalizeName(target)
        }
        if (match != null) setActiveStudent(match.id)
    }

    private fun setActiveStudent(studentId: String) {
        _selectedChildId.value = studentId
        appPrefs.activeStudentId = studentId
    }

    private fun startChildLogsListener(childId: String) {
        childLogsListener?.remove()
        childLogsListener = null
        if (childId.isBlank() || !FirebaseManager.isAvailable()) return
        childLogsListener = runCatching {
            FirebaseManager.listenToStudentDailyLogs(childId, _todayDate.value) { logs ->
                viewModelScope.launch {
                    logs.forEach { repository.syncDailyLogFromFirebase(it) }
                }
            }
        }.getOrNull()
    }

    fun selectChild(childId: String) {
        if (_selectedChildId.value == childId) return
        setActiveStudent(childId)
        _isCustomizingSchedule.value = false
        viewModelScope.launch {
            repository.ensureScheduleForDate(childId, _todayDate.value)
            startChildLogsListener(childId)
        }
        _notificationMessage.value = "Yönetilen öğrenci değiştirildi."
    }

    fun setRole(role: UserRole) { _currentRole.value = role }

    fun setCategoryFilter(catId: String?) { _selectedCategoryFilter.value = catId }

    fun clearNotification() { _notificationMessage.value = null }

    // ---------------- Öğrenci ----------------

    fun markTaskAsDone(taskId: String) {
        val studentId = currentStudentId
        if (studentId.isBlank()) {
            _notificationMessage.value = "Öğrenci profili bulunamadı. Lütfen tekrar giriş yapınız."
            return
        }
        viewModelScope.launch {
            repository.markTaskAsDone(studentId, taskId, _todayDate.value)
            _notificationMessage.value = "Görev tamamlandı olarak işaretlendi, veli onayına gönderildi."
        }
    }

    fun claimMonthlyReward() {
        val studentId = currentStudentId.ifBlank { return }
        viewModelScope.launch { repository.claimMonthlyReward(studentId) }
    }

    // ---------------- Veli ----------------

    fun approveTask(taskId: String) {
        val studentId = currentStudentId.ifBlank { return }
        viewModelScope.launch {
            val awarded = repository.approveTask(studentId, taskId, _todayDate.value)
            _notificationMessage.value = if (awarded > 0) {
                "Görev onaylandı! +$awarded 💎 eklendi."
            } else {
                "Bu görev zaten onaylanmış."
            }
            if (awarded > 0) checkAndTriggerAutomaticBadges(studentId)
        }
    }

    fun approveAllWaitingTasks() {
        val studentId = currentStudentId.ifBlank { return }
        if (_isBusy.value) return
        viewModelScope.launch {
            _isBusy.value = true
            val (count, diamonds) = repository.approveAllWaiting(studentId, _todayDate.value)
            _isBusy.value = false
            _notificationMessage.value = if (count == 0) {
                "Onay bekleyen görev bulunmuyor."
            } else {
                "$count görev onaylandı! (+$diamonds 💎)"
            }
            if (count > 0) checkAndTriggerAutomaticBadges(studentId)
        }
    }

    fun rejectTask(taskId: String, reason: String = "Lütfen görevi eksiksiz tamamlayıp tekrar gönder.") {
        val studentId = currentStudentId.ifBlank { return }
        viewModelScope.launch {
            repository.rejectTask(studentId, taskId, _todayDate.value, reason)
            _notificationMessage.value = "Görev öğrenciye geri gönderildi."
        }
    }

    private fun checkAndTriggerAutomaticBadges(studentId: String) {
        viewModelScope.launch {
            runCatching {
                val profile = studentProfile.value
                val tasks = dailyTasks.value
                val diamonds = profile?.diamonds ?: 0
                val streak = profile?.streakDays ?: 0
                val approvedTasks = tasks.count { it.log.status == "APPROVED" }
                val approvedReligion = tasks.count { it.task.categoryId == "religion" && it.log.status == "APPROVED" }

                if (streak >= 7) {
                    FirebaseManager.awardBadgeToStudent(studentId, "consistency_star", "İstikrar Yıldızı")
                }
                if (diamonds >= 100) {
                    FirebaseManager.awardBadgeToStudent(studentId, "diamond_hunter", "Elmas Avcısı")
                }
                if (approvedReligion >= 5 || (streak >= 5 && approvedTasks >= 10)) {
                    FirebaseManager.awardBadgeToStudent(studentId, "worship_hero", "İbadet Kahramanı")
                }
            }
        }
    }

    fun startCustomizingSchedule() {
        val currentIds = dailySchedule.value?.taskIdsCsv
            ?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet()
            ?: DefaultTasks.DEFAULT_DAILY_20_IDS.toSet()
        _customSelectedTaskIds.value = currentIds
        _isCustomizingSchedule.value = true
    }

    fun cancelCustomizingSchedule() { _isCustomizingSchedule.value = false }

    fun toggleCustomTaskSelection(task: TaskEntity): Boolean {
        val current = _customSelectedTaskIds.value
        if (current.contains(task.id)) {
            _customSelectedTaskIds.value = current - task.id
            return true
        }
        if (current.size >= 20) {
            _notificationMessage.value = "Günlük toplam en fazla 20 görev seçilebilir!"
            return false
        }
        val taskMap = allTasks.value.associateBy { it.id }
        val sameCategoryCount = current.count { taskMap[it]?.categoryId == task.categoryId }
        if (sameCategoryCount >= 5) {
            _notificationMessage.value =
                "${TaskCategory.fromId(task.categoryId).titleTr} kategorisinden en fazla 5 görev seçebilirsiniz!"
            return false
        }
        _customSelectedTaskIds.value = current + task.id
        return true
    }

    fun saveCustomSchedule(): Boolean {
        val selected = _customSelectedTaskIds.value.toList()
        if (selected.size != 20) {
            _notificationMessage.value = "Toplam 20 görev seçmelisiniz! (Şu an: ${selected.size})"
            return false
        }
        val taskMap = allTasks.value.associateBy { it.id }
        val counts = selected.mapNotNull { taskMap[it]?.categoryId }.groupingBy { it }.eachCount()
        val home = counts["home"] ?: 0
        val school = counts["school"] ?: 0
        val religion = counts["religion"] ?: 0
        val etiquette = counts["etiquette"] ?: 0
        if (home != 5 || school != 5 || religion != 5 || etiquette != 5) {
            _notificationMessage.value =
                "Her kategoriden tam 5 görev seçilmeli! (Ev: $home/5, Okul: $school/5, Dini: $religion/5, Adap: $etiquette/5)"
            return false
        }

        val studentId = currentStudentId.ifBlank { return false }
        viewModelScope.launch {
            if (repository.saveCustomSchedule(studentId, _todayDate.value, selected)) {
                _isCustomizingSchedule.value = false
                _notificationMessage.value = "Bugünün 20 görevi kaydedildi. (Veli Özel Listesi)"
            } else {
                _notificationMessage.value = "Liste kaydedilemedi, lütfen tekrar deneyiniz."
            }
        }
        return true
    }

    fun resetToTeacherSchedule() {
        val studentId = currentStudentId.ifBlank { return }
        viewModelScope.launch {
            repository.resetToTeacherSchedule(studentId, _todayDate.value)
            _isCustomizingSchedule.value = false
            _notificationMessage.value = "Öğretmenin standart listesine dönüldü."
        }
    }

    fun updateMonthlyReward(title: String, targetDiamonds: Int) {
        val studentId = currentStudentId.ifBlank { return }
        if (title.isBlank() || targetDiamonds <= 0) {
            _notificationMessage.value = "Ödül adı ve hedef elmas sayısı geçerli olmalıdır."
            return
        }
        viewModelScope.launch {
            repository.updateMonthlyReward(studentId, title.trim(), targetDiamonds)
            _notificationMessage.value = "Aylık ödül ve hedef güncellendi."
        }
    }

    // ---------------- Rol / oturum ----------------

    fun verifyTeacherPin(inputPin: String): Boolean {
        val actualPin = classroomConfig.value?.teacherPin ?: "1234"
        return inputPin.trim() == actualPin.trim()
    }

    fun switchRoleSafely(role: UserRole) { _currentRole.value = role }

    fun registerAndStartRole(role: UserRole, fullName: String, linkedStudentName: String?) {
        if (_isBusy.value) return
        viewModelScope.launch {
            _isBusy.value = true
            val roleStr = role.name
            val config = classroomConfig.value
            val result = repository.registerUser(
                role = roleStr,
                fullName = fullName,
                linkedStudentName = linkedStudentName,
                phoneOrContact = null,
                classId = config?.id ?: "default_class",
                activityName = config?.activityName ?: "Haftalık Sorumluluk Hareketi"
            )
            _isBusy.value = false

            result.onSuccess { user ->
                // Oturum ancak doğrulama başarılıysa açılır.
                appPrefs.registeredRole = role
                appPrefs.userName = user.fullName
                appPrefs.linkedStudentName = linkedStudentName ?: ""
                user.linkedStudentId?.let { setActiveStudent(it) }

                _currentRole.value = role
                _activeUserName.value = user.fullName
                _isSessionActive.value = true

                _selectedChildId.value.takeIf { it.isNotBlank() }?.let { childId ->
                    repository.ensureScheduleForDate(childId, _todayDate.value)
                    startChildLogsListener(childId)
                }
                _notificationMessage.value = "✓ Hoş geldiniz! ${role.titleTr} modu aktif."
            }.onFailure { ex ->
                _notificationMessage.value = ex.message ?: "Kayıt tamamlanamadı."
            }
        }
    }

    fun switchRole(newRole: UserRole) {
        _currentRole.value = newRole
        appPrefs.registeredRole = newRole
        _notificationMessage.value = "✓ ${newRole.titleTr} görünümüne geçildi."
    }

    fun logoutOrSwitchRole() {
        childLogsListener?.remove()
        childLogsListener = null
        appPrefs.clearSession()
        _selectedChildId.value = ""
        _activeUserName.value = ""
        _isSessionActive.value = false
        _isCustomizingSchedule.value = false
        _notificationMessage.value = "Oturum kapatıldı. Lütfen yeni giriş rolünüzü seçiniz."
    }

    fun updateTypographySettings(fontOption: AppFontOption, fontScale: Float) {
        val scale = fontScale.coerceIn(0.85f, 1.35f)
        appPrefs.fontOptionId = fontOption.id
        appPrefs.fontScale = scale
        _activeFontOption.value = fontOption
        _activeFontScale.value = scale
        _notificationMessage.value = "✓ Yazı tipi ve görünüm ayarları güncellendi."
    }

    fun setDontShowIntroAgain(value: Boolean) {
        appPrefs.dontShowIntroAgain = value
        _dontShowIntroAgain.value = value
        if (value) {
            _notificationMessage.value = "✓ Tanıtım kapatıldı. Uygulama açılışında bir daha gösterilmeyecek."
        } else {
            _notificationMessage.value = "Tanıtım açıldı. Uygulama açılışında gösterilecek."
        }
    }

    fun toggleDontShowIntroAgain() {
        setDontShowIntroAgain(!_dontShowIntroAgain.value)
    }

    fun trySwitchToTeacher(inputPin: String): Boolean {
        return if (verifyTeacherPin(inputPin)) {
            _currentRole.value = UserRole.TEACHER
            _notificationMessage.value = "Öğretmen yetkilendirmesi başarılı."
            true
        } else {
            _notificationMessage.value = "Hatalı öğretmen PIN kodu!"
            false
        }
    }

    // ---------------- Öğretmen ----------------

    private fun requireTeacher(action: String): Boolean {
        if (_currentRole.value != UserRole.TEACHER) {
            _notificationMessage.value = "Yetkisiz işlem! $action sadece öğretmen tarafından yapılabilir."
            return false
        }
        return true
    }

    fun updateClassroomInfo(
        className: String,
        subtitle: String,
        activityName: String,
        joinCode: String,
        teacherPin: String,
        dailyAnnouncement: String = ""
    ) {
        if (!requireTeacher("Sınıf bilgisi düzenleme")) return
        if (teacherPin.isNotBlank() && !teacherPin.trim().matches(Regex("\\d{4,8}"))) {
            _notificationMessage.value = "PIN kodu 4-8 haneli rakamlardan oluşmalıdır."
            return
        }
        viewModelScope.launch {
            repository.updateClassroomConfig(
                className = className.trim(),
                subtitle = subtitle.trim(),
                activityName = activityName.trim(),
                joinCode = joinCode.trim(),
                teacherPin = teacherPin.trim(),
                dailyAnnouncement = dailyAnnouncement.trim()
            )
            _notificationMessage.value = "Sınıf bilgileri güncellendi."
        }
    }

    fun addActivity(
        title: String,
        description: String,
        joinCode: String,
        targetDays: Int = 30,
        onCreated: ((ActivityEntity) -> Unit)? = null
    ) {
        if (!requireTeacher("Etkinlik ekleme")) return
        if (title.isBlank()) {
            _notificationMessage.value = "Etkinlik adı boş olamaz."
            return
        }
        viewModelScope.launch {
            val act = repository.addActivity(title, description, joinCode, targetDays)
            _notificationMessage.value = "Yeni etkinlik eklendi: ${act.title}"
            onCreated?.invoke(act)
        }
    }

    fun saveActivityTaskSettings(
        activityId: String,
        activityTitle: String,
        selectedSystemTaskIds: List<String>,
        customTasks: List<ActivityCustomTask>,
        onSuccess: () -> Unit = {}
    ) {
        if (!requireTeacher("Görev ayarları")) return
        viewModelScope.launch {
            repository.saveActivityTaskSettings(activityId, selectedSystemTaskIds, customTasks)
            _notificationMessage.value = "✓ '$activityTitle' görev ayarları kaydedildi."
            onSuccess()
        }
    }

    fun assignTasksToStudents(
        studentIds: List<String>,
        taskIds: List<String>,
        activityTitle: String,
        groupName: String = "Seçilen Öğrenciler",
        onSuccess: () -> Unit = {}
    ) {
        if (!requireTeacher("Görev atama")) return
        if (studentIds.isEmpty() || taskIds.isEmpty()) {
            _notificationMessage.value = "Lütfen en az bir öğrenci ve bir görev seçiniz."
            return
        }
        viewModelScope.launch {
            repository.assignTasksToStudents(studentIds, taskIds, _todayDate.value, activityTitle)
            _notificationMessage.value = "✓ '$activityTitle' görevleri ${studentIds.size} öğrenciye atandı. ($groupName)"
            onSuccess()
        }
    }

    fun completeTaskForStudents(
        studentIds: List<String>,
        taskId: String,
        taskTitle: String,
        rewardDiamonds: Int = 5,
        onSuccess: () -> Unit = {}
    ) {
        if (!requireTeacher("Toplu tamamlama")) return
        if (studentIds.isEmpty()) {
            _notificationMessage.value = "Lütfen en az bir öğrenci seçiniz."
            return
        }
        viewModelScope.launch {
            repository.completeTaskForStudents(studentIds, taskId, _todayDate.value, rewardDiamonds)
            _notificationMessage.value =
                "✓ '$taskTitle' ${studentIds.size} öğrenci için tamamlandı. (+$rewardDiamonds 💎)"
            onSuccess()
        }
    }

    fun setActiveActivity(activityId: String, activityTitle: String) {
        if (!requireTeacher("Aktif etkinlik değiştirme")) return
        viewModelScope.launch {
            repository.setActiveActivity(activityId)
            _notificationMessage.value = "Aktif etkinlik: $activityTitle"
        }
    }

    fun deleteActivity(activityId: String, activityTitle: String) {
        if (!requireTeacher("Etkinlik silme")) return
        viewModelScope.launch {
            repository.deleteActivity(activityId)
            _notificationMessage.value = "$activityTitle etkinliği silindi."
        }
    }

    fun registerViaQr(
        role: String,
        fullName: String,
        linkedStudentName: String?,
        phone: String?,
        classId: String,
        activityName: String,
        onComplete: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(
                role = role,
                fullName = fullName,
                linkedStudentName = linkedStudentName,
                phoneOrContact = phone,
                classId = classId,
                activityName = activityName
            )
            result.onSuccess { user ->
                val msg = if (role == "PARENT") {
                    "✓ ${user.linkedStudentName} isimli öğrencinin velisi olarak doğrulandınız."
                } else {
                    "✓ ${user.fullName} sınıfa ve '$activityName' etkinliğine katıldı."
                }
                _notificationMessage.value = msg
                onComplete(true, msg)
            }.onFailure { ex ->
                val msg = ex.message ?: "Kayıt işlemi gerçekleştirilemedi."
                _notificationMessage.value = msg
                onComplete(false, msg)
            }
        }
    }

    fun deleteRegisteredUser(userId: String) {
        if (!requireTeacher("Kayıt silme")) return
        viewModelScope.launch {
            repository.deleteRegisteredUser(userId)
            _notificationMessage.value = "Kayıt silindi."
        }
    }

    fun deleteClassmate(studentId: String, studentName: String) {
        if (!requireTeacher("Öğrenci silme")) return
        viewModelScope.launch {
            repository.deleteStudent(studentId)
            if (_selectedChildId.value == studentId) {
                _selectedChildId.value = ""
                appPrefs.activeStudentId = null
            }
            _notificationMessage.value = "$studentName sınıftan silindi."
        }
    }

    fun deleteStudent(studentId: String, studentName: String) = deleteClassmate(studentId, studentName)

    override fun onCleared() {
        super.onCleared()
        childLogsListener?.remove()
        announcementListener?.remove()
    }
}
