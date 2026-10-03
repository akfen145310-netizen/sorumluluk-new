package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.ActivityCustomTask
import com.example.data.ActivityEntity
import com.example.data.ClassmateStatEntity
import com.example.data.ClassroomConfigEntity
import com.example.data.DailyTaskUiItem
import com.example.data.RegisteredUserEntity
import com.example.data.TaskEntity
import com.example.ui.components.ActivityTaskSettingsDialog
import com.example.ui.components.AddActivityDialog
import com.example.ui.components.EditClassroomDialog
import com.example.ui.components.QrCodeShareDialog
import com.example.ui.components.QrRegistrationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherScreen(
    classroomConfig: ClassroomConfigEntity?,
    registeredUsers: List<RegisteredUserEntity>,
    allActivities: List<ActivityEntity>,
    classmateStats: List<ClassmateStatEntity>,
    allTasks: List<TaskEntity>,
    activeDailyTasks: List<DailyTaskUiItem>,
    onUpdateClassroomInfo: (className: String, subTitle: String, activityName: String, joinCode: String, teacherPin: String, announcement: String) -> Unit,
    onAddActivity: (title: String, description: String, joinCode: String, targetDays: Int) -> Unit,
    onSetActiveActivity: (activityId: String, title: String) -> Unit,
    onDeleteActivity: (activityId: String, title: String) -> Unit,
    onSaveActivityTaskSettings: (activityId: String, title: String, systemTaskIds: List<String>, customTasks: List<ActivityCustomTask>) -> Unit,
    onAssignTasksToStudents: (studentIds: List<String>, taskIds: List<String>, title: String, groupName: String) -> Unit,
    onCompleteTaskForStudents: (studentIds: List<String>, taskId: String, taskTitle: String, diamonds: Int) -> Unit,
    onRegisterUser: (role: String, name: String, studentName: String, phone: String, onComplete: (Boolean, String) -> Unit) -> Unit,
    onDeleteRegisteredUser: (userId: String) -> Unit,
    onDeleteStudent: (studentId: String, studentName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showEditClassDialog by remember { mutableStateOf(false) }
    var showAddActivityDialog by remember { mutableStateOf(false) }
    var showQrShareDialog by remember { mutableStateOf(false) }
    var showManualRegisterDialog by remember { mutableStateOf(false) }
    var activityForTaskSettings by remember { mutableStateOf<ActivityEntity?>(null) }
    var studentToDelete by remember { mutableStateOf<ClassmateStatEntity?>(null) }
    var userToDelete by remember { mutableStateOf<RegisteredUserEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("teacher_screen_root")
    ) {
        // Tab Row
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Genel & Sınıf", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                icon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Etkinlikler", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                icon = { Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Öğrenciler (${classmateStats.size})", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                icon = { Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Kayıtlar (${registeredUsers.size})", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        when (selectedTab) {
            0 -> {
                // Tab 0: Class Overview & Announcement
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = classroomConfig?.className ?: "Sorumluluk Sınıfı",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = classroomConfig?.subtitle ?: "Haftalık Sorumluluk Hareketi",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }

                                    Row {
                                        IconButton(onClick = { showQrShareDialog = true }) {
                                            Icon(
                                                Icons.Default.QrCode,
                                                contentDescription = "QR Kod Paylaş",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(onClick = { showEditClassDialog = true }) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Sınıfı Düzenle",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Katılım Kodu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                        Text(classroomConfig?.joinCode ?: "---", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("Öğretmen PIN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                        Text(classroomConfig?.teacherPin ?: "1234", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("Kayıtlı Öğrenci", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                        Text("${classmateStats.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Announcement Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Günlük Öğretmen Duyurusu",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = classroomConfig?.dailyAnnouncement?.ifBlank { "Henüz bir duyuru girilmedi. Düzenle butonundan yeni duyuru ekleyebilirsiniz." }
                                        ?: "Henüz bir duyuru girilmedi.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Quick Actions
                    item {
                        Text(
                            text = "Hızlı İşlemler",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showEditClassDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Bilgileri Güncelle")
                            }

                            OutlinedButton(
                                onClick = { showQrShareDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("QR Paylaş")
                            }
                        }
                    }
                }
            }

            1 -> {
                // Tab 1: Activities Management
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Etkinlikler (${allActivities.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { showAddActivityDialog = true },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Yeni Etkinlik")
                            }
                        }
                    }

                    if (allActivities.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Kayıtlı etkinlik bulunamadı. Yeni bir etkinlik ekleyin.")
                            }
                        }
                    } else {
                        items(allActivities, key = { it.id }) { activity ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (activity.isActive) {
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                ),
                                border = if (activity.isActive) {
                                    androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                } else null
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = activity.title,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                if (activity.isActive) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = MaterialTheme.colorScheme.primary
                                                    ) {
                                                        Text(
                                                            text = "AKTİF",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onPrimary,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            if (activity.description.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = activity.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        IconButton(onClick = { onDeleteActivity(activity.id, activity.title) }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Etkinliği Sil",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (!activity.isActive) {
                                            OutlinedButton(
                                                onClick = { onSetActiveActivity(activity.id, activity.title) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Aktif Yap")
                                            }
                                        }

                                        Button(
                                            onClick = { activityForTaskSettings = activity },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Görev Ayarları")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Tab 2: Students List & Leaderboard
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Öğrenci İlerlemesi & Sıralama",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (classmateStats.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Henüz kayıtlı öğrenci bulunmuyor.")
                            }
                        }
                    } else {
                        items(classmateStats, key = { it.id }) { student ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = student.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("${student.diamonds} Elmas", style = MaterialTheme.typography.bodySmall)

                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("${student.streakDays} Gün", style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFE8F5E9)
                                        ) {
                                            Text(
                                                text = "${student.completedToday} Onaylı",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E7D32),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        IconButton(onClick = { studentToDelete = student }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Öğrenciyi Sil",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Tab 3: Registered Users
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kayıtlı Kullanıcılar (${registeredUsers.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { showManualRegisterDialog = true },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Kullanıcı Ekle")
                            }
                        }
                    }

                    if (registeredUsers.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Henüz kayıtlı veli veya öğrenci bulunmuyor.")
                            }
                        }
                    } else {
                        items(registeredUsers, key = { it.id }) { user ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = user.fullName,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (user.role == "TEACHER") Color(0xFFE8EAF6) else if (user.role == "PARENT") Color(0xFFFFF3E0) else Color(0xFFE0F7FA)
                                            ) {
                                                Text(
                                                    text = when (user.role) {
                                                        "TEACHER" -> "Öğretmen"
                                                        "PARENT" -> "Veli"
                                                        else -> "Öğrenci"
                                                    },
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (user.role) {
                                                        "TEACHER" -> Color(0xFF283593)
                                                        "PARENT" -> Color(0xFFE65100)
                                                        else -> Color(0xFF006064)
                                                    },
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        if (!user.linkedStudentName.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Bağlı Öğrenci: ${user.linkedStudentName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (!user.phoneOrContact.isNullOrBlank()) {
                                            Text(
                                                text = "İletişim: ${user.phoneOrContact}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(onClick = { userToDelete = user }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Kullanıcıyı Sil",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Edit Classroom Info
    if (showEditClassDialog) {
        EditClassroomDialog(
            currentConfig = classroomConfig,
            onDismiss = { showEditClassDialog = false },
            onSave = { className, subTitle, activityName, joinCode, teacherPin, announcement ->
                onUpdateClassroomInfo(className, subTitle, activityName, joinCode, teacherPin, announcement)
                showEditClassDialog = false
            }
        )
    }

    // Dialog: Add Activity
    if (showAddActivityDialog) {
        AddActivityDialog(
            onDismiss = { showAddActivityDialog = false },
            onAddActivity = { title, desc, joinCode, targetDays ->
                onAddActivity(title, desc, joinCode, targetDays)
                showAddActivityDialog = false
            }
        )
    }

    // Dialog: QR Code Share
    if (showQrShareDialog) {
        QrCodeShareDialog(
            config = classroomConfig,
            selectedActivity = allActivities.find { it.isActive },
            onDismiss = { showQrShareDialog = false },
            onOpenRegisterScreen = {
                showQrShareDialog = false
                showManualRegisterDialog = true
            }
        )
    }

    // Dialog: Activity Task Settings (20 Custom Slots + System Tasks + Group Assignment)
    activityForTaskSettings?.let { activity ->
        ActivityTaskSettingsDialog(
            activity = activity,
            allTasks = allTasks,
            students = classmateStats,
            registeredUsers = registeredUsers,
            onDismiss = { activityForTaskSettings = null },
            onSaveTaskSettings = { systemIds, customTasks ->
                onSaveActivityTaskSettings(activity.id, activity.title, systemIds, customTasks)
                activityForTaskSettings = null
            },
            onAssignTasksToStudents = { studentIds, taskIds, groupName ->
                onAssignTasksToStudents(studentIds, taskIds, activity.title, groupName)
            },
            onCompleteTaskForStudents = { studentIds, taskId, taskTitle, diamonds ->
                onCompleteTaskForStudents(studentIds, taskId, taskTitle, diamonds)
            }
        )
    }

    // Dialog: Manual Register User
    if (showManualRegisterDialog) {
        QrRegistrationDialog(
            config = classroomConfig,
            selectedActivity = allActivities.find { it.isActive },
            onDismiss = { showManualRegisterDialog = false },
            onRegister = { role, name, studentName, phone, onComplete ->
                onRegisterUser(role, name, studentName ?: "", phone ?: "") { ok, msg ->
                    onComplete(ok, msg)
                    if (ok) {
                        showManualRegisterDialog = false
                    }
                }
            }
        )
    }

    // Confirm Dialog: Delete Student
    studentToDelete?.let { student ->
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Öğrenciyi Sil") },
            text = { Text("'${student.name}' isimli öğrenciyi ve ilişkili tüm kayıtları silmek istediğinizden emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteStudent(student.id, student.name)
                        studentToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToDelete = null }) {
                    Text("İptal")
                }
            }
        )
    }

    // Confirm Dialog: Delete Registered User
    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Kullanıcıyı Sil") },
            text = { Text("'${user.fullName}' isimli kullanıcının kaydını silmek istediğinizden emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteRegisteredUser(user.id)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("İptal")
                }
            }
        )
    }
}
