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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.ActivityEntity
import com.example.data.ClassmateStatEntity
import com.example.data.DefaultTasks
import kotlinx.coroutines.launch

/**
 * Öğretmen Deneme Ekranı:
 * Adı: Sayın, Soyadı: Öğretmenim
 * Sınıf: 5-A Sınıfı Değerler & Sorumluluk
 * Öğretmenin yapabileceği tüm işlemleri (Sıralama, Duyuru & Sınıf Yönetimi,
 * Etkinlik Tanımlama, Görev Atama ve Karekod Paylaşımı) canlı olarak deneyimlemesini sağlar.
 */
@Composable
fun TeacherDemoScreen(
    onNavigateToRegister: () -> Unit,
    onBackToIntro: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var className by remember { mutableStateOf("5-A Sınıfı") }
    var dailyAnnouncement by remember { mutableStateOf("Günün Sözü: Ahlak ve sorumluluk, insanı yücelten iki kanattır.") }
    var joinCode by remember { mutableStateOf("DEGERLER-5A-2026") }

    var showEditAnnouncementDialog by remember { mutableStateOf(false) }
    var tempAnnouncement by remember { mutableStateOf(dailyAnnouncement) }

    var showAddActivityDialog by remember { mutableStateOf(false) }
    var newActivityTitle by remember { mutableStateOf("") }
    var newActivityDays by remember { mutableStateOf("30") }

    val simulatedStudents = remember {
        mutableStateListOf(
            ClassmateStatEntity("1", "Ahmet Yılmaz", completedToday = 18, totalToday = 20, diamonds = 220, streakDays = 5),
            ClassmateStatEntity("2", "Zeynep Kaya", completedToday = 20, totalToday = 20, diamonds = 260, streakDays = 7),
            ClassmateStatEntity("3", "Değerli Öğrencim", completedToday = 16, totalToday = 20, diamonds = 180, streakDays = 4),
            ClassmateStatEntity("4", "Elif Demir", completedToday = 14, totalToday = 20, diamonds = 150, streakDays = 3),
            ClassmateStatEntity("5", "Mustafa Şahin", completedToday = 12, totalToday = 20, diamonds = 135, streakDays = 2)
        )
    }

    val activities = remember {
        mutableStateListOf(
            ActivityEntity("act_1", "Haftalık Sorumluluk Hareketi", "Ev, okul ve ahlaki değerler takibi", joinCode, "2026-10-01", 30, true),
            ActivityEntity("act_2", "Ramazan & Maneviyat Hareketi", "İbadet ve adabı muaşeret öncelikli takip", joinCode, "2026-10-01", 30, false)
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
                                modifier = Modifier.testTag("teacher_demo_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Tanıtıma Dön",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column {
                                Text(
                                    text = "Sayın Öğretmenim",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Öğretmen Deneme Girişi • $className",
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
                            modifier = Modifier.testTag("teacher_demo_top_register_btn")
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

                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Sıralama", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("Sınıf & Duyuru", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTabIndex == 2,
                            onClick = { selectedTabIndex = 2 },
                            text = { Text("Etkinlikler", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTabIndex == 3,
                            onClick = { selectedTabIndex = 3 },
                            text = { Text("Görevler & Ödül", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            // ALT SABİT BUTON: Şimdi Kaydol (Öğretmen Olarak)
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
                            .testTag("teacher_demo_bottom_register_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Şimdi Kaydol (Öğretmen Olarak Giriş Yap)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Öğretmen olarak kaydedilir, tanıtım kapatılır ve hep Öğretmen Menüsü açılır",
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
                // TAB 0: Sınıf Sıralaması
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = "Öğretmen: Sayın Öğretmenim", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text(text = "Sınıfınızdaki tüm öğrencilerin anlık tamamlama durumunu ve elmaslarını buradan izleyebilirsiniz.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    item {
                        Text(text = "🏆 Canlı Sınıf Sıralaması (${simulatedStudents.size} Öğrenci)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }

                    items(simulatedStudents.sortedByDescending { it.diamonds }, key = { it.id }) { student ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = student.name.take(1), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = student.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = "Bugün: ${student.completedToday}/${student.totalToday} görev • Seri: ${student.streakDays} gün", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "${student.diamonds} 💎", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            val index = simulatedStudents.indexOfFirst { it.id == student.id }
                                            if (index != -1) {
                                                simulatedStudents[index] = simulatedStudents[index].copy(diamonds = simulatedStudents[index].diamonds + 10)
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("✓ ${student.name} öğrencisine +10 Elmas başarı ödülü verildi!")
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("+10 Elmas", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }

            1 -> {
                // TAB 1: Sınıf & Duyuru
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📢 Günlük Sınıf Duyurusu", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Button(
                                        onClick = {
                                            tempAnnouncement = dailyAnnouncement
                                            showEditAnnouncementDialog = true
                                        },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Düzenle", fontSize = 12.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = dailyAnnouncement, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = "Sınıf Katılım Kodu & Karekod", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Öğrenci ve veliler bu kod ile sınıfınıza katılır:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = joinCode, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                        Icon(Icons.Default.QrCode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }

            2 -> {
                // TAB 2: Etkinlikler
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Sınıf Etkinlikleri (${activities.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Button(
                                onClick = { showAddActivityDialog = true },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Yeni Etkinlik", fontSize = 12.sp)
                            }
                        }
                    }

                    items(activities, key = { it.id }) { act ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(if (act.isActive) 2.dp else 1.dp, if (act.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = act.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = act.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (act.isActive) {
                                    Surface(color = Color(0xFF16A34A), shape = RoundedCornerShape(12.dp)) {
                                        Text(text = "Aktif", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            activities.replaceAll { it.copy(isActive = it.id == act.id) }
                                            coroutineScope.launch { snackbarHostState.showSnackbar("✓ '${act.title}' aktif etkinlik olarak belirlendi.") }
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Aktif Yap", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }

            3 -> {
                // TAB 3: Görev Havuzu
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Standart Müfredat Görev Havuzu (80 Görev)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Ev, Okul, Dini ve Görgü kuralları alanlarındaki hazır görevleri inceleyebilirsiniz.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    items(DefaultTasks.ALL_TASKS.take(15)) { task ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = task.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = "Kategori: ${task.categoryId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(text = "+${task.diamondReward} 💎", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }

    if (showEditAnnouncementDialog) {
        AlertDialog(
            onDismissRequest = { showEditAnnouncementDialog = false },
            title = { Text("Günün Duyurusu") },
            text = {
                OutlinedTextField(
                    value = tempAnnouncement,
                    onValueChange = { tempAnnouncement = it },
                    label = { Text("Duyuru / Günün Sözü") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        dailyAnnouncement = tempAnnouncement.ifBlank { "Günün sözü belirlenmedi." }
                        showEditAnnouncementDialog = false
                        coroutineScope.launch { snackbarHostState.showSnackbar("✓ Sınıf duyurusu güncellendi.") }
                    }
                ) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = { showEditAnnouncementDialog = false }) { Text("İptal") }
            }
        )
    }

    if (showAddActivityDialog) {
        AlertDialog(
            onDismissRequest = { showAddActivityDialog = false },
            title = { Text("Yeni Etkinlik Başlat") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newActivityTitle,
                        onValueChange = { newActivityTitle = it },
                        label = { Text("Etkinlik Başlığı") },
                        placeholder = { Text("Örn: Kitap Okuma & Ahlak Hareketi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newActivityDays,
                        onValueChange = { newActivityDays = it },
                        label = { Text("Süre (Gün)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newActivityTitle.isNotBlank()) {
                            activities.add(
                                ActivityEntity("act_${System.currentTimeMillis()}", newActivityTitle.trim(), "Öğretmen özel sınıf etkinliği", joinCode, "2026-10-02", newActivityDays.toIntOrNull() ?: 30, false)
                            )
                            showAddActivityDialog = false
                            newActivityTitle = ""
                            coroutineScope.launch { snackbarHostState.showSnackbar("✓ Yeni etkinlik başarıyla eklendi.") }
                        }
                    }
                ) { Text("Ekle") }
            },
            dismissButton = {
                TextButton(onClick = { showAddActivityDialog = false }) { Text("İptal") }
            }
        )
    }
}
