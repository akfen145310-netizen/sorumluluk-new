package com.example.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val isUnlocked: Boolean
)

object BadgeCatalog {
    /**
     * Calculates badge unlock statuses based on student metrics.
     * Specific Module 6 triggers:
     * 1. 7 consecutive days completed -> 'İstikrar Yıldızı' (Consistency Star)
     * 2. 100 diamonds reached -> 'Elmas Avcısı' (Diamond Hunter)
     * 3. Religion tasks completed for a week (>= 5 religion tasks approved) -> 'İbadet Kahramanı' (Worship Hero)
     */
    fun getBadges(
        diamonds: Int,
        streakDays: Int,
        approvedTaskCount: Int,
        approvedReligionCount: Int = 0
    ): List<Badge> {
        return listOf(
            Badge(
                id = "consistency_star",
                title = "İstikrar Yıldızı",
                description = "7 gün kesintisiz tüm görevlerini tamamladı",
                icon = Icons.Default.Star,
                color = Color(0xFFFFB300),
                isUnlocked = streakDays >= 7
            ),
            Badge(
                id = "diamond_hunter",
                title = "Elmas Avcısı",
                description = "100 elmas barajına ulaştı",
                icon = Icons.Default.Diamond,
                color = Color(0xFF00ACC1),
                isUnlocked = diamonds >= 100
            ),
            Badge(
                id = "worship_hero",
                title = "İbadet Kahramanı",
                description = "Maneviyat ve namaz görevlerinde haftalık tam başarı",
                icon = Icons.Default.Mosque,
                color = Color(0xFF2E7D32),
                isUnlocked = approvedReligionCount >= 5 || (streakDays >= 5 && approvedTaskCount >= 10)
            ),
            Badge(
                id = "diamond_200",
                title = "Elmas Şampiyonu",
                description = "200 Elmas biriktirerek zirveye yaklaştı",
                icon = Icons.Default.EmojiEvents,
                color = Color(0xFFFBC02D),
                isUnlocked = diamonds >= 200
            ),
            Badge(
                id = "home_master",
                title = "Ev Düzenleyici",
                description = "Ev sorumluluklarını aksatmadan yerine getiriyor",
                icon = Icons.Default.Home,
                color = Color(0xFFE65100),
                isUnlocked = approvedTaskCount >= 5
            ),
            Badge(
                id = "school_hero",
                title = "Örnek Talebe",
                description = "Okul ve ders sorumluluklarında tam disiplin",
                icon = Icons.Default.School,
                color = Color(0xFF1565C0),
                isUnlocked = approvedTaskCount >= 8
            ),
            Badge(
                id = "first_step",
                title = "İlk Adım",
                description = "İlk sorumluluk görevini başarıyla tamamladı",
                icon = Icons.Default.AutoAwesome,
                color = Color(0xFF7E57C2),
                isUnlocked = approvedTaskCount >= 1
            )
        )
    }
}

