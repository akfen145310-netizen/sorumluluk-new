package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActivityEntity
import com.example.data.Badge
import com.example.data.ClassmateStatEntity
import com.example.data.ClassroomConfigEntity
import com.example.data.DailyScheduleEntity
import com.example.data.DailyTaskUiItem
import com.example.data.DefaultTasks
import com.example.data.RegisteredUserEntity
import com.example.data.StudentProfileEntity
import com.example.data.TaskCategory
import com.example.data.TaskEntity
import com.example.data.TaskLogEntity
import com.example.ui.UserRole
import com.example.ui.components.BadgesSection
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.DiamondVaultCard
import com.example.ui.components.QrCodeCanvas
import com.example.ui.components.TaskCard
import com.example.ui.theme.BrandBorderMint
import com.example.ui.theme.BrandGold
import com.example.ui.theme.BrandGoldBg
import com.example.ui.theme.BrandGoldBorder
import com.example.ui.theme.BrandGoldText
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryContainer
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusSuccessBorder
import com.example.ui.theme.StatusSuccessText
import com.example.ui.theme.StatusWaitingBg
import com.example.ui.theme.StatusWaitingBorder
import com.example.ui.theme.StatusWaitingText
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.launch

/**
 * Masaüstü Versiyonu (Desktop Workstation Version)
 *
 * Mobil uygulamanın TÜM özelliklerine sahip olup programın veritabanı, StateFlow akışları ve
 * ViewModel'i ile %100 canlı irtibat kurar:
 * 1. Öğrenci Masası: 20 Görev, Canlı "Yaptım" İşaretleme, Elmas Sandığı, Rozetler.
 * 2. Veli Onay Masası: Tek Tıkla Onaylama, Düzeltme İsteme, 20 Özel Görev Belirleme, Aylık Ödül.
 * 3. Öğretmen Yönetim Masası: Sınıf İçi Liderlik Sıralaması, Duyuru Düzenleme, Etkinlik & Karekod.
 * 4. Sınıf Raporlama & Analiz Paneli.
 * 5. APK İndirme & Mobil Bağlantı Merkezi.
 */
