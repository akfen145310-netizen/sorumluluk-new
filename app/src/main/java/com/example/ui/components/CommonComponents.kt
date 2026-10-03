package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import com.example.data.Badge
import com.example.data.DailyTaskUiItem
import com.example.data.EtiquetteSubCategory
import com.example.data.StudentProfileEntity
import com.example.data.TaskCategory
import com.example.ui.UserRole
import com.example.ui.theme.StatusRejectedBg
import com.example.ui.theme.StatusRejectedBorder
import com.example.ui.theme.StatusRejectedText
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusSuccessBorder
import com.example.ui.theme.StatusSuccessText
import com.example.ui.theme.StatusWaitingBg
import com.example.ui.theme.StatusWaitingBorder
import com.example.ui.theme.StatusWaitingText

@Composable
fun RoleSelectorBar(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserRole.entries.forEach { role ->
                val isSelected = role == currentRole
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    label = "roleBg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "roleText"
                )

                val icon = when (role) {
                    UserRole.STUDENT -> Icons.Default.Person
                    UserRole.PARENT -> Icons.Default.SupervisorAccount
                    UserRole.TEACHER -> Icons.Default.School
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onRoleSelected(role) }
                        .testTag("role_tab_${role.name.lowercase()}"),
                    shape = RoundedCornerShape(14.dp),
                    color = bgColor
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = role.titleTr,
                            tint = textColor,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = when (role) {
                                UserRole.STUDENT -> "Öğrenci"
                                UserRole.PARENT -> "Veli"
                                UserRole.TEACHER -> "Öğretmen"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiamondVaultCard(
    profile: StudentProfileEntity?,
    approvedCountToday: Int,
    totalToday: Int = 20,
    modifier: Modifier = Modifier
) {
    val diamonds = profile?.diamonds ?: 0
    val target = profile?.monthlyTargetDiamonds ?: 300
    val rewardTitle = profile?.monthlyRewardTitle ?: "Aylık Ödül"
    val streak = profile?.streakDays ?: 0

    val progress = (diamonds.toFloat() / target.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("diamond_vault_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF00695C).copy(alpha = 0.08f),
                            Color(0xFF0288D1).copy(alpha = 0.12f),
                            Color(0xFFFFB300).copy(alpha = 0.06f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile info
                    Column {
                        Text(
                            text = profile?.name ?: "Öğrenci",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${profile?.className ?: "Sınıf"} • Bugün: $approvedCountToday/$totalToday Onaylandı",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Streak Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF3E0),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Seri",
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$streak Gün",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Diamond Balance Large Display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F7FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = "Elmas",
                                tint = Color(0xFF00ACC1),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$diamonds",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00695C)
                                )
                                Text(
                                    text = " Elmas",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF00897B),
                                    modifier = Modifier.padding(bottom = 3.dp, start = 3.dp)
                                )
                            }
                            Text(
                                text = "Aylık Hedef: $target Elmas",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Progress percentage badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (progress >= 1f) Color(0xFFE8F5E9) else Color(0xFFE1F5FE)
                    ) {
                        Text(
                            text = "%${(progress * 100).toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (progress >= 1f) Color(0xFF2E7D32) else Color(0xFF0277BD),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Monthly Reward Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (progress >= 1f) Color(0xFF2E7D32) else Color(0xFF00ACC1),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Monthly Reward Goal Text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎁 Ödül: $rewardTitle",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (progress >= 1f) "KİLİT AÇILDI! 🎉" else "${target - diamonds} Elmas Kaldı",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (progress >= 1f) Color(0xFF2E7D32) else Color(0xFF00838F)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFilterChips(
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit,
    categoryItemCounts: Map<String, Pair<Int, Int>> = emptyMap(),
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        item {
            val isSelected = selectedCategoryId == null
            FilterChipItem(
                title = "Tümü (20)",
                isSelected = isSelected,
                color = MaterialTheme.colorScheme.primary,
                onClick = { onCategorySelected(null) }
            )
        }

        items(TaskCategory.entries) { category ->
            val isSelected = selectedCategoryId == category.id
            val counts = categoryItemCounts[category.id]
            val countText = if (counts != null) " (${counts.first}/${counts.second})" else " (5)"

            FilterChipItem(
                title = "${category.titleTr}$countText",
                isSelected = isSelected,
                color = category.color,
                icon = category.icon,
                onClick = { onCategorySelected(category.id) }
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    title: String,
    isSelected: Boolean,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color else color.copy(alpha = 0.1f),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f)) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else color,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else color
            )
        }
    }
}

@Composable
fun SparkleBurst(
    trigger: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
            )
        }
    }

    if (progress.value > 0f && progress.value < 1f) {
        Canvas(modifier = modifier) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val currentProgress = progress.value
            val maxRadius = size.width.coerceAtLeast(size.height) * 1.15f
            val distance = currentProgress * maxRadius
            val alpha = (1f - currentProgress).coerceIn(0f, 1f)

            val particleColors = listOf(
                Color(0xFFFFD700), // Gold
                Color(0xFF00E676), // Emerald
                Color(0xFF00E5FF), // Cyan
                Color(0xFFFF4081), // Pink
                Color(0xFF7C4DFF), // Purple
                Color(0xFFFF9100)  // Orange
            )

            for (i in 0 until 8) {
                val angle = Math.toRadians((i * 45.0) + (currentProgress * 25.0))
                val x = center.x + (distance * cos(angle)).toFloat()
                val y = center.y + (distance * sin(angle)).toFloat()
                val particleRadius = (4.5.dp.toPx() * (1f - currentProgress * 0.7f)).coerceAtLeast(1f)

                drawCircle(
                    color = particleColors[i % particleColors.size].copy(alpha = alpha),
                    radius = particleRadius,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    uiItem: DailyTaskUiItem,
    userRole: UserRole,
    onStudentDoneClick: () -> Unit,
    onParentApproveClick: () -> Unit,
    onParentRejectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val task = uiItem.task
    val log = uiItem.log
    val category = TaskCategory.fromId(task.categoryId)
    val subCategory = EtiquetteSubCategory.fromId(task.subCategoryId)

    val isApproved = log.status == "APPROVED"
    val isWaiting = log.status == "WAITING_APPROVAL" || log.status == "STUDENT_DONE" || (log.isCompletedByStudent && !isApproved)
    val isRejected = log.status == "REJECTED"
    val isChecked = isApproved || isWaiting

    var showSparkle by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val cardBorderColor = when {
        isApproved -> StatusSuccessBorder
        isWaiting -> StatusWaitingBorder
        isRejected -> StatusRejectedBorder
        else -> Color.Transparent
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isApproved -> StatusSuccessBg
                isWaiting -> StatusWaitingBg
                isRejected -> StatusRejectedBg
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = if (cardBorderColor != Color.Transparent) androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Category & Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(category.color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (subCategory != null) "${category.titleTr} • ${subCategory.titleTr}" else category.titleTr,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = category.color,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Diamond tag (Strictly Read-Only)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE0F7FA)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = null,
                            tint = Color(0xFF0097A7),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "+${task.diamondReward} Elmas",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00838F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Task Content & Role Specific Section
            when (userRole) {
                UserRole.STUDENT -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Checkbox with celebratory SparkleBurst effect
                        Box(
                            modifier = Modifier.size(44.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            SparkleBurst(
                                trigger = showSparkle,
                                modifier = Modifier.size(44.dp)
                            )

                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    if (!isChecked && !isApproved) {
                                        showSparkle = true
                                        coroutineScope.launch {
                                            delay(650)
                                            showSparkle = false
                                        }
                                        onStudentDoneClick()
                                    }
                                },
                                enabled = !isChecked || isRejected,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = if (isApproved) Color(0xFF2E7D32) else Color(0xFFF57F17),
                                    uncheckedColor = if (isRejected) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary,
                                    checkmarkColor = Color.White
                                ),
                                modifier = Modifier.testTag("student_task_checkbox_${task.id}")
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Task Title (Strictly Read-Only: student cannot edit)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 8.dp)
                        ) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isApproved) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            when {
                                isApproved -> {
                                    Text(
                                        text = "✨ Veli Onayladı (+5 Elmas Kazanıldı)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusSuccessText
                                    )
                                }
                                isWaiting -> {
                                    Text(
                                        text = "⏳ Beklemede (Veli Onayı Bekleniyor)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusWaitingText
                                    )
                                }
                                isRejected -> {
                                    Text(
                                        text = "⚠️ Veli Notu: ${log.rejectionReason ?: "Lütfen tekrar tamamlayın."}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusRejectedText
                                    )
                                }
                            }
                        }
                    }

                    // If not yet marked done, also provide the prominent child-friendly button
                    if (!isChecked) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                showSparkle = true
                                coroutineScope.launch {
                                    delay(650)
                                    showSparkle = false
                                }
                                onStudentDoneClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("student_done_button_${task.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRejected) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = if (isRejected) Icons.Default.Redo else Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRejected) "Yeniden Yaptım Olarak İşaretle" else "Yaptım (Tamamladım)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                UserRole.PARENT -> {
                    // Task Title (Read-Only)
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (isRejected && !log.rejectionReason.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Veli Notu: ${log.rejectionReason}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFC62828)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    when {
                        isWaiting -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onParentRejectClick,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .testTag("parent_reject_button_${task.id}"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Geri Gönder", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Geri Gönder", style = MaterialTheme.typography.labelMedium, maxLines = 1)
                                }

                                Button(
                                    onClick = onParentApproveClick,
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(42.dp)
                                        .testTag("parent_approve_button_${task.id}"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Onayla", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Onayla (+5 💎)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }
                        }
                        isApproved -> {
                            Text(
                                text = "✓ Tarafınızca onaylandı (+5 Elmas verildi)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        else -> {
                            Text(
                                text = "Öğrenci henüz bu görevi tamamlamadı.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                UserRole.TEACHER -> {
                    // Task Title (Read-Only)
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val statusText = when {
                        isApproved -> "✓ Öğrenci tamamladı, veli onayladı"
                        isWaiting -> "⏳ Öğrenci tamamladı, veli onayı bekliyor"
                        isRejected -> "✕ Veli tarafından düzeltme istendi"
                        else -> "○ Henüz yapılmadı"
                    }
                    val statusColor = when {
                        isApproved -> StatusSuccessText
                        isWaiting -> StatusWaitingText
                        isRejected -> StatusRejectedText
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                }
            }
        }
    }
}

@Composable
fun BadgesSection(
    badges: List<Badge>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 6.dp)
    ) {
        Text(
            text = "🏅 Karakter & Sorumluluk Rozetleri",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 6.dp)
        ) {
            items(badges) { badge ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (badge.isUnlocked) badge.color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (badge.isUnlocked) badge.color else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.width(140.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (badge.isUnlocked) badge.color else Color.Gray.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (badge.isUnlocked) badge.icon else Icons.Default.Lock,
                                contentDescription = badge.title,
                                tint = if (badge.isUnlocked) Color.White else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = badge.title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = badge.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 10.sp,
                            lineHeight = 12.sp
                        )
                    }
                }
            }
        }
    }
}
