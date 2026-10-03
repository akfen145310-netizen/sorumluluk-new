package com.example.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class TaskCategory(
    val id: String,
    val titleTr: String,
    val color: Color,
    val icon: ImageVector,
    val totalCount: Int
) {
    HOME(
        id = "home",
        titleTr = "Ev Sorumlulukları",
        color = Color(0xFFD97706),
        icon = Icons.Default.Home,
        totalCount = 20
    ),
    SCHOOL(
        id = "school",
        titleTr = "Okul Sorumlulukları",
        color = Color(0xFF0891B2),
        icon = Icons.Default.School,
        totalCount = 20
    ),
    RELIGION(
        id = "religion",
        titleTr = "Dini Sorumluluklar",
        color = Color(0xFF00875A),
        icon = Icons.Default.Mosque,
        totalCount = 20
    ),
    ETIQUETTE(
        id = "etiquette",
        titleTr = "Adabı Muaşeret",
        color = Color(0xFF7C3AED),
        icon = Icons.Default.Star,
        totalCount = 50
    );

    companion object {
        fun fromId(id: String): TaskCategory {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: HOME
        }
    }
}

enum class EtiquetteSubCategory(
    val id: String,
    val titleTr: String
) {
    SITTING("sitting", "Oturuş-Kalkış Adabı"),
    SPEAKING("speaking", "Konuşma & İletişim Adabı"),
    EATING_DRINKING("eating_drinking", "Yeme-İçme Adabı"),
    SLEEPING_WAKING("sleeping_waking", "Uyku & Uyanma Adabı"),
    GREETING("greeting", "Selamlaşma Adabı"),
    GUEST("guest_etiquette", "Misafirlik & Ağırlama"),
    HYGIENE("hygiene", "Kişisel Bakım & Hijyen"),
    ELDERS("elders", "Büyüklere Saygı Adabı"),
    PEERS("peers", "Akran & Arkadaşlık Münasebeti"),
    PUBLIC_TECH("public_tech", "Toplum İçi & Teknoloji Adabı");

    companion object {
        fun fromId(id: String?): EtiquetteSubCategory? {
            if (id == null) return null
            return entries.find { it.id.equals(id, ignoreCase = true) }
        }
    }
}
