package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.ui.components.TaskCard
import com.example.ui.theme.StatusWaitingBg
import com.example.ui.theme.StatusWaitingBorder
import com.example.ui.theme.StatusWaitingText
import kotlinx.coroutines.launch

/**
 * Veli Deneme Ekranı:
 * Adı: Saygıdeğer, Soyadı: Velim
 * Bağlı Öğrenci: Değerli Öğrencim
 * Velinin yapabileceği tüm işlemleri (Onaylama, Düzeltme İsteme, Özel Liste Belirleme,
 * Aylık Ödül Yönetimi) canlı olarak deneyimlemesini sağlar.
 */
@Composable
fun ParentDemoScreen(
    onNavigateToRegister: () -> Unit,
    onBackToIntro: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    // Çocuğun elmas ve ödül bilgileri
    var studentDiamonds by remember { mutableIntStateOf(65) }
    var monthlyRewardTitle by remember { mutableStateOf("Lunapark Gezisi & Kitap Seti") }
    var monthlyTargetDiamonds by remember { mutableIntStateOf(300) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var tempRewardTitle by remember { mutableStateOf(monthlyRewardTitle) }
    var tempTargetDiamonds by remember { mutableStateOf(monthlyTargetDiamonds.toString()) }

    var rejectingTaskId by remember { mutableStateOf<String?>(null) }
    var rejectionReasonText by remember { mutableStateOf("") }

    val studentProfile = remember(studentDiamonds, monthlyRewardTitle, monthlyTargetDiamonds) {
        StudentProfileEntity(
            id = "demo_degerli_ogrencim",
            name = "Değerli Öğrencim",
            className = "5-A Sınıfı",
            diamonds = studentDiamonds,
            streakDays = 4,
            monthlyRewardTitle = monthlyRewardTitle,
            monthlyTargetDiamonds = monthlyTargetDiamonds
        )
    }

    // Görev kataloğu
    val allDefaultTasks = remember {
        DefaultTasks.ALL_TASKS.map { def ->
            TaskEntity(def.id, def.title, def.categoryId, def.subCategoryId, def.diamondReward)
        }
    }
    val default20Ids = remember { DefaultTasks.DEFAULT_DAILY_20_IDS }

    // Özel liste seçim kümesi
    val customSelectedTaskIds = remember {
        mutableStateListOf<String>().apply { addAll(default20Ids) }
    }

    // Görev durumları: Başlangıçta 4 tanesi veli onayı bekliyor durumunda olsun
    val taskStatusMap = remember {
        mutableStateMapOf<String, String>().apply {
            put("home_1", "WAITING_APPROVAL")
            put("home_2", "WAITING_APPROVAL")
            put("school_1", "WAITING_APPROVAL")
            put("religion_1", "WAITING_APPROVAL")
            put("home_3", "APPROVED")
            put("school_2", "APPROVED")
        }
    }

    val taskMap = remember { allDefaultTasks.associateBy { it.id } }

    val currentScheduleTaskIds = remember(customSelectedTaskIds.toList()) {
        customSelectedTaskIds.toList()
    }

    val dailyTasks: List<DailyTaskUiItem> = remember(currentScheduleTaskIds, taskStatusMap.toMap()) {
        currentScheduleTaskIds.mapNotNull { id ->
            val task = taskMap[id] ?: return@mapNotNull null
            val status = taskStatusMap[id] ?: "NOT_DONE"
            DailyTaskUiItem(
                task = task,
                log = TaskLogEntity(
                    id = "demo_parent_log_$id",
                    dateString = "2026-10-02",
                    studentId = "demo_degerli_ogrencim",
                    taskId = id,
                    status = status,
                    isCompletedByStudent = status == "WAITING_APPROVAL" || status == "APPROVED",
                    isApprovedByParent = status == "APPROVED",
                    diamondsAwarded = if (status == "APPROVED") 5 else 0
                )
            )
        }
    }

    val waitingTasks = remember(dailyTasks) {
        dailyTasks.filter { it.log.status == "WAITING_APPROVAL" || it.log.status == "STUDENT_DONE" }
    }

    val approvedCount = remember(dailyTasks) {
        dailyTasks.count { it.log.status == "APPROVED" }
    }

    val categoryCounts = remember(dailyTasks) {
        TaskCategory.entries.associate { cat ->
            val catTasks = dailyTasks.filter { it.task.categoryId == cat.id }
            val approved = catTasks.count { it.log.status == "APPROVED" }
            cat.id to Pair(approved, catTasks.size)
        }
    }

    val filteredTasks = remember(dailyTasks, selectedCategoryFilter) {
        if (selectedCategoryFilter.isNullOrBlank()) {
            dailyTasks
        } else {
            dailyTasks.filter { it.task.categoryId == selectedCategoryFilter }
        }
    }

    val badges = remember(studentDiamonds, approvedCount) {
        BadgeCatalog.getBadges(
            diamonds = studentDiamonds,
            streakDays = 4,
            approvedTaskCount = approvedCount,
            approvedReligionCount = dailyTasks.count { it.task.categoryId == "religion" && it.log.status == "APPROVED" }
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
                                modifier = Modifier.testTag("parent_demo_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Tanıtıma Dön",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column {
                                Text(
                                    text = "Saygıdeğer Velim",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Veli Deneme Girişi • Öğrenci: Değerli Öğrencim",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToRegister,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("parent_demo_top_register_btn")
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

                    // Tab bar
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Görev Onayı (${waitingTasks.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("Özel Liste", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTabIndex == 2,
                            onClick = { selectedTabIndex = 2 },
                            text = { Text("Rozet & Ödül", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            // ALT SABİT BUTON: Şimdi Kaydol (Veli Olarak)
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
                            .testTag("parent_demo_bottom_register_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FamilyRestroom,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Şimdi Kaydol (Veli Olarak Giriş Yap)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Veli olarak kaydedilir, tanıtım kapatılır ve hep Veli Menüsü açılır",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTabIndex) {
            0 -> {
                // TAB 0: Onay ve Takip
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .testTag("parent_demo_tasks_list"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Hero Bilgilendirme Kartı
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
                                            imageVector = Icons.Default.FamilyRestroom,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Veli: Saygıdeğer Velim • Öğrenci: Değerli Öğrencim",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Çocuğunuzun yaptığı görevleri aşağıda tek dokunuşla 'Onayla'yabilir veya 'Düzeltme İste'yebilirsiniz.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Onay Merkezi Kartı
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (waitingTasks.isNotEmpty()) StatusWaitingBg else Color(0xFFF0FDF4)
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (waitingTasks.isNotEmpty()) StatusWaitingBorder else Color(0xFF86EFAC)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (waitingTasks.isNotEmpty()) Icons.Default.HourglassTop else Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (waitingTasks.isNotEmpty()) Color(0xFFD97706) else Color(0xFF16A34A),
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (waitingTasks.isNotEmpty()) "Onay Bekleyenler (${waitingTasks.size})" else "Tüm Görevler İncelendi ✓",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (waitingTasks.isNotEmpty()) StatusWaitingText else Color(0xFF15803D)
                                        )
                                    }

                                    if (waitingTasks.isNotEmpty()) {
                                        Button(
                                            onClick = {
                                                waitingTasks.forEach {
                                                    taskStatusMap[it.task.id] = "APPROVED"
                                                    studentDiamonds += it.task.diamondReward
                                                }
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("✓ Tüm bekleyen görevler onaylandı! Öğrenci elmasları kazandı.")
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.DoneAll, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Tümünü Onayla", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Bugün onaylanan: $approvedCount / ${dailyTasks.size} • Çocuğun Elması: $studentDiamonds 💎",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Kategori filtreleri
                    item {
                        CategoryFilterChips(
                            selectedCategoryId = selectedCategoryFilter,
                            onCategorySelected = { selectedCategoryFilter = it },
                            categoryItemCounts = categoryCounts
                        )
                    }

                    // Görev kartları
                    items(filteredTasks, key = { it.task.id }) { item ->
                        TaskCard(
                            uiItem = item,
                            userRole = UserRole.PARENT,
                            onStudentDoneClick = {},
                            onParentApproveClick = {
                                taskStatusMap[item.task.id] = "APPROVED"
                                studentDiamonds += item.task.diamondReward
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("✓ '${item.task.title}' görevi onaylandı (+${item.task.diamondReward} Elmas).")
                                }
                            },
                            onParentRejectClick = {
                                rejectingTaskId = item.task.id
                                rejectionReasonText = ""
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            1 -> {
                // TAB 1: Özel 20 Görev Belirleme
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Veli Özel 20 Görev Listesi",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Çocuğunuz için her kategoriden tam 5 görev seçerek (toplam 20 görev) özel bir liste oluşturabilirsiniz.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                val homeCount = customSelectedTaskIds.count { taskMap[it]?.categoryId == "home" }
                                val schoolCount = customSelectedTaskIds.count { taskMap[it]?.categoryId == "school" }
                                val relCount = customSelectedTaskIds.count { taskMap[it]?.categoryId == "religion" }
                                val etqCount = customSelectedTaskIds.count { taskMap[it]?.categoryId == "etiquette" }

                                Text(
                                    text = "Seçilen: ${customSelectedTaskIds.size}/20 (Ev: $homeCount/5, Okul: $schoolCount/5, Dini: $relCount/5, Adap: $etqCount/5)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (customSelectedTaskIds.size == 20) Color(0xFF16A34A) else Color(0xFFD97706)
                                )
                            }
                        }
                    }

                    items(allDefaultTasks, key = { it.id }) { task ->
                        val isChecked = customSelectedTaskIds.contains(task.id)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    if (isChecked) {
                                        customSelectedTaskIds.remove(task.id)
                                    } else {
                                        customSelectedTaskIds.add(task.id)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        if (it) customSelectedTaskIds.add(task.id) else customSelectedTaskIds.remove(task.id)
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = task.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = "Kategori: ${task.categoryId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("✓ Özel liste başarıyla kaydedildi! Çocuğunuz artık bu 20 görevi görecek.")
                                    selectedTabIndex = 0
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Özel Listeyi Kaydet ve Çocuğa Uygula", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            2 -> {
                // TAB 2: Rozetler ve Aylık Ödül
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🎁 Aylık Hedef Ödülü", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    IconButton(onClick = {
                                        tempRewardTitle = monthlyRewardTitle
                                        tempTargetDiamonds = monthlyTargetDiamonds.toString()
                                        showRewardDialog = true
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Ödülü Düzenle", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Text(text = monthlyRewardTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Hedef: $studentDiamonds / $monthlyTargetDiamonds Elmas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        BadgesSection(badges = badges)
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Düzeltme İste Diyalogu
    if (rejectingTaskId != null) {
        AlertDialog(
            onDismissRequest = { rejectingTaskId = null },
            title = { Text("Düzeltme / Eksik Bildirimi") },
            text = {
                Column {
                    Text("Öğrenciye görevin neden onaylanmadığını açıklayınız:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectionReasonText,
                        onValueChange = { rejectionReasonText = it },
                        placeholder = { Text("Örn: Yatağını düzgünce sermelisin...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = rejectingTaskId ?: return@Button
                        taskStatusMap[id] = "REJECTED"
                        rejectingTaskId = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Düzeltme uyarısı öğrenciye iletildi.")
                        }
                    }
                ) {
                    Text("Gönder")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectingTaskId = null }) {
                    Text("İptal")
                }
            }
        )
    }

    // Aylık Ödül Düzenleme Diyalogu
    if (showRewardDialog) {
        AlertDialog(
            onDismissRequest = { showRewardDialog = false },
            title = { Text("Aylık Ödülü Belirle") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tempRewardTitle,
                        onValueChange = { tempRewardTitle = it },
                        label = { Text("Ödül Adı") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempTargetDiamonds,
                        onValueChange = { tempTargetDiamonds = it },
                        label = { Text("Hedef Elmas (Örn: 300)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        monthlyRewardTitle = tempRewardTitle.ifBlank { "Aylık Başarı Ödülü" }
                        monthlyTargetDiamonds = tempTargetDiamonds.toIntOrNull() ?: 300
                        showRewardDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("✓ Aylık ödül ve hedef başarıyla güncellendi.")
                        }
                    }
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRewardDialog = false }) { Text("İptal") }
            }
        )
    }
}
