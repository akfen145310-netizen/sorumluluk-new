package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Badge
import com.example.data.BadgeCatalog
import com.example.data.DailyTaskUiItem
import com.example.data.DefaultTasks
import com.example.data.StudentProfileEntity
import com.example.data.TaskCategory
import com.example.data.TaskEntity
import com.example.data.TaskLogEntity
import com.example.ui.UserRole
import com.example.ui.components.BadgesSection
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.DiamondVaultCard
import com.example.ui.components.StudentDailyIncentiveHeader
import com.example.ui.components.TaskCard
import kotlinx.coroutines.launch

/**
 * Adı: Değerli, Soyadı: Öğrencim olan öğrencinin ekranı.
 * Tanımlanan 20 görevi (5 Ev, 5 Okul, 5 Dini, 5 Adap & Görgü) içerir.
 * Kullanıcı "Yaptım" diyerek canlı olarak işaretleyebilir, elmas kazanabilir,
 * rozetlerini ve filtreleri deneyimleyebilir.
 * "Şimdi Kaydol" butonuyla doğrudan "Lütfen Giriş ve Kayıt Bölümünü Seçiniz" ekranına geçer.
 */
@Composable
fun StudentDemoScreen(
    onNavigateToRegister: () -> Unit,
    onBackToIntro: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Profil: Adı Değerli, Soyadı Öğrencim
    var demoDiamonds by remember { mutableIntStateOf(45) }
    val demoStreakDays = 3

    val profile = remember(demoDiamonds) {
        StudentProfileEntity(
            id = "demo_degerli_ogrencim",
            name = "Değerli Öğrencim",
            className = "5-A Sınıfı Değerler",
            diamonds = demoDiamonds,
            streakDays = demoStreakDays,
            monthlyRewardTitle = "Lunapark Gezisi & Kitap Seti",
            monthlyTargetDiamonds = 300
        )
    }

    // 20 Tanımlanan Görev (Default 20 ids)
    val taskMap = remember {
        DefaultTasks.ALL_TASKS.associateBy { it.id }
    }
    val default20Tasks = remember {
        DefaultTasks.DEFAULT_DAILY_20_IDS.mapNotNull { id ->
            taskMap[id]?.let { def ->
                TaskEntity(
                    id = def.id,
                    title = def.title,
                    categoryId = def.categoryId,
                    subCategoryId = def.subCategoryId,
                    diamondReward = def.diamondReward
                )
            }
        }
    }

    // Görevlerin tamamlanma durumları (canlı işaretlenebilir)
    // Key: TaskId, Value: Boolean (yaptım/yapmadım)
    val completedMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            // Başlangıçta 2 tanesi yapılmış olsun ki kullanıcı hazır durumu da görsün
            put("home_1", true)
            put("school_1", true)
        }
    }

    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    // DailyTaskUiItem listesini oluştur
    val uiTasks: List<DailyTaskUiItem> = remember(completedMap.keys.toList(), completedMap.values.toList()) {
        default20Tasks.map { task ->
            val isDone = completedMap[task.id] == true
            DailyTaskUiItem(
                task = task,
                log = TaskLogEntity(
                    id = "demo_log_${task.id}",
                    dateString = "2026-10-02",
                    studentId = "demo_degerli_ogrencim",
                    taskId = task.id,
                    status = if (isDone) "APPROVED" else "NOT_DONE",
                    isCompletedByStudent = isDone,
                    isApprovedByParent = isDone,
                    diamondsAwarded = if (isDone) 5 else 0
                )
            )
        }
    }

    val approvedCount = remember(uiTasks) {
        uiTasks.count { it.log.status == "APPROVED" }
    }
    val completedCount = remember(uiTasks) {
        uiTasks.count { it.log.isCompletedByStudent }
    }
    val totalCount = uiTasks.size // 20

    val categoryCounts = remember(uiTasks) {
        TaskCategory.entries.associate { cat ->
            val catTasks = uiTasks.filter { it.task.categoryId == cat.id }
            val done = catTasks.count { it.log.isCompletedByStudent }
            cat.id to Pair(done, catTasks.size)
        }
    }

    val filteredTasks = remember(uiTasks, selectedCategoryFilter) {
        if (selectedCategoryFilter.isNullOrBlank()) {
            uiTasks
        } else {
            uiTasks.filter { it.task.categoryId == selectedCategoryFilter }
        }
    }

    val badges = remember(demoDiamonds, approvedCount) {
        BadgeCatalog.getBadges(
            diamonds = demoDiamonds,
            streakDays = demoStreakDays,
            approvedTaskCount = approvedCount,
            approvedReligionCount = uiTasks.count { it.task.categoryId == "religion" && it.log.status == "APPROVED" }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onBackToIntro,
                                modifier = Modifier.testTag("demo_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Tanıtıma Dön",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column {
                                Text(
                                    text = "Değerli Öğrencim",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Öğrenci Deneme Girişi (20 Görev)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Hızlı Şimdi Kaydol Butonu
                        Button(
                            onClick = onNavigateToRegister,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("demo_top_register_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Şimdi Kaydol",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // ALT SABİT DOCK: Şimdi Kaydol - Lütfen Giriş ve Kayıt Bölümünü Seçiniz'e yönlendirir
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onNavigateToRegister,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("demo_bottom_register_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Şimdi Kaydol (Giriş & Kayıt Bölümüne Geç)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Lütfen Giriş ve Kayıt Bölümünü Seçiniz sayfasına aktarır",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("demo_student_tasks_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Bilgilendirici Canlı Deneyim Başlığı
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Öğrenci: Değerli Öğrencim (Tanımlanan 20 Görev)",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Aşağıdaki görevleri 'Yaptım' butonuyla işaretleyebilir, elmas ve rozet kazanımını canlı test edebilirsiniz.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Daily incentive & countdown header
            item {
                StudentDailyIncentiveHeader(
                    announcement = "Günün Sözü: Sorumluluk bilinci, karakterin en parlak mücevheridir. Bugün 20 görevini tamamlayarak elmasları topla!",
                    monthlyRewardTitle = profile.monthlyRewardTitle,
                    monthlyTargetDiamonds = profile.monthlyTargetDiamonds,
                    currentDiamonds = profile.diamonds,
                    completedTasksCount = completedCount,
                    totalTasksCount = totalCount,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Diamond vault card
            item {
                DiamondVaultCard(
                    profile = profile,
                    approvedCountToday = approvedCount,
                    totalToday = totalCount
                )
            }

            // Category Filter Chips
            item {
                CategoryFilterChips(
                    selectedCategoryId = selectedCategoryFilter,
                    onCategorySelected = { selectedCategoryFilter = it },
                    categoryItemCounts = categoryCounts
                )
            }

            // Tasks Header
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(
                        text = "Tanımlanan 20 Görev (${filteredTasks.size} Gösteriliyor)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bir öğrencinin görebileceği ve yapabileceği her şey burada aktiftir. 'Yaptım'a dokunun:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (filteredTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Bu kategoride atanmış görev bulunmuyor.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredTasks, key = { it.task.id }) { item ->
                    TaskCard(
                        uiItem = item,
                        userRole = UserRole.STUDENT,
                        onStudentDoneClick = {
                            val currentlyDone = completedMap[item.task.id] == true
                            val newDone = !currentlyDone
                            completedMap[item.task.id] = newDone

                            if (newDone) {
                                demoDiamonds += item.task.diamondReward
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("✓ Tebrikler Değerli Öğrencim! '${item.task.title}' yapıldı (+${item.task.diamondReward} Elmas).")
                                }
                            } else {
                                demoDiamonds = (demoDiamonds - item.task.diamondReward).coerceAtLeast(0)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("İşaret kaldırıldı.")
                                }
                            }
                        },
                        onParentApproveClick = {},
                        onParentRejectClick = {}
                    )
                }
            }

            // Badges Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                BadgesSection(badges = badges)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
