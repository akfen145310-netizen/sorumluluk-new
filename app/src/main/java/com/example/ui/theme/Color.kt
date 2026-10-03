package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Exact Emerald & Pastel Palette from Reference Image
val BrandPrimary = Color(0xFF00875A)               // Exact rich emerald green from buttons & logo in image
val BrandPrimaryHover = Color(0xFF00734D)          // Darker emerald for pressed states
val BrandPrimaryContainer = Color(0xFFECFDF5)      // Mint pastel background (as in #Kur'an-ı Kerim tags, Sınıf Derecesi)
val BrandOnPrimaryContainer = Color(0xFF065F46)    // Deep emerald text for pills
val BrandBorderMint = Color(0xFFA7F3D0)            // Mint border for chips

// Gold / Amber (as in star points badge and "Eksiklerini Tamamla" button)
val BrandGold = Color(0xFFD97706)
val BrandGoldBg = Color(0xFFFFFBEB)
val BrandGoldBorder = Color(0xFFFDE68A)
val BrandGoldText = Color(0xFFB45309)

// Lavender / Purple (as in "Esmâ Aynası" button)
val BrandPurple = Color(0xFF7C3AED)
val BrandPurpleBg = Color(0xFFF5F3FF)
val BrandPurpleBorder = Color(0xFFDDD6FE)
val BrandPurpleText = Color(0xFF6D28D9)

// Cyan / Sky Blue (as in "Sözlük" button)
val BrandCyan = Color(0xFF0891B2)
val BrandCyanBg = Color(0xFFECFEFF)
val BrandCyanBorder = Color(0xFFA5F3FC)
val BrandCyanText = Color(0xFF0E7490)

// Peach / Orange (as in "Defterim" button)
val BrandPeach = Color(0xFFEA580C)
val BrandPeachBg = Color(0xFFFFF7ED)
val BrandPeachBorder = Color(0xFFFFEDD5)
val BrandPeachText = Color(0xFFC2410C)

// Slate / Neutral (as in "Flash Bellek Kiti" and search bar)
val BrandSlate = Color(0xFF475569)
val BrandSlateBg = Color(0xFFF8FAFC)
val BrandSlateBorder = Color(0xFFE2E8F0)
val BrandSlateText = Color(0xFF334155)

// Secondary & Tertiary
val BrandSecondary = Color(0xFF0E7490)
val BrandSecondaryContainer = Color(0xFFECFEFF)
val BrandOnSecondaryContainer = Color(0xFF155E75)

val BrandTertiary = Color(0xFFD97706)
val BrandTertiaryContainer = Color(0xFFFFFBEB)
val BrandOnTertiaryContainer = Color(0xFF78350F)

// High-Contrast Headings & Body Colors
val TextDark = Color(0xFF0F172A)                   // Deep bold black/slate for main titles
val TextSubheading = Color(0xFF1E293B)             // High-contrast subheadings
val TextMuted = Color(0xFF475569)                  // Clean readable body text
val TextLight = Color(0xFF94A3B8)                  // Placeholder / subtle text

val BackgroundLight = Color(0xFFF8FAFC)            // Clean light slate page background
val SurfaceLight = Color(0xFFFFFFFF)               // Pure crisp white card/surface
val BorderSubtle = Color(0xFFE2E8F0)               // Card & divider outline

// Semantic status tokens
val StatusSuccessBg = Color(0xFFECFDF5)
val StatusSuccessBorder = Color(0xFFA7F3D0)
val StatusSuccessText = Color(0xFF065F46)

val StatusWaitingBg = Color(0xFFFFFBEB)
val StatusWaitingBorder = Color(0xFFFDE68A)
val StatusWaitingText = Color(0xFFB45309)

val StatusRejectBg = Color(0xFFFEF2F2)
val StatusRejectBorder = Color(0xFFFCA5A5)
val StatusRejectText = Color(0xFF991B1B)

val StatusRejectedBg = StatusRejectBg
val StatusRejectedBorder = StatusRejectBorder
val StatusRejectedText = StatusRejectText

val StatusDiamondBg = Color(0xFFECFEFF)
val StatusDiamondBorder = Color(0xFFA5F3FC)
val StatusDiamondText = Color(0xFF0E7490)

// Legacy alias tokens
val PrimaryIndigo = BrandPrimary
val SecondaryTeal = BrandSecondary
val AccentAmber = BrandTertiary
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
