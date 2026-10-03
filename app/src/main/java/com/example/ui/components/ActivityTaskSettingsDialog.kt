package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ActivityCustomTask
import com.example.data.ActivityCustomTaskHelper
import com.example.data.ActivityEntity
import com.example.data.ClassmateStatEntity
import com.example.data.DefaultTasks
import com.example.data.RegisteredUserEntity
import com.example.data.TaskEntity

/**
 * Activity Task Settings Dialog (Etkinlik Görev Ayarları ve Yönetimi).
 * Accessible exclusively by Teacher.
 *
 * Features:
 * 1. Select tasks from system catalog (Ev, Okul, İbadet, Adab-ı Muaşeret).
 * 2. "Sistem Dışı Görev Eklemek İstiyorum" button to reveal 20 manually fillable task slots.
 * 3. Each slot can be filled manually and has an individual activation toggle switch.
 *    The teacher can activate as many of the 20 tasks as desired.
 * 4. Assign selected & active tasks to student groups (All class, activity participants, or custom selection).
 * 5. Complete task(s) directly for any number of students with diamond rewards.
 */
@Composable
fun ActivityTaskSettingsDialog(
    activity: ActivityEntity,
    allTasks: List<TaskEntity>,
    students: List<ClassmateStatEntity>,
    registeredUsers: List<RegisteredUserEntity>,
    onDismiss: () -> Unit,
    onSaveTaskSettings: (selectedSystemIds: List<String>, customTasks: List<ActivityCustomTask>) -> Unit,
    onAssignTasksToStudents: (studentIds: List<String>, taskIds: List<String>, groupName: String) -> Unit,
    onCompleteTaskForStudents: (studentIds: List<String>, taskId: String, taskTitle: String, diamonds: Int) -> Unit
) {
    // 0: Görev Tanımlama (Sistem + 20 Özel Görev)
    // 1: Gruba / Öğrencilere Görev Ekleme
    // 2: Öğretmen Toplu Görev Tamamlama
    var selectedTab by remember { mutableStateOf(0) }

    // Parse existing custom tasks (guarantees exactly 20 slots)
    val initialCustomTasks = remember(activity.customTasksJson) {
        ActivityCustomTaskHelper.parseJson(activity.customTasksJson)
    }
    val customTaskList = remember {
        mutableStateListOf<ActivityCustomTask>().apply {
            addAll(initialCustomTasks)
        }
    }

    // Parse existing active task IDs
    val initialActiveIds = remember(activity.activeTaskIdsCsv) {
        if (activity.activeTaskIdsCsv.isNotBlank()) {
            activity.activeTaskIdsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        } else {
            DefaultTasks.DEFAULT_DAILY_20_IDS.toSet()
        }
    }

    // Track selected system task IDs
    val selectedSystemTaskIds = remember {
        val systemIds = initialActiveIds.filter { !it.startsWith("custom_act_") }.toSet()
        mutableStateListOf<String>().apply { addAll(systemIds) }
    }

    // Toggle for "Sistem Dışı Görev Eklemek İstiyorum" section
    var showCustomTasksSection by remember {
        val hasActiveCustom = customTaskList.any { it.isActive || it.title.isNotBlank() }
        mutableStateOf(hasActiveCustom)
    }

    // Search & Filter for System Tasks
    var systemSearchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) } // null = Tümü

    // Group Assignment State
    var assignmentTargetType by remember { mutableStateOf(0) } // 0: Tüm Sınıf, 1: Etkinliğe Kayıtlılar, 2: Özel Seçim
    val selectedStudentIdsForAssignment = remember {
        mutableStateListOf<String>().apply { addAll(students.map { it.id }) }
    }

    // Teacher Task Completion State
    var selectedTaskIdToComplete by remember { mutableStateOf<String?>(null) }
    val selectedStudentIdsForCompletion = remember {
        mutableStateListOf<String>()
    }

    val activeCustomCount by remember {
        derivedStateOf { customTaskList.count { it.isActive && it.title.isNotBlank() } }
    }
    val totalActiveTaskCount by remember {
        derivedStateOf { selectedSystemTaskIds.size + activeCustomCount }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 10.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Dialog Header
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = activity.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.errorContainer
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Lock,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(10.dp),
                                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    text = "Öğretmen Yetkili",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "🏷️ Kod: ${activity.joinCode} • Toplam Aktif Görev: $totalActiveTaskCount",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Kapat")
                            }
                        }
                    }
                }

                // 3 Top Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "📝 Görevler ($totalActiveTaskCount)",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "👥 Gruba Ata",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "⭐ Tamamlama",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Tab Content Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> TaskDefinitionTabContent(
                            activity = activity,
                            allTasks = allTasks,
                            selectedSystemTaskIds = selectedSystemTaskIds,
                            customTaskList = customTaskList,
                            showCustomTasksSection = showCustomTasksSection,
                            onToggleCustomTasksSection = { showCustomTasksSection = !showCustomTasksSection },
                            systemSearchQuery = systemSearchQuery,
                            onSystemSearchChange = { systemSearchQuery = it },
                            selectedCategoryFilter = selectedCategoryFilter,
                            onCategoryFilterChange = { selectedCategoryFilter = it },
                            onToggleSystemTask = { taskId ->
                                if (selectedSystemTaskIds.contains(taskId)) {
                                    selectedSystemTaskIds.remove(taskId)
                                } else {
                                    selectedSystemTaskIds.add(taskId)
                                }
                            },
                            onSelectDefault20 = {
                                selectedSystemTaskIds.clear()
                                selectedSystemTaskIds.addAll(DefaultTasks.DEFAULT_DAILY_20_IDS)
                            },
                            onClearAllSystem = {
                                selectedSystemTaskIds.clear()
                            },
                            onCustomTaskTitleChange = { index, newTitle ->
                                val current = customTaskList[index]
                                customTaskList[index] = current.copy(title = newTitle)
                            },
                            onCustomTaskActiveToggle = { index, isActive ->
                                val current = customTaskList[index]
                                customTaskList[index] = current.copy(isActive = isActive)
                            },
                            onSave = {
                                onSaveTaskSettings(selectedSystemTaskIds.toList(), customTaskList.toList())
                            }
                        )

                        1 -> GroupAssignmentTabContent(
                            activity = activity,
                            totalActiveTaskCount = totalActiveTaskCount,
                            students = students,
                            registeredUsers = registeredUsers,
                            assignmentTargetType = assignmentTargetType,
                            onTargetTypeChange = { assignmentTargetType = it },
                            selectedStudentIds = selectedStudentIdsForAssignment,
                            onToggleStudent = { id ->
                                if (selectedStudentIdsForAssignment.contains(id)) {
                                    selectedStudentIdsForAssignment.remove(id)
                                } else {
                                    selectedStudentIdsForAssignment.add(id)
                                }
                            },
                            onSelectAllStudents = {
                                selectedStudentIdsForAssignment.clear()
                                selectedStudentIdsForAssignment.addAll(students.map { it.id })
                            },
                            onDeselectAllStudents = {
                                selectedStudentIdsForAssignment.clear()
                            },
                            onApplyAssignment = {
                                val targetIds = when (assignmentTargetType) {
                                    0 -> students.map { it.id }
                                    1 -> {
                                        val registeredStudentNames = registeredUsers
                                            .filter { it.activityName == activity.title }
                                            .mapNotNull { it.linkedStudentName ?: it.fullName }
                                            .toSet()
                                        students.filter { registeredStudentNames.contains(it.name) }.map { it.id }
                                            .ifEmpty { students.map { it.id } }
                                    }
                                    else -> selectedStudentIdsForAssignment.toList()
                                }

                                val activeCustomTaskIds = customTaskList
                                    .filter { it.isActive && it.title.isNotBlank() }
                                    .map { "custom_act_${activity.id}_${it.slotIndex}" }
                                val combinedIds = (selectedSystemTaskIds + activeCustomTaskIds).distinct()

                                val groupLabel = when (assignmentTargetType) {
                                    0 -> "Tüm Sınıf"
                                    1 -> "${activity.title} Katılımcıları"
                                    else -> "${targetIds.size} Seçili Öğrenci"
                                }

                                onAssignTasksToStudents(targetIds, combinedIds, groupLabel)
                            }
                        )

                        2 -> TeacherCompletionTabContent(
                            activity = activity,
                            allTasks = allTasks,
                            selectedSystemTaskIds = selectedSystemTaskIds,
                            customTaskList = customTaskList,
                            students = students,
                            selectedTaskId = selectedTaskIdToComplete,
                            onSelectTask = { selectedTaskIdToComplete = it },
                            selectedStudentIds = selectedStudentIdsForCompletion,
                            onToggleStudent = { id ->
                                if (selectedStudentIdsForCompletion.contains(id)) {
                                    selectedStudentIdsForCompletion.remove(id)
                                } else {
                                    selectedStudentIdsForCompletion.add(id)
                                }
                            },
                            onSelectAllStudents = {
                                selectedStudentIdsForCompletion.clear()
                                selectedStudentIdsForCompletion.addAll(students.map { it.id })
                            },
                            onDeselectAllStudents = {
                                selectedStudentIdsForCompletion.clear()
                            },
                            onCompleteTask = { taskId, diamonds ->
                                val taskTitle = if (taskId.startsWith("custom_act_")) {
                                    val slot = taskId.substringAfterLast("_").toIntOrNull() ?: 1
                                    customTaskList.getOrNull(slot - 1)?.title ?: "Özel Görev"
                                } else {
                                    allTasks.firstOrNull { it.id == taskId }?.title ?: "Sistem Görevi"
                                }
                                onCompleteTaskForStudents(
                                    selectedStudentIdsForCompletion.toList(),
                                    taskId,
                                    taskTitle,
                                    diamonds
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tab 1: Görev Tanımlama (Sistem Havuzu + 20 Özel Görev Alanı)
 */
@Composable
private fun TaskDefinitionTabContent(
    activity: ActivityEntity,
    allTasks: List<TaskEntity>,
    selectedSystemTaskIds: List<String>,
    customTaskList: List<ActivityCustomTask>,
    showCustomTasksSection: Boolean,
    onToggleCustomTasksSection: () -> Unit,
    systemSearchQuery: String,
    onSystemSearchChange: (String) -> Unit,
    selectedCategoryFilter: String?,
    onCategoryFilterChange: (String?) -> Unit,
    onToggleSystemTask: (String) -> Unit,
    onSelectDefault20: () -> Unit,
    onClearAllSystem: () -> Unit,
    onCustomTaskTitleChange: (index: Int, title: String) -> Unit,
    onCustomTaskActiveToggle: (index: Int, isActive: Boolean) -> Unit,
    onSave: () -> Unit
) {
    val filteredSystemTasks = remember(allTasks, systemSearchQuery, selectedCategoryFilter) {
        allTasks.filter { task ->
            val matchesCategory = selectedCategoryFilter == null || task.categoryId == selectedCategoryFilter
            val matchesSearch = systemSearchQuery.isBlank() ||
                task.title.contains(systemSearchQuery, ignoreCase = true)
            matchesCategory && matchesSearch && !task.id.startsWith("custom_act_")
        }
    }

    val activeCustomCount = customTaskList.count { it.isActive && it.title.isNotBlank() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        // PROMINENT BUTTON: "Sistem Dışı Görev Eklemek İstiyorum"
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleCustomTasksSection() }
                    .testTag("btn_custom_tasks_section"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (showCustomTasksSection) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (showCustomTasksSection) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (showCustomTasksSection) Icons.Default.Edit else Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "✨ Sistem Dışı Görev Eklemek İstiyorum",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Elle doldurulabilen 20 özel görev alanı ($activeCustomCount aktif)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (showCustomTasksSection) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = if (showCustomTasksSection) "Kapat ▲" else "Aç / Düzenle ▼",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 20 MANUALLY FILLABLE TASK SLOTS (Visible when toggled)
        if (showCustomTasksSection) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "✍️ 20 Adet Elle Doldurulabilir Özel Görev Alanı",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "İstediğiniz alanları doldurun ve yanındaki tuşla aktifleştirin.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "$activeCustomCount / 20 Aktif",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Exactly 20 slots
            itemsIndexed(customTaskList) { index, item ->
                val slotNumber = index + 1
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("custom_task_slot_$slotNumber"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.isActive && item.title.isNotBlank())
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (item.isActive && item.title.isNotBlank()) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (item.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$slotNumber",
                                            color = if (item.isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Görev $slotNumber",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Text(
                                        text = "+${item.diamondReward} 💎",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Activation Switch
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (item.isActive) "Aktif" else "Pasif",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isActive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = item.isActive,
                                    onCheckedChange = { onCustomTaskActiveToggle(index, it) },
                                    modifier = Modifier.testTag("switch_slot_$slotNumber"),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF2E7D32)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = item.title,
                            onValueChange = { onCustomTaskTitleChange(index, it) },
                            placeholder = { Text("Örn: Kitap okuma, fidan dikme, nezaket görevi...") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_slot_$slotNumber"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))

            // SYSTEM TASKS CATALOG SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📚 Sistem Görev Havuzundan Seçim",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${selectedSystemTaskIds.size} sistem görevi seçili",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    OutlinedButton(
                        onClick = onSelectDefault20,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Varsayılan 20", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedButton(
                        onClick = onClearAllSystem,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Temizle", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = systemSearchQuery,
                onValueChange = onSystemSearchChange,
                placeholder = { Text("Sistem görevlerinde ara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (systemSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSystemSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Temizle")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    null to "Tümü",
                    "home" to "🏠 Ev",
                    "school" to "🏫 Okul",
                    "religion" to "🕌 İbadet",
                    "etiquette" to "🤝 Adab"
                ).forEach { (catId, label) ->
                    FilterChip(
                        selected = selectedCategoryFilter == catId,
                        onClick = { onCategoryFilterChange(catId) },
                        label = { Text(label, fontSize = 11.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // List of system tasks
        items(filteredSystemTasks, key = { it.id }) { task ->
            val isSelected = selectedSystemTaskIds.contains(task.id)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clickable { onToggleSystemTask(task.id) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onToggleSystemTask(task.id) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "+5 💎",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // SAVE BUTTON AT THE BOTTOM
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_activity_settings"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "✓ Görev Ayarlarını Kaydet (${selectedSystemTaskIds.size + activeCustomCount} Görev)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(70.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

/**
 * Tab 2: Gruba / Öğrencilere Görev Ekleme ve Atama
 */
@Composable
private fun GroupAssignmentTabContent(
    activity: ActivityEntity,
    totalActiveTaskCount: Int,
    students: List<ClassmateStatEntity>,
    registeredUsers: List<RegisteredUserEntity>,
    assignmentTargetType: Int,
    onTargetTypeChange: (Int) -> Unit,
    selectedStudentIds: List<String>,
    onToggleStudent: (String) -> Unit,
    onSelectAllStudents: () -> Unit,
    onDeselectAllStudents: () -> Unit,
    onApplyAssignment: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "👥 Görevleri Gruba veya Sınıfa Ekle / Ata",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "'${activity.title}' etkinliğine ait $totalActiveTaskCount adet aktif görevi seçtiğiniz öğrenci grubunun günlük programı olarak uygulayabilirsiniz.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Hedef Öğrenci Grubunu Seçiniz:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option 0: Tüm Sınıf
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (assignmentTargetType == 0) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.5.dp, if (assignmentTargetType == 0) MaterialTheme.colorScheme.primary else Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTargetTypeChange(0) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (assignmentTargetType == 0) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Tüm Sınıf (Bütün Kayıtlı Öğrenciler)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("${students.size} öğrencinin tamamına anında uygulanır.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Option 1: Sadece Bu Etkinlikteki Öğrenciler
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (assignmentTargetType == 1) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.5.dp, if (assignmentTargetType == 1) MaterialTheme.colorScheme.primary else Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTargetTypeChange(1) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (assignmentTargetType == 1) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Sadece '${activity.title}' Katılımcıları", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("Yalnızca bu etkinliğe katılmış öğrencilere atanır.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Option 2: Özel Öğrenci Grubu Seç
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (assignmentTargetType == 2) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.5.dp, if (assignmentTargetType == 2) MaterialTheme.colorScheme.primary else Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTargetTypeChange(2) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (assignmentTargetType == 2) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Özel Öğrenci Grubu Seç (${selectedStudentIds.size} Seçili)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("Aşağıdaki listeden dilediğiniz öğrencileri tek tek işaretleyin.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (assignmentTargetType == 2) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Öğrenci Listesi:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Row {
                        OutlinedButton(onClick = onSelectAllStudents, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp), shape = RoundedCornerShape(8.dp)) {
                            Text("Tümünü Seç", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(onClick = onDeselectAllStudents, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp), shape = RoundedCornerShape(8.dp)) {
                            Text("Temizle", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Student checklist when Option 2 is selected
        if (assignmentTargetType == 2) {
            items(students) { student ->
                val isSelected = selectedStudentIds.contains(student.id)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { onToggleStudent(student.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { onToggleStudent(student.id) },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = student.name,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Text(
                            "${student.diamonds} 💎",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onApplyAssignment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_apply_group_assignment"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Group, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📋 Seçilen Gruba Görevleri Ata ve Programı Güncelle",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(70.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

/**
 * Tab 3: Öğretmen Toplu Görev Tamamlama ("öğretmen istediği kadar öğrenciye yeni görevi tamamlayabilmeli")
 */
@Composable
private fun TeacherCompletionTabContent(
    activity: ActivityEntity,
    allTasks: List<TaskEntity>,
    selectedSystemTaskIds: List<String>,
    customTaskList: List<ActivityCustomTask>,
    students: List<ClassmateStatEntity>,
    selectedTaskId: String?,
    onSelectTask: (String) -> Unit,
    selectedStudentIds: List<String>,
    onToggleStudent: (String) -> Unit,
    onSelectAllStudents: () -> Unit,
    onDeselectAllStudents: () -> Unit,
    onCompleteTask: (taskId: String, diamonds: Int) -> Unit
) {
    // Collect active task options from this activity
    val activeTaskOptions = remember(allTasks, selectedSystemTaskIds, customTaskList) {
        val list = mutableListOf<Pair<String, String>>() // id to title

        // 1. Custom tasks that are active & filled
        customTaskList.filter { it.isActive && it.title.isNotBlank() }.forEach {
            list.add("custom_act_${activity.id}_${it.slotIndex}" to "✨ ${it.title} (Özel Görev)")
        }

        // 2. Selected system tasks
        val taskMap = allTasks.associateBy { it.id }
        selectedSystemTaskIds.forEach { sysId ->
            val title = taskMap[sysId]?.title ?: sysId
            list.add(sysId to "📚 $title (Sistem Görevi)")
        }

        list
    }

    var completionSuccessMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "⭐ Öğretmen Doğrudan Görev Tamamlama",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Öğretmen olarak dilediğiniz görevi seçip, istediğiniz kadar öğrenci için anında tamamlayabilir ve elmaslarını (+5 💎) verebilirsiniz.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (completionSuccessMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = completionSuccessMessage!!,
                            color = Color(0xFF1B5E20),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "1. Tamamlanacak Görevi Seçiniz:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (activeTaskOptions.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Henüz bu etkinlik için aktif bir görev tanımlanmadı. Lütfen 'Görevler' sekmesinden sistemden görev seçin veya özel 20 alandan birini aktifleştirin.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        } else {
            items(activeTaskOptions) { (taskId, taskTitle) ->
                val isSelected = selectedTaskId == taskId
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    border = BorderStroke(1.5.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { onSelectTask(taskId) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = taskTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = "+5 💎",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2. Görevi Tamamlayacak Öğrencileri Seçiniz (${selectedStudentIds.size}/${students.size}):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    OutlinedButton(onClick = onSelectAllStudents, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp), shape = RoundedCornerShape(8.dp)) {
                        Text("Tümünü Seç", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedButton(onClick = onDeselectAllStudents, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp), shape = RoundedCornerShape(8.dp)) {
                        Text("Temizle", fontSize = 11.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(students) { student ->
            val isSelected = selectedStudentIds.contains(student.id)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clickable { onToggleStudent(student.id) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onToggleStudent(student.id) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = student.name,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Text(
                        "Mevcut: ${student.diamonds} 💎",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            val isButtonEnabled = selectedTaskId != null && selectedStudentIds.isNotEmpty()
            Button(
                onClick = {
                    if (selectedTaskId != null) {
                        onCompleteTask(selectedTaskId, 5)
                        completionSuccessMessage = "✓ Seçili ${selectedStudentIds.size} öğrenci için görev başarıyla onaylandı ve +5'er elmas eklendi!"
                    }
                },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_complete_task_for_students"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "✓ Seçili ${selectedStudentIds.size} Öğrenci İçin Görevi Tamamla (+5 💎)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(70.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}
