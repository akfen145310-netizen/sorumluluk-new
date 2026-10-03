package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.UserRole
import com.example.ui.components.AppSettingsDialog
import com.example.ui.components.QrRegistrationDialog
import com.example.ui.components.RoleSelectorBar
import com.example.ui.components.TeacherPinDialog
import com.example.ui.screens.IntroductionScreen
import com.example.ui.screens.ParentDemoScreen
import com.example.ui.screens.ParentScreen
import com.example.ui.screens.RegistrationScreen
import com.example.ui.screens.StudentDemoScreen
import com.example.ui.screens.StudentScreen
import com.example.ui.screens.TeacherDemoScreen
import com.example.ui.screens.TeacherScreen
import com.example.ui.screens.WebPortalScreen
import com.example.ui.theme.MyApplicationTheme

enum class IntroScreenState {
    INTRO,
    STUDENT_DEMO,
    PARENT_DEMO,
    TEACHER_DEMO,
    REGISTRATION,
    WEB_PORTAL
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val activeFontOption by viewModel.activeFontOption.collectAsStateWithLifecycle()
            val activeFontScale by viewModel.activeFontScale.collectAsStateWithLifecycle()

            MyApplicationTheme(
                fontOption = activeFontOption,
                fontScale = activeFontScale
            ) {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    viewModel: MainViewModel = viewModel()
) {
    val isSessionActive by viewModel.isSessionActive.collectAsStateWithLifecycle()
    val activeUserName by viewModel.activeUserName.collectAsStateWithLifecycle()
    val activeFontOption by viewModel.activeFontOption.collectAsStateWithLifecycle()
    val activeFontScale by viewModel.activeFontScale.collectAsStateWithLifecycle()

    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val studentProfile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val dailyTasks by viewModel.dailyTasks.collectAsStateWithLifecycle()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val dailySchedule by viewModel.dailySchedule.collectAsStateWithLifecycle()
    val waitingApprovalLogs by viewModel.waitingApprovalLogs.collectAsStateWithLifecycle()
    val classmateStats by viewModel.classmateStats.collectAsStateWithLifecycle()
    val classroomConfig by viewModel.classroomConfig.collectAsStateWithLifecycle()
    val registeredUsers by viewModel.registeredUsers.collectAsStateWithLifecycle()
    val allActivities by viewModel.allActivities.collectAsStateWithLifecycle()
    val badges by viewModel.badges.collectAsStateWithLifecycle()
    val allStudentProfiles by viewModel.allStudentProfiles.collectAsStateWithLifecycle()
    val selectedChildId by viewModel.selectedChildId.collectAsStateWithLifecycle()

    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val isCustomizingSchedule by viewModel.isCustomizingSchedule.collectAsStateWithLifecycle()
    val customSelectedTaskIds by viewModel.customSelectedTaskIds.collectAsStateWithLifecycle()
    val notificationMessage by viewModel.notificationMessage.collectAsStateWithLifecycle()

    val dontShowIntroAgain by viewModel.dontShowIntroAgain.collectAsStateWithLifecycle()
    var authScreenState by remember(dontShowIntroAgain) {
        mutableStateOf(if (dontShowIntroAgain) IntroScreenState.REGISTRATION else IntroScreenState.INTRO)
    }

    var preselectedRole by remember { mutableStateOf<UserRole?>(UserRole.STUDENT) }
    var prefilledFullName by remember { mutableStateOf("") }
    var prefilledLinkedStudent by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    var showTeacherPinDialog by remember { mutableStateOf(false) }
    var showQrRegisterDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showWebPortalView by remember { mutableStateOf(false) }

    LaunchedEffect(notificationMessage) {
        notificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    // Uygulama ön plana geldiğinde günün değişip değişmediği kontrol edilir;
    // gece boyunca açık kalan uygulamada "bugünün görevleri" ertesi güne geçer.
    LifecycleResumeEffect(Unit) {
        viewModel.refreshToday()
        onPauseOrDispose { }
    }

    if (!isSessionActive) {
        when (authScreenState) {
            IntroScreenState.INTRO -> {
                IntroductionScreen(
                    dontShowAgain = dontShowIntroAgain,
                    onToggleDontShowAgain = { viewModel.setDontShowIntroAgain(it) },
                    onOpenStudentDemo = { authScreenState = IntroScreenState.STUDENT_DEMO },
                    onOpenParentDemo = { authScreenState = IntroScreenState.PARENT_DEMO },
                    onOpenTeacherDemo = { authScreenState = IntroScreenState.TEACHER_DEMO },
                    onOpenWebPortal = { authScreenState = IntroScreenState.WEB_PORTAL },
                    onNavigateToRegister = {
                        preselectedRole = UserRole.STUDENT
                        prefilledFullName = ""
                        prefilledLinkedStudent = ""
                        authScreenState = IntroScreenState.REGISTRATION
                    }
                )
            }

            IntroScreenState.STUDENT_DEMO -> {
                BackHandler {
                    authScreenState = IntroScreenState.INTRO
                }
                StudentDemoScreen(
                    onNavigateToRegister = {
                        preselectedRole = UserRole.STUDENT
                        prefilledFullName = "Değerli Öğrencim"
                        prefilledLinkedStudent = ""
                        authScreenState = IntroScreenState.REGISTRATION
                    },
                    onBackToIntro = {
                        authScreenState = IntroScreenState.INTRO
                    }
                )
            }

            IntroScreenState.PARENT_DEMO -> {
                BackHandler {
                    authScreenState = IntroScreenState.INTRO
                }
                ParentDemoScreen(
                    onNavigateToRegister = {
                        preselectedRole = UserRole.PARENT
                        prefilledFullName = "Saygıdeğer Velim"
                        prefilledLinkedStudent = "Değerli Öğrencim"
                        authScreenState = IntroScreenState.REGISTRATION
                    },
                    onBackToIntro = {
                        authScreenState = IntroScreenState.INTRO
                    }
                )
            }

            IntroScreenState.TEACHER_DEMO -> {
                BackHandler {
                    authScreenState = IntroScreenState.INTRO
                }
                TeacherDemoScreen(
                    onNavigateToRegister = {
                        preselectedRole = UserRole.TEACHER
                        prefilledFullName = "Sayın Öğretmenim"
                        prefilledLinkedStudent = ""
                        authScreenState = IntroScreenState.REGISTRATION
                    },
                    onBackToIntro = {
                        authScreenState = IntroScreenState.INTRO
                    }
                )
            }

            IntroScreenState.WEB_PORTAL -> {
                BackHandler {
                    authScreenState = IntroScreenState.INTRO
                }
                WebPortalScreen(
                    onNavigateToMobile = {
                        authScreenState = IntroScreenState.REGISTRATION
                    },
                    onOpenStudentDemo = {
                        authScreenState = IntroScreenState.STUDENT_DEMO
                    },
                    onOpenParentDemo = {
                        authScreenState = IntroScreenState.PARENT_DEMO
                    },
                    onOpenTeacherDemo = {
                        authScreenState = IntroScreenState.TEACHER_DEMO
                    },
                    onNavigateToRegister = {
                        authScreenState = IntroScreenState.REGISTRATION
                    }
                )
            }

            IntroScreenState.REGISTRATION -> {
                BackHandler(enabled = !dontShowIntroAgain) {
                    authScreenState = IntroScreenState.INTRO
                }
                // Kayıt ekranı: klavye açıldığında içeriğin yukarı kayabilmesi için imePadding uygulandı.
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        RegistrationScreen(
                            config = classroomConfig,
                            initialRole = preselectedRole,
                            initialFullName = prefilledFullName,
                            initialLinkedStudent = prefilledLinkedStudent,
                            dontShowIntroAgain = dontShowIntroAgain,
                            onToggleDontShowIntroAgain = { viewModel.setDontShowIntroAgain(it) },
                            onOpenIntro = { authScreenState = IntroScreenState.INTRO },
                            onOpenStudentDemo = { authScreenState = IntroScreenState.STUDENT_DEMO },
                            onOpenParentDemo = { authScreenState = IntroScreenState.PARENT_DEMO },
                            onOpenTeacherDemo = { authScreenState = IntroScreenState.TEACHER_DEMO },
                            onOpenWebPortal = { authScreenState = IntroScreenState.WEB_PORTAL },
                            onRegisterSuccess = { role, fullName, linkedStudentName ->
                                viewModel.setDontShowIntroAgain(true)
                                viewModel.registerAndStartRole(role, fullName, linkedStudentName)
                            },
                            onVerifyTeacherPin = { pin ->
                                viewModel.verifyTeacherPin(pin)
                            }
                        )
                    }
                }
            }
        }
    } else {
        // Simplified, uncluttered role-specific view: only the registered role opens
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                if (!isCustomizingSchedule) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                        ) {
                            // Header title, user badge & quick settings
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Emerald Logo Box (Matching the book icon logo in image)
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = com.example.ui.theme.BrandPrimary,
                                        modifier = Modifier.size(40.dp),
                                        shadowElevation = 2.dp
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.School,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = classroomConfig?.className ?: "Din Keşif Atölyesi",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = com.example.ui.theme.TextDark,
                                            maxLines = 1
                                        )
                                        val roleLabel = when (currentRole) {
                                            UserRole.STUDENT -> "Öğrenci"
                                            UserRole.PARENT -> "Veli"
                                            UserRole.TEACHER -> "Öğretmen"
                                        }
                                        val userDisplay = if (activeUserName.isNotBlank()) "$activeUserName • $roleLabel" else currentRole.titleTr
                                        Text(
                                            text = "| Temel Değerler • $userDisplay",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = com.example.ui.theme.TextMuted,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Star Diamond Pill (Matching 0 | 0 P pill in screenshot)
                                    if (currentRole == UserRole.STUDENT || currentRole == UserRole.PARENT) {
                                        val diamonds = studentProfile?.diamonds ?: 0
                                        Surface(
                                            shape = RoundedCornerShape(18.dp),
                                            color = com.example.ui.theme.BrandGoldBg,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BrandGoldBorder),
                                            modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = com.example.ui.theme.BrandGold,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "$diamonds 💎",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = com.example.ui.theme.BrandGoldText
                                                )
                                            }
                                        }
                                    }

                                    // Tanıtımı bir daha gösterme ikonu (Kayıttan sonra tanıtım tercihi)
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (dontShowIntroAgain) com.example.ui.theme.BrandPrimaryContainer else Color(0xFFF1F5F9),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (dontShowIntroAgain) com.example.ui.theme.BrandBorderMint else com.example.ui.theme.BorderSubtle),
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.toggleDontShowIntroAgain() },
                                            modifier = Modifier.size(36.dp).testTag("topbar_dont_show_intro_btn")
                                        ) {
                                            Icon(
                                                imageVector = if (dontShowIntroAgain) Icons.Filled.VisibilityOff else Icons.Outlined.Visibility,
                                                contentDescription = if (dontShowIntroAgain) "Tanıtım Kapalı (Açılışta Gösterilmeyecek)" else "Tanıtımı Bir Daha Gösterme",
                                                tint = if (dontShowIntroAgain) com.example.ui.theme.BrandPrimary else com.example.ui.theme.TextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    // Teacher specific QR action
                                    if (currentRole == UserRole.TEACHER) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = com.example.ui.theme.BrandPrimaryContainer,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BrandBorderMint),
                                            modifier = Modifier.padding(end = 4.dp)
                                        ) {
                                            IconButton(
                                                onClick = { showQrRegisterDialog = true },
                                                modifier = Modifier.size(36.dp).testTag("topbar_qr_join_btn")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.QrCode,
                                                    contentDescription = "Karekod Paylaş / Kayıt",
                                                    tint = com.example.ui.theme.BrandPrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Dedicated Web Portal & APK Download Button
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = com.example.ui.theme.BrandPrimaryContainer,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BrandBorderMint),
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        IconButton(
                                            onClick = { showWebPortalView = true },
                                            modifier = Modifier.size(36.dp).testTag("topbar_web_portal_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Laptop,
                                                contentDescription = "Bilgisayar Sitesi & Cep APK İndir",
                                                tint = com.example.ui.theme.BrandPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    // Dedicated Settings & Font Switcher Button
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFF1F5F9),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSubtle)
                                    ) {
                                        IconButton(
                                            onClick = { showSettingsDialog = true },
                                            modifier = Modifier.size(36.dp).testTag("topbar_settings_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = "Ayarlar & Yazı Fontu",
                                                tint = com.example.ui.theme.TextDark,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            androidx.compose.material3.HorizontalDivider(
                                color = com.example.ui.theme.BorderSubtle,
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (showWebPortalView) {
                    BackHandler {
                        showWebPortalView = false
                    }
                    WebPortalScreen(
                        onNavigateToMobile = {
                            showWebPortalView = false
                        },
                        onOpenStudentDemo = {
                            showWebPortalView = false
                            viewModel.switchRole(UserRole.STUDENT)
                        },
                        onOpenParentDemo = {
                            showWebPortalView = false
                            viewModel.switchRole(UserRole.PARENT)
                        },
                        onOpenTeacherDemo = {
                            showWebPortalView = false
                            viewModel.switchRole(UserRole.TEACHER)
                        },
                        onNavigateToRegister = {
                            showWebPortalView = false
                        }
                    )
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                    // Kayıt olduktan sonra kullanıcının tanıtımı kapatabilmesi için doğrudan bildirim kartı
                    if (!dontShowIntroAgain) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .testTag("dont_show_intro_reminder_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Uygulama açılışında tanıtımın bir daha çıkmasını istemiyorsanız:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = { viewModel.setDontShowIntroAgain(true) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("banner_dont_show_intro_btn")
                                ) {
                                    Text(
                                        text = "Bir Daha Gösterme",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        // Strict Role Isolation: Only the mode corresponding to the user's registration opens
                        when (currentRole) {
                    UserRole.STUDENT -> {
                        StudentScreen(
                            profile = studentProfile,
                            tasks = dailyTasks,
                            badges = badges,
                            dailyAnnouncement = classroomConfig?.dailyAnnouncement ?: "",
                            selectedCategoryFilter = selectedCategoryFilter,
                            onCategoryFilterSelected = { viewModel.setCategoryFilter(it) },
                            onStudentDoneClick = { taskId -> viewModel.markTaskAsDone(taskId) }
                        )
                    }

                    UserRole.PARENT -> {
                        ParentScreen(
                            profile = studentProfile,
                            allStudentProfiles = allStudentProfiles,
                            selectedChildId = selectedChildId,
                            onSelectChild = { viewModel.selectChild(it) },
                            tasks = dailyTasks,
                            badges = badges,
                            allTasks = allTasks,
                            schedule = dailySchedule,
                            waitingApprovalLogs = waitingApprovalLogs,
                            isCustomizingSchedule = isCustomizingSchedule,
                            customSelectedTaskIds = customSelectedTaskIds,
                            selectedCategoryFilter = selectedCategoryFilter,
                            onCategoryFilterSelected = { viewModel.setCategoryFilter(it) },
                            onApproveTask = { taskId -> viewModel.approveTask(taskId) },
                            onApproveAllWaitingTasks = { viewModel.approveAllWaitingTasks() },
                            onRejectTask = { taskId, reason -> viewModel.rejectTask(taskId, reason) },
                            onStartCustomizingSchedule = { viewModel.startCustomizingSchedule() },
                            onCancelCustomizingSchedule = { viewModel.cancelCustomizingSchedule() },
                            onToggleCustomTask = { task -> viewModel.toggleCustomTaskSelection(task) },
                            onSaveCustomSchedule = { viewModel.saveCustomSchedule() },
                            onResetToTeacherSchedule = { viewModel.resetToTeacherSchedule() },
                            onUpdateMonthlyReward = { title, target -> viewModel.updateMonthlyReward(title, target) }
                        )
                    }

                    UserRole.TEACHER -> {
                        TeacherScreen(
                            classroomConfig = classroomConfig,
                            registeredUsers = registeredUsers,
                            allActivities = allActivities,
                            classmateStats = classmateStats,
                            allTasks = allTasks,
                            activeDailyTasks = dailyTasks,
                            onUpdateClassroomInfo = { cName, sub, act, jCode, pin, announcement ->
                                viewModel.updateClassroomInfo(cName, sub, act, jCode, pin, announcement)
                            },
                            onAddActivity = { title, desc, jCode, days ->
                                viewModel.addActivity(title, desc, jCode, days)
                            },
                            onSetActiveActivity = { actId, title ->
                                viewModel.setActiveActivity(actId, title)
                            },
                            onDeleteActivity = { actId, title ->
                                viewModel.deleteActivity(actId, title)
                            },
                            onSaveActivityTaskSettings = { actId, title, systemIds, customTasks ->
                                viewModel.saveActivityTaskSettings(actId, title, systemIds, customTasks)
                            },
                            onAssignTasksToStudents = { studentIds, taskIds, title, groupName ->
                                viewModel.assignTasksToStudents(studentIds, taskIds, title, groupName)
                            },
                            onCompleteTaskForStudents = { studentIds, taskId, taskTitle, diamonds ->
                                viewModel.completeTaskForStudents(studentIds, taskId, taskTitle, diamonds)
                            },
                            onRegisterUser = { role, name, studentName, phone, onComplete ->
                                viewModel.registerViaQr(
                                    role = role,
                                    fullName = name,
                                    linkedStudentName = studentName,
                                    phone = phone,
                                    classId = classroomConfig?.id ?: "default_class",
                                    activityName = classroomConfig?.activityName ?: "Haftalık Sorumluluk Hareketi",
                                    onComplete = onComplete
                                )
                            },
                            onDeleteRegisteredUser = { userId ->
                                viewModel.deleteRegisteredUser(userId)
                            },
                            onDeleteStudent = { studentId, studentName ->
                                viewModel.deleteStudent(studentId, studentName)
                            }
                        )
                    }
                }
            }
        }
    }
}
}
}

    // App Settings & Font Customizer Dialog
    if (showSettingsDialog) {
        AppSettingsDialog(
            currentRole = currentRole,
            userName = activeUserName,
            currentFontOption = activeFontOption,
            currentFontScale = activeFontScale,
            dontShowIntroAgain = dontShowIntroAgain,
            onToggleDontShowIntro = { viewModel.setDontShowIntroAgain(it) },
            onDismiss = { showSettingsDialog = false },
            onSaveSettings = { fontOption, scale ->
                viewModel.updateTypographySettings(fontOption, scale)
            },
            onLogoutOrSwitchRole = {
                viewModel.logoutOrSwitchRole()
            }
        )
    }

    // Teacher PIN Verification Dialog
    if (showTeacherPinDialog) {
        TeacherPinDialog(
            onDismiss = { showTeacherPinDialog = false },
            onSubmitPin = { pin ->
                val ok = viewModel.trySwitchToTeacher(pin)
                ok
            }
        )
    }

    // Global QR Code Registration Modal Dialog (Opened from top bar or QR reader)
    if (showQrRegisterDialog) {
        QrRegistrationDialog(
            config = classroomConfig,
            selectedActivity = allActivities.find { it.isActive },
            onDismiss = { showQrRegisterDialog = false },
            onRegister = { role, fullName, linkedStudentName, phone, onComplete ->
                viewModel.registerViaQr(
                    role = role,
                    fullName = fullName,
                    linkedStudentName = linkedStudentName,
                    phone = phone,
                    classId = classroomConfig?.id ?: "default_class",
                    activityName = classroomConfig?.activityName ?: "Haftalık Sorumluluk Hareketi",
                    onComplete = { success, msg ->
                        onComplete(success, msg)
                        if (success) {
                            if (role == "STUDENT") {
                                viewModel.switchRoleSafely(UserRole.STUDENT)
                            } else {
                                viewModel.switchRoleSafely(UserRole.PARENT)
                            }
                        }
                    }
                )
            }
        )
    }
}