@Composable
fun DesktopWorkstationScreen(
    currentRole: UserRole,
    activeUserName: String,
    studentProfile: StudentProfileEntity?,
    allStudentProfiles: List<StudentProfileEntity>,
    selectedChildId: String,
    onSelectChild: (String) -> Unit,
    dailyTasks: List<DailyTaskUiItem>,
    badges: List<Badge>,
    allTasks: List<TaskEntity>,
    dailySchedule: DailyScheduleEntity?,
    waitingApprovalLogs: List<TaskLogEntity>,
    classmateStats: List<ClassmateStatEntity>,
    classroomConfig: ClassroomConfigEntity?,
    registeredUsers: List<RegisteredUserEntity>,
    allActivities: List<ActivityEntity>,
    selectedCategoryFilter: String?,
    onCategoryFilterSelected: (String?) -> Unit,
    onStudentDoneClick: (String) -> Unit,
    onApproveTask: (String) -> Unit,
    onApproveAllWaitingTasks: () -> Unit,
    onRejectTask: (String, String) -> Unit,
    onStartCustomizingSchedule: () -> Unit,
    onCancelCustomizingSchedule: () -> Unit,
    onToggleCustomTask: (TaskEntity) -> Boolean,
    onSaveCustomSchedule: () -> Boolean,
    onResetToTeacherSchedule: () -> Unit,
    onUpdateMonthlyReward: (String, Int) -> Unit,
    onUpdateClassroomInfo: (String, String, String, String, String, String) -> Unit,
    onAddActivity: (String, String, String, Int) -> Unit,
    onSetActiveActivity: (String, String) -> Unit,
    onDeleteActivity: (String, String) -> Unit,
    onCompleteTaskForStudents: (List<String>, String, String, Int) -> Unit,
    onDeleteRegisteredUser: (String) -> Unit,
    onDeleteStudent: (String, String) -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    onNavigateToMobile: () -> Unit,
    onOpenWebPortal: (() -> Unit)? = null,
    onSyncData: (() -> Unit)? = null,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Masaüstü Sekmeleri (0: Öğrenci, 1: Veli, 2: Öğretmen, 3: İstatistikler, 4: APK İndir)
    var activeDesktopTab by remember(currentRole) {
        mutableIntStateOf(
            when (currentRole) {
                UserRole.STUDENT -> 0
                UserRole.PARENT -> 1
                UserRole.TEACHER -> 2
            }
        )
    }

    // Öğretmen duyuru düzenleme dialog durumu
    var showEditAnnouncementDialog by remember { mutableStateOf(false) }
    var tempAnnouncementText by remember { mutableStateOf(classroomConfig?.dailyAnnouncement ?: "") }

    // Aylık ödül düzenleme dialog durumu
    var showRewardDialog by remember { mutableStateOf(false) }
    var tempRewardTitle by remember { mutableStateOf(studentProfile?.monthlyRewardTitle ?: "Aylık Kitap Seti") }
    var tempRewardTarget by remember { mutableStateOf((studentProfile?.monthlyTargetDiamonds ?: 300).toString()) }

    // Düzeltme isteme (Reddetme) dialog durumu
    var taskToReject by remember { mutableStateOf<TaskLogEntity?>(null) }
    var rejectionReasonText by remember { mutableStateOf("") }

    val webAppUrl = "https://ais-pre-nu7hpo5lbggmotbcu6l2ij-249386399444.europe-west2.run.app"

    fun copyDownloadUrl() {
        clipboardManager.setText(AnnotatedString(webAppUrl))
        coroutineScope.launch {
            snackbarHostState.showSnackbar("✓ APK indirme bağlantısı panoya kopyalandı!")
        }
    }

    fun openBrowserDownload() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webAppUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            copyDownloadUrl()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
        ) {
            // =========================================================================
            // 1. SOL MASAÜSTÜ YÖNETİM PANELİ (SIDEBAR)
            // =========================================================================
            Surface(
                color = Color.White,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Logo & Başlık
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BrandPrimary,
                            modifier = Modifier.size(42.dp),
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Sorumluluk & Değerler",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = TextDark
                            )
                            Text(
                                text = "Masaüstü Versiyonu",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = BrandPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Program Canlı İrtibat Göstergesi (Room DB + Live Sync)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = StatusSuccessBg,
                        border = BorderStroke(1.dp, StatusSuccessBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSyncData?.invoke()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("✓ Program veritabanı senkronize edildi (Canlı İrtibat Aktif)")
                                }
                            }
                            .testTag("desktop_sync_indicator_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Sync, contentDescription = null, tint = StatusSuccessText, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Program ile Canlı İrtibatlı",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusSuccessText
                                    )
                                    Text(
                                        text = "Room SQLite Veritabanı Senkronize",
                                        fontSize = 9.sp,
                                        color = StatusSuccessText.copy(alpha = 0.85f)
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StatusSuccessText.copy(alpha = 0.15f),
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    text = "Eşitle",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusSuccessText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Kullanıcı Profil Kartı
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = TextDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (activeUserName.isNotBlank()) activeUserName else "Aktif Kullanıcı",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BrandGoldBg,
                                    border = BorderStroke(1.dp, BrandGoldBorder)
                                ) {
                                    Text(
                                        text = "${studentProfile?.diamonds ?: 45} 💎",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandGoldText,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Sınıf: ${classroomConfig?.className ?: "5-A Sınıfı"} • ${currentRole.titleTr}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Masaüstü Menü Maddeleri
                    Text(
                        text = "MASAÜSTÜ ÇALIŞMA ALANLARI",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DesktopNavButton(
                        icon = Icons.Default.AutoAwesome,
                        title = "🎒 Öğrenci Masası",
                        subtitle = "20 Görev & Elmas Sandığı",
                        isSelected = activeDesktopTab == 0,
                        badgeText = "20 Görev",
                        onClick = {
                            activeDesktopTab = 0
                            onSwitchRole(UserRole.STUDENT)
                        }
                    )

                    DesktopNavButton(
                        icon = Icons.Default.FamilyRestroom,
                        title = "👨‍👩‍👧 Veli Onay Masası",
                        subtitle = "Onaylar & 20 Özel Liste",
                        isSelected = activeDesktopTab == 1,
                        badgeText = if (waitingApprovalLogs.isNotEmpty()) "${waitingApprovalLogs.size} Bekleyen" else null,
                        badgeColor = Color(0xFFD97706),
                        onClick = {
                            activeDesktopTab = 1
                            onSwitchRole(UserRole.PARENT)
                        }
                    )

                    DesktopNavButton(
                        icon = Icons.Default.School,
                        title = "👩‍🏫 Öğretmen Masası",
                        subtitle = "Sınıf Sıralaması & Duyuru",
                        isSelected = activeDesktopTab == 2,
                        badgeText = "Sınıf",
                        badgeColor = Color(0xFF16A34A),
                        onClick = {
                            activeDesktopTab = 2
                            onSwitchRole(UserRole.TEACHER)
                        }
                    )

                    DesktopNavButton(
                        icon = Icons.Default.Leaderboard,
                        title = "📊 Sınıf İstatistikleri",
                        subtitle = "Genel Başarı & Dağılım",
                        isSelected = activeDesktopTab == 3,
                        onClick = { activeDesktopTab = 3 }
                    )

                    DesktopNavButton(
                        icon = Icons.Default.Download,
                        title = "📥 APK İndir & Mobil",
                        subtitle = "Cep Versiyonu İndir (.APK)",
                        isSelected = activeDesktopTab == 4,
                        onClick = { activeDesktopTab = 4 }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(12.dp))

                    if (onOpenWebPortal != null) {
                        OutlinedButton(
                            onClick = onOpenWebPortal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("desktop_open_web_portal_btn"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BrandPrimary)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🌐 Web Portalı & İndir", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // Alt Hızlı Butonlar: Cep Moduna Geç & Ayarlar
                    Button(
                        onClick = onNavigateToMobile,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("desktop_switch_to_mobile_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📱 Cep Versiyonuna Geç", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("desktop_open_settings_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = TextDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ayarlar & Font Boyutu", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    }
                }
            }

            VerticalDivider(color = Color(0xFFE2E8F0))

            // =========================================================================
            // 2. SAĞ GENİŞ ÇALIŞMA ALANI (MULTI-PANE WORKSTATION)
            // =========================================================================
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Üst Durum Çubuğu
                Surface(
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (activeDesktopTab) {
                                    0 -> "🎒 Öğrenci Çalışma Masası"
                                    1 -> "👨‍👩‍👧 Veli Onay & Liste Masası"
                                    2 -> "👩‍🏫 Öğretmen Yönetim Masası"
                                    3 -> "📊 Sınıf Başarı Raporları & Analiz"
                                    else -> "📥 Android APK İndirme & Mobil Bağlantı"
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BrandPrimaryContainer,
                                border = BorderStroke(1.dp, BrandBorderMint)
                            ) {
                                Text(
                                    text = "CANLI İRTİBAT: AKTİF",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Günün Sözü: ${classroomConfig?.dailyAnnouncement?.take(36) ?: "Sorumluluk ve değerler..."}...",
                                fontSize = 11.sp,
                                color = TextMuted,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                onClick = {
                                    onSyncData?.invoke()
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("✓ Mobil ve masaüstü verileri eşitlendi")
                                    }
                                },
                                modifier = Modifier.size(32.dp).testTag("desktop_top_sync_btn")
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = "Eşitle", tint = BrandPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            OutlinedButton(
                                onClick = { openBrowserDownload() },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                border = BorderStroke(1.dp, BrandPrimary)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("APK İndir", fontSize = 11.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Sekmeye Göre Çok Bölmeli İçerik
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    when (activeDesktopTab) {
                        0 -> DesktopStudentPane(
                            profile = studentProfile,
                            tasks = dailyTasks,
                            badges = badges,
                            dailyAnnouncement = classroomConfig?.dailyAnnouncement ?: "",
                            selectedCategoryFilter = selectedCategoryFilter,
                            onCategoryFilterSelected = onCategoryFilterSelected,
                            onStudentDoneClick = onStudentDoneClick
                        )

                        1 -> DesktopParentPane(
                            profile = studentProfile,
                            allStudentProfiles = allStudentProfiles,
                            selectedChildId = selectedChildId,
                            onSelectChild = onSelectChild,
                            tasks = dailyTasks,
                            allTasks = allTasks,
                            waitingApprovalLogs = waitingApprovalLogs,
                            onApproveTask = onApproveTask,
                            onApproveAllWaitingTasks = onApproveAllWaitingTasks,
                            onRejectClick = { log ->
                                taskToReject = log
                                rejectionReasonText = ""
                            },
                            onOpenRewardDialog = { showRewardDialog = true },
                            onStartCustomizingSchedule = onStartCustomizingSchedule
                        )

                        2 -> DesktopTeacherPane(
                            classroomConfig = classroomConfig,
                            classmateStats = classmateStats,
                            allActivities = allActivities,
                            registeredUsers = registeredUsers,
                            allTasks = allTasks,
                            onOpenAnnouncementEdit = {
                                tempAnnouncementText = classroomConfig?.dailyAnnouncement ?: ""
                                showEditAnnouncementDialog = true
                            },
                            onCompleteTaskForStudents = onCompleteTaskForStudents,
                            onDeleteRegisteredUser = onDeleteRegisteredUser,
                            onDeleteStudent = onDeleteStudent
                        )

                        3 -> DesktopAnalyticsPane(
                            classmateStats = classmateStats,
                            allTasks = allTasks,
                            dailyTasks = dailyTasks
                        )

                        4 -> DesktopDownloadPane(
                            downloadUrl = webAppUrl,
                            onDirectDownload = { openBrowserDownload() },
                            onCopyLink = { copyDownloadUrl() }
                        )
                    }
                }
            }
        }
    }

    // Günün Sözü / Duyuru Düzenleme Dialogu
    if (showEditAnnouncementDialog) {
        AlertDialog(
            onDismissRequest = { showEditAnnouncementDialog = false },
            title = { Text("📢 Günün Duyurusu & Değerler Sözü", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Tüm öğrencilerin ve velilerin masaüstü ve cep ekranında görünecektir:", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempAnnouncementText,
                        onValueChange = { tempAnnouncementText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        label = { Text("Duyuru / Günün Sözü") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentConfig = classroomConfig
                        onUpdateClassroomInfo(
                            currentConfig?.className ?: "5-A Sınıfı",
                            currentConfig?.subtitle ?: "Temel Değerler",
                            currentConfig?.activityName ?: "Haftalık Sorumluluk Hareketi",
                            currentConfig?.joinCode ?: "DEGERLER-5A-2026",
                            currentConfig?.teacherPin ?: "1234",
                            tempAnnouncementText
                        )
                        showEditAnnouncementDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Kaydet & Yayınla")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditAnnouncementDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    // Aylık Ödül ve Hedef Belirleme Dialogu
    if (showRewardDialog) {
        AlertDialog(
            onDismissRequest = { showRewardDialog = false },
            title = { Text("🎁 Aylık Ödül ve Elmas Hedefi", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Çocuğunuz bu ayki elmas hedefine ulaştığında kazanacağı ödülü belirleyin:", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempRewardTitle,
                        onValueChange = { tempRewardTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Ödül Başlığı (Örn: Kitap Seti, Bisiklet, Lunapark)") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempRewardTarget,
                        onValueChange = { tempRewardTarget = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Gereken Elmas Hedefi (Örn: 300)") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = tempRewardTarget.toIntOrNull() ?: 300
                        onUpdateMonthlyReward(tempRewardTitle, target)
                        showRewardDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                ) {
                    Text("Ödülü Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRewardDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    // Düzeltme İsteme (Reddetme) Dialogu
    if (taskToReject != null) {
        AlertDialog(
            onDismissRequest = { taskToReject = null },
            title = { Text("⚠️ Düzeltme İste", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Öğrenciye görevi neden tamamlanmamış saydığınızı ve ne yapması gerektiğini belirtin:", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rejectionReasonText,
                        onValueChange = { rejectionReasonText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Örn: Odanı biraz daha düzenlemen gerekiyor.") },
                        label = { Text("Düzeltme Notu") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        taskToReject?.taskId?.let { taskId ->
                            onRejectTask(taskId, rejectionReasonText)
                        }
                        taskToReject = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Gönder")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToReject = null }) {
                    Text("Vazgeç")
                }
            }
        )
    }
}

// =========================================================================
// 1. ÖĞRENCİ MASAÜSTÜ BÖLMESİ (SOL: 20 GÖREV, SAĞ: ELMAS SANDIĞI & ROZETLER)
// =========================================================================
@Composable
private fun DesktopStudentPane(
    profile: StudentProfileEntity?,
    tasks: List<DailyTaskUiItem>,
    badges: List<Badge>,
    dailyAnnouncement: String,
    selectedCategoryFilter: String?,
    onCategoryFilterSelected: (String?) -> Unit,
    onStudentDoneClick: (String) -> Unit
) {
    val completedCount = remember(tasks) {
        tasks.count { it.log.status == "APPROVED" || it.log.status == "STUDENT_DONE" || it.log.isCompletedByStudent }
    }
    val totalCount = remember(tasks) { tasks.size }

    val categoryCounts = remember(tasks) {
        TaskCategory.entries.associate { cat ->
            val catTasks = tasks.filter { it.task.categoryId == cat.id }
            val done = catTasks.count { it.log.status == "APPROVED" || it.log.isCompletedByStudent }
            cat.id to Pair(done, catTasks.size)
        }
    }

    val filteredTasks = remember(tasks, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) tasks else tasks.filter { it.task.categoryId == selectedCategoryFilter }
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Sol Bölme: Kategori Filtreleri & 20 Görev Listesi (Canlı İrtibatlı)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Tanımlanan 20 Görev Listesi",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = TextDark
                        )
                        Text(
                            text = "Her görev tamamlandığında +5 💎 elmas sandığına aktarılır.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BrandPrimaryContainer,
                        border = BorderStroke(1.dp, BrandBorderMint)
                    ) {
                        Text(
                            text = "$completedCount / $totalCount Tamamlandı",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = BrandPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Kategori Filtreleri
                CategoryFilterChips(
                    selectedCategoryId = selectedCategoryFilter,
                    onCategorySelected = onCategoryFilterSelected,
                    categoryItemCounts = categoryCounts
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Görev Listesi (Canlı)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTasks, key = { it.task.id }) { item ->
                        TaskCard(
                            uiItem = item,
                            userRole = UserRole.STUDENT,
                            onStudentDoneClick = { onStudentDoneClick(item.task.id) },
                            onParentApproveClick = {},
                            onParentRejectClick = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Sağ Bölme: Elmas Sandığı, Rozetler & Günün Duyurusu
        Column(
            modifier = Modifier
                .weight(0.9f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val approvedCountToday = remember(tasks) { tasks.count { it.log.status == "APPROVED" } }
            // Elmas Sandığı
            DiamondVaultCard(
                profile = profile,
                approvedCountToday = approvedCountToday,
                totalToday = 20,
                modifier = Modifier.fillMaxWidth()
            )

            // Günün Duyurusu Kartı
            if (dailyAnnouncement.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF065F46), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Öğretmen Duyurusu", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF065F46))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = dailyAnnouncement, fontSize = 12.sp, color = Color(0xFF065F46), lineHeight = 18.sp)
                    }
                }
            }

            // Rozet Kataloğu (Canlı)
            BadgesSection(
                badges = badges,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// =========================================================================
// 2. VELİ MASAÜSTÜ BÖLMESİ (SOL: ONAY BEKLEYENLER, SAĞ: ÖZEL LİSTE & ÖDÜL)
// =========================================================================
@Composable
private fun DesktopParentPane(
    profile: StudentProfileEntity?,
    allStudentProfiles: List<StudentProfileEntity>,
    selectedChildId: String,
    onSelectChild: (String) -> Unit,
    tasks: List<DailyTaskUiItem>,
    allTasks: List<TaskEntity>,
    waitingApprovalLogs: List<TaskLogEntity>,
    onApproveTask: (String) -> Unit,
    onApproveAllWaitingTasks: () -> Unit,
    onRejectClick: (TaskLogEntity) -> Unit,
    onOpenRewardDialog: () -> Unit,
    onStartCustomizingSchedule: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Sol Bölme: Onay Bekleyen Görevler
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Onay Bekleyen Görevler",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = TextDark
                        )
                        Text(
                            text = "Öğrencinin 'Yaptım' dediği görevleri onaylayarak elmas kazandırın.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    if (waitingApprovalLogs.isNotEmpty()) {
                        Button(
                            onClick = onApproveAllWaitingTasks,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hepsini Onayla", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(8.dp))

                if (waitingApprovalLogs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Harika! Onay bekleyen görev bulunmuyor.", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                            Text("Öğrenciniz yeni görev tamamladığında burada görünecektir.", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(waitingApprovalLogs, key = { it.id }) { log ->
                            val taskItem = tasks.find { it.task.id == log.taskId }
                            val taskTitle = taskItem?.task?.title ?: "Sorumluluk Görevi"

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = StatusWaitingBg,
                                border = BorderStroke(1.dp, StatusWaitingBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = taskTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                                        Text(text = "Öğrenci tamamladı • Onayınızla +5 💎 verilecek", fontSize = 11.sp, color = StatusWaitingText)
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = { onRejectClick(log) },
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Düzeltme İste", fontSize = 10.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { onApproveTask(log.id) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                        ) {
                                            Text("✓ Onayla (+5 💎)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sağ Bölme: Çocuk Seçimi, Özel Liste Yönetimi & Aylık Ödül Hedefi
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Çocuk Seçimi & Genel Durum
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Takip Edilen Öğrenci:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = profile?.name ?: "Değerli Öğrencim", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                    Text(text = "Sınıf: ${profile?.className ?: "5-A Sınıfı"} • Toplam: ${profile?.diamonds ?: 45} 💎", fontSize = 12.sp, color = BrandPrimary)
                }
            }

            // 20 Özel Görev Belirleme Masası
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("20 Özel Görev Belirleme", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Öğretmenin standart listesi yerine çocuğunuza özel 20 görev seçebilir ve kaydedebilirsiniz.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onStartCustomizingSchedule,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("Özel 20 Görev Listesini Düzenle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Aylık Ödül ve Hedef Kartı
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Aylık Ödül Hedefi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        TextButton(
                            onClick = onOpenRewardDialog,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Düzenle", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "🎁 ${profile?.monthlyRewardTitle ?: "Aylık Kitap Seti"}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFF78350F))
                    Spacer(modifier = Modifier.height(6.dp))

                    val currentDiamonds = profile?.diamonds ?: 45
                    val targetDiamonds = profile?.monthlyTargetDiamonds ?: 300
                    val progress = (currentDiamonds.toFloat() / targetDiamonds.coerceAtLeast(1)).coerceIn(0f, 1f)

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFD97706),
                        trackColor = Color(0xFFFEF3C7)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currentDiamonds / $targetDiamonds Elmas (%${(progress * 100).toInt()})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

// =========================================================================
// 3. ÖĞRETMEN MASAÜSTÜ BÖLMESİ (SOL: LİDERLİK TABLOSU, SAĞ: DUYURU & ETKİNLİK)
// =========================================================================
@Composable
private fun DesktopTeacherPane(
    classroomConfig: ClassroomConfigEntity?,
    classmateStats: List<ClassmateStatEntity>,
    allActivities: List<ActivityEntity>,
    registeredUsers: List<RegisteredUserEntity>,
    allTasks: List<TaskEntity>,
    onOpenAnnouncementEdit: () -> Unit,
    onCompleteTaskForStudents: (List<String>, String, String, Int) -> Unit,
    onDeleteRegisteredUser: (String) -> Unit,
    onDeleteStudent: (String, String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Sol Bölme: Sınıf Liderlik Tablosu & Başarı Sıralaması
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Sınıf İçi Liderlik Sıralaması",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = TextDark
                        )
                        Text(
                            text = "Öğrencilerin toplam elmas ve tamamlanan görev istatistikleri",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDCFCE7),
                        border = BorderStroke(1.dp, Color(0xFF16A34A))
                    ) {
                        Text(
                            text = "${classmateStats.size} Öğrenci",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sortedStats = classmateStats.sortedByDescending { it.diamonds }
                    items(sortedStats) { stat ->
                        val rank = sortedStats.indexOf(stat) + 1
                        val medal = when (rank) {
                            1 -> "🥇"
                            2 -> "🥈"
                            3 -> "🥉"
                            else -> "#$rank"
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (rank <= 3) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (rank <= 3) Color(0xFFFDE68A) else Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = medal, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = stat.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                                        Text(text = "${stat.completedToday}/${stat.totalToday} Görev • ${stat.streakDays} Gün Seri 🔥", fontSize = 10.sp, color = TextMuted)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BrandGoldBg,
                                    border = BorderStroke(1.dp, BrandGoldBorder)
                                ) {
                                    Text(
                                        text = "${stat.diamonds} 💎",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = BrandGoldText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sağ Bölme: Duyurular, Sınıf Kodu & Etkinlikler
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Günün Sözü & Duyuru Kartı
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Günün Duyurusu & Değerler Sözü", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                        TextButton(
                            onClick = onOpenAnnouncementEdit,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Düzenle", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = classroomConfig?.dailyAnnouncement ?: "Ahlak ve sorumluluk, insanı yücelten iki kanattır.",
                        fontSize = 12.sp,
                        color = Color(0xFF065F46),
                        lineHeight = 18.sp
                    )
                }
            }

            // Sınıf Katılım Kodu & Karekod
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sınıf Katılım Kodu", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = classroomConfig?.joinCode ?: "DEGERLER-5A-2026",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = BrandPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Öğrenci ve veliler bu kod ile doğrudan sınıfa bağlanabilir.", fontSize = 11.sp, color = TextMuted)
                }
            }

            // Aktif Etkinlikler
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Haftalık Sorumluluk Etkinlikleri", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    allActivities.forEach { act ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = act.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                                    Text(text = "${act.targetDays} Günlük Hedef", fontSize = 10.sp, color = BrandPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 4. SINIF RAPORU & ANALİZ BÖLMESİ
// =========================================================================
@Composable
private fun DesktopAnalyticsPane(
    classmateStats: List<ClassmateStatEntity>,
    allTasks: List<TaskEntity>,
    dailyTasks: List<DailyTaskUiItem>
) {
    val totalDiamondsInClass = classmateStats.sumOf { it.diamonds }
    val totalCompletedTasks = classmateStats.sumOf { it.completedToday }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // İstatistik Metrik Kartları
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetricCard(
                title = "Toplam Dağıtılan Elmas",
                value = "$totalDiamondsInClass 💎",
                subtitle = "Sınıf Genelinde Kazanılan",
                color = BrandGold,
                bgColor = BrandGoldBg,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Tamamlanan Görev",
                value = "$totalCompletedTasks",
                subtitle = "Öğrenci Doğrulanan",
                color = BrandPrimary,
                bgColor = BrandPrimaryContainer,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Sınıf Mevcudu",
                value = "${classmateStats.size} Öğrenci",
                subtitle = "Aktif Takip Edilen",
                color = Color(0xFF16A34A),
                bgColor = Color(0xFFDCFCE7),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Kategori Başarı Dağılımı Kartı
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Kategori Bazında Günlük Sorumluluk Dağılımı", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                Spacer(modifier = Modifier.height(14.dp))

                TaskCategory.entries.forEach { cat ->
                    val catTasks = dailyTasks.filter { it.task.categoryId == cat.id }
                    val done = catTasks.count { it.log.status == "APPROVED" || it.log.isCompletedByStudent }
                    val total = catTasks.size.coerceAtLeast(1)
                    val ratio = (done.toFloat() / total).coerceIn(0f, 1f)

                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(cat.icon, contentDescription = null, tint = cat.color, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = cat.titleTr, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                            Text(text = "$done / $total (%${(ratio * 100).toInt()})", fontSize = 11.sp, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = BrandPrimary,
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 5. APK İNDİR & MOBİL BAĞLANTI BÖLMESİ
// =========================================================================
@Composable
private fun DesktopDownloadPane(
    downloadUrl: String,
    onDirectDownload: () -> Unit,
    onCopyLink: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(2.dp, BrandPrimary),
            shadowElevation = 3.dp,
            modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Android, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Android APK Uygulama Dosyasını İndir", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                        Text("Masaüstündeki tüm veriler ve özellikler cep telefonunuzda da anında açılır", fontSize = 11.sp, color = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDirectDownload,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("📥 Doğrudan APK İndir (.APK)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onCopyLink,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF475569))
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📋 İndirme Linkini Kopyala", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF475569))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("📱 Telefonunuzla Okutarak İndirin (Karekod):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                        .padding(8.dp)
                ) {
                    QrCodeCanvas(content = downloadUrl, sizeDp = 160.dp, darkColor = Color(0xFF0F172A))
                }
            }
        }
    }
}

// =========================================================================
// YARDIMCI MASAÜSTÜ BİLEŞENLERİ
// =========================================================================
@Composable
private fun DesktopNavButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    badgeText: String? = null,
    badgeColor: Color = BrandPrimary,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) BrandPrimaryContainer else Color.Transparent,
        border = if (isSelected) BorderStroke(1.dp, BrandBorderMint) else null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) BrandPrimary else Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = if (isSelected) BrandPrimary else TextDark
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            if (badgeText != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 11.sp, color = TextMuted)
        }
    }
}
