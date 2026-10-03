package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Badge
import com.example.data.DailyScheduleEntity
import com.example.data.DailyTaskUiItem
import com.example.data.DefaultTasks
import com.example.data.EtiquetteSubCategory
import com.example.data.StudentProfileEntity
import com.example.data.TaskCategory
import com.example.data.TaskEntity
import com.example.data.TaskLogEntity
import com.example.ui.UserRole
import com.example.ui.components.BadgesSection
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.TaskCard
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.ui.theme.StatusWaitingBg
import com.example.ui.theme.StatusWaitingBorder
import com.example.ui.theme.StatusWaitingText

/**
 * Module 5: The Parent Dashboard
 * Features:
 * 1. Child Selection & Overview: Switch between linked children, view real-time diamonds and monthly progress.
 * 2. One-Tap Approval System: List tasks with STUDENT_DONE status, one-tap 'Approve' (Onayla) awarding +5 diamonds atomically via Firestore transaction.
 * 3. Task Configuration Screen: Toggle between Teacher's List and Custom Selection (strictly 5 tasks per category, total 20).
 */
@Composable
fun ParentScreen(
    profile: StudentProfileEntity?,
    allStudentProfiles: List<StudentProfileEntity>,
    selectedChildId: String,
    onSelectChild: (String) -> Unit,
    tasks: List<DailyTaskUiItem>,
    badges: List<Badge> = emptyList(),
    allTasks: List<TaskEntity>,
    schedule: DailyScheduleEntity?,
    waitingApprovalLogs: List<TaskLogEntity>,
    isCustomizingSchedule: Boolean,
    customSelectedTaskIds: Set<String>,
    selectedCategoryFilter: String?,
    onCategoryFilterSelected: (String?) -> Unit,
    onApproveTask: (String) -> Unit,
    onApproveAllWaitingTasks: () -> Unit,
    onRejectTask: (String, String) -> Unit,
    onStartCustomizingSchedule: () -> Unit,
    onCancelCustomizingSchedule: () -> Unit,
    onToggleCustomTask: (TaskEntity) -> Boolean,
    onSaveCustomSchedule: () -> Boolean,
    onResetToTeacherSchedule: () -> Unit,
    onUpdateMonthlyReward: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isCustomizingSchedule) {
        CustomScheduleBuilderScreen(
            allTasks = allTasks,
            selectedIds = customSelectedTaskIds,
            onToggleTask = onToggleCustomTask,
            onSave = onSaveCustomSchedule,
            onCancel = onCancelCustomizingSchedule,
            onResetToDefault = onResetToTeacherSchedule,
            modifier = modifier
        )
    } else {
        ParentDashboardContent(
            profile = profile,
            allStudentProfiles = allStudentProfiles,
            selectedChildId = selectedChildId,
            onSelectChild = onSelectChild,
            tasks = tasks,
            badges = badges,
            schedule = schedule,
            waitingApprovalLogs = waitingApprovalLogs,
            selectedCategoryFilter = selectedCategoryFilter,
            onCategoryFilterSelected = onCategoryFilterSelected,
            onApproveTask = onApproveTask,
            onApproveAllWaitingTasks = onApproveAllWaitingTasks,
            onRejectTask = onRejectTask,
            onStartCustomizing = onStartCustomizingSchedule,
            onResetToDefault = onResetToTeacherSchedule,
            onUpdateMonthlyReward = onUpdateMonthlyReward,
            modifier = modifier
        )
    }
}

