package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Badge
import com.example.data.DailyTaskUiItem
import com.example.data.StudentProfileEntity
import com.example.data.TaskCategory
import com.example.ui.UserRole
import com.example.ui.components.BadgesSection
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.DiamondVaultCard
import com.example.ui.components.StudentDailyIncentiveHeader
import com.example.ui.components.TaskCard

@Composable
fun StudentScreen(
    profile: StudentProfileEntity?,
    tasks: List<DailyTaskUiItem>,
    badges: List<Badge>,
    dailyAnnouncement: String,
    selectedCategoryFilter: String?,
    onCategoryFilterSelected: (String?) -> Unit,
    onStudentDoneClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val approvedCount = remember(tasks) {
        tasks.count { it.log.status == "APPROVED" }
    }
    val completedCount = remember(tasks) {
        tasks.count { it.log.status == "APPROVED" || it.log.status == "STUDENT_DONE" || it.log.status == "WAITING_APPROVAL" || it.log.isCompletedByStudent }
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
        if (selectedCategoryFilter.isNullOrBlank()) {
            tasks
        } else {
            tasks.filter { it.task.categoryId == selectedCategoryFilter }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("student_screen_list"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Daily incentive & countdown header
        item {
            StudentDailyIncentiveHeader(
                announcement = dailyAnnouncement,
                monthlyRewardTitle = profile?.monthlyRewardTitle ?: "Aylık Başarı Ödülü",
                monthlyTargetDiamonds = profile?.monthlyTargetDiamonds ?: 300,
                currentDiamonds = profile?.diamonds ?: 0,
                completedTasksCount = completedCount,
                totalTasksCount = totalCount.coerceAtLeast(1),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Diamond vault card
        item {
            DiamondVaultCard(
                profile = profile,
                approvedCountToday = approvedCount,
                totalToday = totalCount.coerceAtLeast(1)
            )
        }

        // Category Filter Chips
        item {
            CategoryFilterChips(
                selectedCategoryId = selectedCategoryFilter,
                onCategorySelected = onCategoryFilterSelected,
                categoryItemCounts = categoryCounts
            )
        }

        // Tasks Header
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(
                    text = "Günün Sorumlulukları (${filteredTasks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Görevi tamamladığında dokun, velin onaylayınca elmasları kazan!",
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
                    onStudentDoneClick = { onStudentDoneClick(item.task.id) },
                    onParentApproveClick = {},
                    onParentRejectClick = {}
                )
            }
        }

        // Badges Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            BadgesSection(badges = badges)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
