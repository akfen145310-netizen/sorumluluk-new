package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

enum class AppFontOption(
    val id: String,
    val titleTr: String,
    val descriptionTr: String,
    val fontResId: Int?
) {
    LEXEND(
        id = "lexend",
        titleTr = "Lexend",
        descriptionTr = "Okuma akıcılığını ve odaklanmayı artıran modern yazı tipi",
        fontResId = R.font.lexend
    ),
    NUNITO(
        id = "nunito",
        titleTr = "Nunito",
        descriptionTr = "Yuvarlak hatlı, samimi ve çocuk dostu yazı tipi",
        fontResId = R.font.nunito
    ),
    QUICKSAND(
        id = "quicksand",
        titleTr = "Quicksand",
        descriptionTr = "Geometrik, ferah ve hafif tasarımıyla göz yormaz",
        fontResId = R.font.quicksand
    ),
    SYSTEM(
        id = "system",
        titleTr = "Sistem Varsayılanı",
        descriptionTr = "Cihazınızın standart Android sistem yazı tipi",
        fontResId = null
    );

    companion object {
        fun fromId(id: String): AppFontOption {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: LEXEND
        }
    }
}

fun getFontFamilyForOption(option: AppFontOption): FontFamily {
    return option.fontResId?.let { FontFamily(Font(it)) } ?: FontFamily.Default
}

fun getTypographyForFont(fontOption: AppFontOption, fontScale: Float = 1.0f): Typography {
    val family = getFontFamilyForOption(fontOption)
    val scale = fontScale.coerceIn(0.85f, 1.35f)

    return Typography(
        displayLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (57 * scale).sp,
            lineHeight = (64 * scale).sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (45 * scale).sp,
            lineHeight = (52 * scale).sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (36 * scale).sp,
            lineHeight = (44 * scale).sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = (32 * scale).sp,
            lineHeight = (40 * scale).sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = (28 * scale).sp,
            lineHeight = (36 * scale).sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = (24 * scale).sp,
            lineHeight = (32 * scale).sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = (22 * scale).sp,
            lineHeight = (28 * scale).sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = (16 * scale).sp,
            lineHeight = (24 * scale).sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * scale).sp,
            lineHeight = (20 * scale).sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * scale).sp,
            lineHeight = (24 * scale).sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * scale).sp,
            lineHeight = (20 * scale).sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (12 * scale).sp,
            lineHeight = (16 * scale).sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * scale).sp,
            lineHeight = (20 * scale).sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = (12 * scale).sp,
            lineHeight = (16 * scale).sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = (11 * scale).sp,
            lineHeight = (16 * scale).sp,
            letterSpacing = 0.5.sp
        )
    )
}