@Composable
private fun ParentDashboardContent(
    profile: StudentProfileEntity?,
    allStudentProfiles: List<StudentProfileEntity>,
    selectedChildId: String,
    onSelectChild: (String) -> Unit,
    tasks: List<DailyTaskUiItem>,
    badges: List<Badge> = emptyList(),
    schedule: DailyScheduleEntity?,
    waitingApprovalLogs: List<TaskLogEntity>,
    selectedCategoryFilter: String?,
    onCategoryFilterSelected: (String?) -> Unit,
    onApproveTask: (String) -> Unit,
    onApproveAllWaitingTasks: () -> Unit,
    onRejectTask: (String, String) -> Unit,
    onStartCustomizing: () -> Unit,
    onResetToDefault: () -> Unit,
    onUpdateMonthlyReward: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRewardDialog by remember { mutableStateOf(false) }
    var showRejectDialogForTaskId by remember { mutableStateOf<String?>(null) }
    var rejectionReasonInput by remember { mutableStateOf("") }

    val taskMap = remember(tasks) { tasks.associateBy { it.task.id } }

    // Filter tasks that are STUDENT_DONE or in waitingApprovalLogs
    val waitingTasks = remember(tasks, waitingApprovalLogs) {
        val waitingIds = waitingApprovalLogs.map { it.taskId }.toSet()
        tasks.filter { item ->
            waitingIds.contains(item.task.id) || (item.log.isCompletedByStudent && !item.log.isApprovedByParent)
        }
    }

    val filteredTasks = if (selectedCategoryFilter == null) {
        tasks
    } else {
        tasks.filter { it.task.categoryId == selectedCategoryFilter }
    }

    val approvedCountToday = tasks.count { it.log.status == "APPROVED" || it.log.isApprovedByParent }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        // Section 1: Child Selection Header
        item {
            ChildSelectorSection(
                allProfiles = allStudentProfiles,
                selectedChildId = selectedChildId,
                onSelectChild = onSelectChild
            )
        }

        // Section 2: Child Overview Card (Diamonds & Monthly Reward Progress)
        item {
            ChildOverviewCard(
                profile = profile,
                onEditRewardClick = { showRewardDialog = true }
            )
        }

        // Badges Section (Child's achievements and unlocked badges)
        if (badges.isNotEmpty()) {
            item {
                BadgesSection(badges = badges)
            }
        }

        // Section 3: One-Tap Approval System (The Core Loop)
        item {
            ApprovalCenterSection(
                waitingTasks = waitingTasks,
                onApproveTask = onApproveTask,
                onApproveAll = onApproveAllWaitingTasks,
                onRejectClick = { taskId ->
                    showRejectDialogForTaskId = taskId
                    rejectionReasonInput = ""
                }
            )
        }

        // Section 4: Task Configuration Screen Trigger (The 5-per-Category Rule)
        item {
            TaskConfigurationCard(
                schedule = schedule,
                onStartCustomizing = onStartCustomizing,
                onResetToDefault = onResetToDefault
            )
        }

        // Section 5: Today's 20-Task Checklist Status with Category Filters
        item {
            Column(modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 Bugünün 20 Görevi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "$approvedCountToday/20 Onaylı",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                CategoryFilterChips(
                    selectedCategoryId = selectedCategoryFilter,
                    onCategorySelected = onCategoryFilterSelected
                )
            }
        }

        // Tasks List
        items(filteredTasks, key = { it.task.id }) { uiItem ->
            TaskCard(
                uiItem = uiItem,
                userRole = UserRole.PARENT,
                onStudentDoneClick = {},
                onParentApproveClick = { onApproveTask(uiItem.task.id) },
                onParentRejectClick = {
                    showRejectDialogForTaskId = uiItem.task.id
                    rejectionReasonInput = ""
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }

    // Monthly Reward Edit Dialog
    if (showRewardDialog) {
        MonthlyRewardDialog(
            currentTitle = profile?.monthlyRewardTitle ?: "",
            currentTarget = profile?.monthlyTargetDiamonds ?: 300,
            onDismiss = { showRewardDialog = false },
            onConfirm = { title, target ->
                onUpdateMonthlyReward(title, target)
                showRewardDialog = false
            }
        )
    }

    // Rejection Reason Dialog
    if (showRejectDialogForTaskId != null) {
        val taskId = showRejectDialogForTaskId!!
        RejectionFeedbackDialog(
            taskId = taskId,
            reasonInput = rejectionReasonInput,
            onReasonChange = { rejectionReasonInput = it },
            onDismiss = { showRejectDialogForTaskId = null },
            onConfirm = { reason ->
                onRejectTask(taskId, reason)
                showRejectDialogForTaskId = null
            }
        )
    }
}

/**
 * Child Selector: lets parent switch between linked children
 */
@Composable
private fun ChildSelectorSection(
    allProfiles: List<StudentProfileEntity>,
    selectedChildId: String,
    onSelectChild: (String) -> Unit
) {
    if (allProfiles.size <= 1) return

    Column(modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)) {
        Text(
            text = "👨‍👧 Çocuk Seçimi",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("child_selector_row"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allProfiles, key = { it.id }) { child ->
                val isSelected = child.id == selectedChildId
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = if (isSelected) 3.dp else 0.dp,
                    modifier = Modifier
                        .testTag("child_chip_${child.id}")
                        .clickable { onSelectChild(child.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = child.name.take(1),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = child.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "💎 ${child.diamonds} • ${child.className}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Child Overview: Real-time diamonds, streak, and monthly reward progress bar
 */
@Composable
private fun ChildOverviewCard(
    profile: StudentProfileEntity?,
    onEditRewardClick: () -> Unit
) {
    val diamonds = profile?.diamonds ?: 0
    val target = (profile?.monthlyTargetDiamonds ?: 300).coerceAtLeast(1)
    val progress = (diamonds.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    val percent = (progress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("parent_child_overview_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Child name and diamonds
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "👨‍👧 Veli Denetim Paneli",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = profile?.name ?: "Ahmet Yılmaz",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${profile?.className ?: "5-A Sınıfı"} • 🔥 ${profile?.streakDays ?: 0} Günlük Seri",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFE0F7FA),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF80DEEA))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💎",
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$diamonds",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF006064)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Monthly Reward Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Aylık Ödül Hedefi",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "🎁 ${profile?.monthlyRewardTitle ?: "Lunapark Gezisi & Kitap Seti"}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedButton(
                    onClick = onEditRewardClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("edit_monthly_reward_btn")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Ödülü Düzenle", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Düzenle", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Linear progress indicator
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = Color(0xFF00ACC1),
                trackColor = Color(0xFFB2EBF2).copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "%$percent Tamamlandı",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00838F)
                )
                Text(
                    text = "$diamonds / $target 💎",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * One-Tap Approval Center (The Core Loop)
 */
@Composable
private fun ApprovalCenterSection(
    waitingTasks: List<DailyTaskUiItem>,
    onApproveTask: (String) -> Unit,
    onApproveAll: () -> Unit,
    onRejectClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("approval_center_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (waitingTasks.isNotEmpty()) StatusWaitingBg else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (waitingTasks.isNotEmpty()) StatusWaitingBorder else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = StatusWaitingText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Onay Bekleyen Görevler",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusWaitingText
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = if (waitingTasks.isNotEmpty()) StatusWaitingText else Color.Gray.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${waitingTasks.size}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (waitingTasks.isEmpty()) {
                Text(
                    text = "Şu anda onay bekleyen görev yok. Öğrenci gün içinde görev yaptıkça onayınız için buraya gelecektir.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Öğrenciniz bu görevleri tamamladığını bildirdi. Onayladığınızda elmaslar anında hesabına yansıtılır.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBF360C)
                )

                // Quick 'Approve All' if more than 1
                if (waitingTasks.size > 1) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onApproveAll,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("approve_all_waiting_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tümünü Onayla (${waitingTasks.size} Görev • +${waitingTasks.size * 5} 💎)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Individual Waiting Task Items
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    waitingTasks.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusWaitingBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("waiting_task_item_${item.task.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.task.title,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontSize = 16.sp,
                                            lineHeight = 22.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "${TaskCategory.fromId(item.task.categoryId).titleTr} • +${item.task.diamondReward} Elmas",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 12.sp,
                                            color = TaskCategory.fromId(item.task.categoryId).color,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = StatusWaitingBg
                                    ) {
                                        Text(
                                            text = "⏳ Bekliyor",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 11.sp,
                                            color = StatusWaitingText,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onRejectClick(item.task.id) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .testTag("reject_btn_${item.task.id}"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Geri Gönder", style = MaterialTheme.typography.labelMedium, maxLines = 1)
                                    }

                                    Button(
                                        onClick = { onApproveTask(item.task.id) },
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .height(42.dp)
                                            .testTag("approve_btn_${item.task.id}"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Onayla (+5 💎)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Task Configuration Screen Card: Toggle between Teacher's List and Custom Selection
 */
@Composable
private fun TaskConfigurationCard(
    schedule: DailyScheduleEntity?,
    onStartCustomizing: () -> Unit,
    onResetToDefault: () -> Unit
) {
    val isCustom = schedule?.isCustom == true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("parent_task_config_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "⚙️ Görev Listesi Yapılandırması",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kategori Başına 5 Görev Kuralı (Toplam 20)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isCustom) Color(0xFFEDE7F6) else Color(0xFFE3F2FD)
                ) {
                    Text(
                        text = if (isCustom) "Özel Liste" else "Öğretmen Listesi",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isCustom) Color(0xFF6A1B9A) else Color(0xFF1565C0),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isCustom) {
                    "Çocuğunuz şu anda sizin seçtiğiniz 20 özel görevi takip ediyor. İsterseniz tekrar öğretmeninizin belirlediği standart listeye dönebilirsiniz."
                } else {
                    "Şu anda öğretmenin belirlediği 20 görev geçerlidir. 'Kendim Seçeceğim' diyerek her kategoriden 5'er görev seçerek özel liste oluşturabilirsiniz."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onStartCustomizing,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(42.dp)
                        .testTag("parent_customize_schedule_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCustom) "Özel Listeyi Düzenle" else "Kendim Seçeceğim",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isCustom) {
                    OutlinedButton(
                        onClick = onResetToDefault,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("parent_reset_to_teacher_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Öğretmen Listesi", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

/**
 * Custom Schedule Builder Screen
 * Strict 5-per-Category Rule (Total 20):
 * - 4 main categories: Ev (Home), Okul (School), Dini (Religion), Adap (Etiquette)
 * - UI prevents selecting a 6th task from any category (checkbox disabled, warning shown)
 * - Save button enabled only when 5 from each category are selected (20 total)
 */
@Composable
fun CustomScheduleBuilderScreen(
    allTasks: List<TaskEntity>,
    selectedIds: Set<String>,
    onToggleTask: (TaskEntity) -> Boolean,
    onSave: () -> Boolean,
    onCancel: () -> Unit,
    onResetToDefault: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val categories = TaskCategory.entries

    val currentCategory = categories[selectedTabIndex]
    val tasksInCurrentCategory = allTasks.filter { it.categoryId == currentCategory.id }

    val taskMap = remember(allTasks) { allTasks.associateBy { it.id } }
    val countsByCategory = remember(selectedIds, allTasks) {
        selectedIds.mapNotNull { taskMap[it]?.categoryId }.groupingBy { it }.eachCount()
    }

    val homeCount = countsByCategory["home"] ?: 0
    val schoolCount = countsByCategory["school"] ?: 0
    val religionCount = countsByCategory["religion"] ?: 0
    val etiquetteCount = countsByCategory["etiquette"] ?: 0

    val currentCategoryCount = countsByCategory[currentCategory.id] ?: 0
    val totalSelected = selectedIds.size
    val isReadyToSave = totalSelected == 20 && homeCount == 5 && schoolCount == 5 && religionCount == 5 && etiquetteCount == 5

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF4A148C),
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Özel Günlük Liste Belirle",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Her 4 ana kategoriden tam 5'er görev seçin",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isReadyToSave) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.22f)
                    ) {
                        Text(
                            text = "$totalSelected / 20",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Real-time Quota Badges for the 4 categories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CategoryQuotaBadge(
                        title = "Ev",
                        count = homeCount,
                        max = 5,
                        color = TaskCategory.HOME.color,
                        modifier = Modifier.weight(1f)
                    )
                    CategoryQuotaBadge(
                        title = "Okul",
                        count = schoolCount,
                        max = 5,
                        color = TaskCategory.SCHOOL.color,
                        modifier = Modifier.weight(1f)
                    )
                    CategoryQuotaBadge(
                        title = "Dini",
                        count = religionCount,
                        max = 5,
                        color = TaskCategory.RELIGION.color,
                        modifier = Modifier.weight(1f)
                    )
                    CategoryQuotaBadge(
                        title = "Adap",
                        count = etiquetteCount,
                        max = 5,
                        color = TaskCategory.ETIQUETTE.color,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Category Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            categories.forEachIndexed { index, category ->
                val count = countsByCategory[category.id] ?: 0
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier.testTag("custom_tab_${category.id}"),
                    text = {
                        Text(
                            text = "${category.titleTr} ($count/5)",
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }

        // Quota status banner for current category
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (currentCategoryCount >= 5) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (currentCategoryCount >= 5) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (currentCategoryCount >= 5) Color(0xFF2E7D32) else Color(0xFFE65100),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (currentCategoryCount >= 5) {
                        "${currentCategory.titleTr} kategorisi için 5 görev kotası doldu (Maksimum 5/5)."
                    } else {
                        "${currentCategory.titleTr} kategorisinden ${5 - currentCategoryCount} görev daha seçmelisiniz ($currentCategoryCount/5)."
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (currentCategoryCount >= 5) Color(0xFF2E7D32) else Color(0xFFBF360C),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Tasks in Current Category
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasksInCurrentCategory, key = { it.id }) { task ->
                val isSelected = selectedIds.contains(task.id)
                // Strict 5-per-category restriction: If category has 5 selected, unselected items cannot be checked
                val isCategoryFull = currentCategoryCount >= 5
                val canToggle = isSelected || !isCategoryFull

                val subCat = EtiquetteSubCategory.fromId(task.subCategoryId)

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = when {
                        isSelected -> currentCategory.color.copy(alpha = 0.12f)
                        !canToggle -> MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        else -> MaterialTheme.colorScheme.surface
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            isSelected -> currentCategory.color
                            !canToggle -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(enabled = canToggle) { onToggleTask(task) }
                        .testTag("custom_task_toggle_${task.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = if (canToggle) { { onToggleTask(task) } } else null,
                            enabled = canToggle,
                            colors = CheckboxDefaults.colors(checkedColor = currentCategory.color)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            if (subCat != null) {
                                Text(
                                    text = subCat.titleTr,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 12.sp,
                                    color = currentCategory.color,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 15.sp,
                                lineHeight = 21.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (!canToggle && !isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f) else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (!canToggle && !isSelected) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Gray.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Kota Doldu",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }

        // Bottom Action Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("custom_schedule_cancel_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("İptal")
                }

                Button(
                    onClick = { onSave() },
                    modifier = Modifier
                        .weight(2f)
                        .height(46.dp)
                        .testTag("custom_schedule_save_btn"),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isReadyToSave,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isReadyToSave) "Özel Listeyi Kaydet (20)" else "20 Görevi Tamamlayın ($totalSelected/20)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryQuotaBadge(
    title: String,
    count: Int,
    max: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val isComplete = count == max
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (isComplete) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.18f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontSize = 11.sp,
                maxLines = 1
            )
            Text(
                text = "$count/$max",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun MonthlyRewardDialog(
    currentTitle: String,
    currentTarget: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var title by remember { mutableStateOf(currentTitle) }
    var targetStr by remember { mutableStateOf(currentTarget.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aylık Hedef & Ödül Belirle") },
        text = {
            Column {
                Text(
                    text = "Çocuğunuz ay sonunda bu elmas hedefine ulaştığında kazanacağı hediyeyi belirleyin.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Ödül / Hediye Adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Gereken Elmas Hedefi (örn: 300)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetStr.toIntOrNull() ?: 300
                    if (title.isNotBlank()) {
                        onConfirm(title, target)
                    }
                }
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
private fun RejectionFeedbackDialog(
    taskId: String,
    reasonInput: String,
    onReasonChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Görevi Geri Gönder") },
        text = {
            Column {
                Text(
                    text = "Öğrenciye görevi neden tamamlanmamış saydığınızı ve ne yapması gerektiğini iletin:",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = reasonInput,
                    onValueChange = onReasonChange,
                    placeholder = { Text("Örn: Yatağını tam toplamamışsın, lütfen yastığı da düzelt.") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val reason = if (reasonInput.isBlank()) "Görevi eksiksiz tamamlayıp tekrar gönder." else reasonInput
                    onConfirm(reason)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) {
                Text("Geri Gönder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        }
    )
}
