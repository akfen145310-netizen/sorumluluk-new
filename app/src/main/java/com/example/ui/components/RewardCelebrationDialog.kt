package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Full-Screen Celebration Animation Dialog for Monthly Reward Unlock.
 * Triggered when student reaches diamonds >= monthlyTargetDiamonds.
 * Features vibrant confetti particle simulation, celebratory sound cue visualizer,
 * and high-contrast reward unlock card.
 */
@Composable
fun MonthlyRewardCelebrationDialog(
    rewardTitle: String,
    currentDiamonds: Int,
    targetDiamonds: Int,
    studentName: String,
    onDismiss: () -> Unit,
    onClaimReward: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        val animProgress = remember { Animatable(0f) }
        val scaleAnim = remember { Animatable(0.7f) }

        LaunchedEffect(Unit) {
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        }

        LaunchedEffect(Unit) {
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2400, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.78f))
                .testTag("monthly_reward_celebration_dialog"),
            contentAlignment = Alignment.Center
        ) {
            // Full screen confetti / fireworks canvas
            ConfettiCanvas(progress = animProgress.value)

            // Celebration Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .scale(scaleAnim.value)
                    .testTag("celebration_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFFF8E1),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Floating trophy badge with sparkles
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(96.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFD54F).copy(alpha = 0.25f),
                                modifier = Modifier.size(96.dp)
                            ) {}
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFA000),
                                modifier = Modifier.size(76.dp),
                                shadowElevation = 6.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Kupa",
                                        tint = Color.White,
                                        modifier = Modifier.size(44.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Title: TEBRİKLER!
                        Text(
                            text = "🎉 TEBRİKLER! 🎉",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE65100),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$studentName Hedefine Ulaştı!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Big Reward Card
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFF004D40),
                            modifier = Modifier.fillMaxWidth(),
                            shadowElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD54F),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AYLIK BÜYÜK ÖDÜL KAZANILDI",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD54F)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "🎁 $rewardTitle",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Diamond,
                                        contentDescription = null,
                                        tint = Color(0xFF80DEEA),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$currentDiamonds / $targetDiamonds Elmas Toplandı",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFFE0F2F1),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Bir ay boyunca gösterdiğin üstün sorumluluk, gayret ve istikrar sayesinde bu ödülü hak ettin. Ailenle birlikte kutla!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("celebration_dismiss_btn")
                            ) {
                                Text("Kapat", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    onClaimReward()
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("celebration_claim_btn")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ödülü Al", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Animated Canvas that draws floating celebration confetti and burst fireworks particles.
 */
@Composable
private fun ConfettiCanvas(
    progress: Float,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    // Generate deterministic particles
    val particles = remember {
        List(70) { index ->
            ConfettiParticle(
                seedX = (index * 17 % 100) / 100f,
                speed = 0.5f + (index % 5) * 0.15f,
                angle = (index * 23 % 360).toDouble(),
                colorIndex = index % 6,
                radius = 3f + (index % 4) * 1.5f,
                wobbleSpeed = 3f + (index % 4)
            )
        }
    }

    val palette = listOf(
        Color(0xFFFFD700), // Gold
        Color(0xFFFF3D00), // Bright Orange
        Color(0xFF00E676), // Bright Green
        Color(0xFF00B0FF), // Bright Cyan
        Color(0xFFE040FB), // Magenta
        Color(0xFFFFEB3B)  // Yellow
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            val effectiveProgress = (progress * p.speed) % 1f
            val y = effectiveProgress * height
            val wobble = sin(effectiveProgress * Math.PI * p.wobbleSpeed).toFloat() * 24.dp.toPx()
            val x = (p.seedX * width + wobble).coerceIn(0f, width)

            val alpha = if (effectiveProgress < 0.15f) {
                effectiveProgress / 0.15f
            } else if (effectiveProgress > 0.85f) {
                (1f - effectiveProgress) / 0.15f
            } else 1f

            drawCircle(
                color = palette[p.colorIndex].copy(alpha = alpha.coerceIn(0f, 1f)),
                radius = p.radius.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

private data class ConfettiParticle(
    val seedX: Float,
    val speed: Float,
    val angle: Double,
    val colorIndex: Int,
    val radius: Float,
    val wobbleSpeed: Float
)
