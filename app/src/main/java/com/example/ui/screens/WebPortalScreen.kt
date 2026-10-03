package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QrCodeCanvas
import com.example.ui.theme.BrandBorderMint
import com.example.ui.theme.BrandGold
import com.example.ui.theme.BrandGoldBg
import com.example.ui.theme.BrandGoldBorder
import com.example.ui.theme.BrandGoldText
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryContainer
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusSuccessBorder
import com.example.ui.theme.StatusSuccessText
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.launch

/**
 * Bilgisayar Web Sitesi & Masaüstü Portalı Görünümü:
 * - Masaüstü web sitesi arayüzü sunar.
 * - Siteden çalışan interaktif "Cep Telefonu Simülatörü" içerir (öğrenci, veli, öğretmen modları).
 * - Siteden cep telefonu versiyonunu (.APK) indirme bağlantısı, QR kod ve kurulum rehberi sağlar.
 * - Kullanıcı dilediğinde doğrudan tam ekran cep telefonu moduna geçiş yapabilir.
 */
@Composable
fun WebPortalScreen(
    onNavigateToMobile: () -> Unit,
    onOpenStudentDemo: () -> Unit,
    onOpenParentDemo: () -> Unit,
    onOpenTeacherDemo: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Canlı web sitesi ve mobil indirme URL'si
    val mobileDownloadUrl = "https://ais-pre-nu7hpo5lbggmotbcu6l2ij-249386399444.europe-west2.run.app"

    var selectedPreviewTab by remember { mutableIntStateOf(0) }

    fun triggerDownload() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mobileDownloadUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            coroutineScope.launch {
                snackbarHostState.showSnackbar("🌐 Cep versiyonu indirme bağlantısı tarayıcıda açıldı.")
            }
        } catch (_: Exception) {
            clipboardManager.setText(AnnotatedString(mobileDownloadUrl))
            coroutineScope.launch {
                snackbarHostState.showSnackbar("📋 İndirme linki panoya kopyalandı! Tarayıcınızdan indirebilirsiniz.")
            }
        }
    }

    fun copyDownloadLink() {
        clipboardManager.setText(AnnotatedString(mobileDownloadUrl))
        coroutineScope.launch {
            snackbarHostState.showSnackbar("✓ Cep telefonu indirme bağlantısı kopyalandı!")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // ==========================================
            // 1. MASAÜSTÜ SİTE ÜST MENÜ BAR (NAVBAR)
            // ==========================================
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BrandPrimary,
                                modifier = Modifier.size(42.dp),
                                shadowElevation = 2.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Din Keşif Atölyesi",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = TextDark
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = BrandPrimaryContainer,
                                        border = BorderStroke(1.dp, BrandBorderMint)
                                    ) {
                                        Text(
                                            text = "WEB PORTALI",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = BrandPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Sorumluluk ve Değerler Eğitimi Platformu",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Üst Bar Hızlı Geçiş Butonları
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = onNavigateToMobile,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("portal_switch_to_mobile_btn")
                            ) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("📱 Cep Moduna Geç", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // 2. HERO BÖLÜMÜ (Masaüstü Tanıtım & İndirme)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = BrandPrimaryContainer,
                            border = BorderStroke(1.dp, BrandBorderMint),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Laptop, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Bilgisayar Web Sitesi & Cep Telefonu Portalı",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary
                                )
                            }
                        }

                        Text(
                            text = "Din Keşif Atölyesi Web Sitesi",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Bu web sitesi üzerinden tüm sınıfın günlük 20 sorumluluk görevini, rozetleri ve haftalık duyuruları takip edebilirsiniz. İsterseniz aşağıdaki bağlantı veya karekod ile cep telefonu versiyonunu (.APK) indirip doğrudan akıllı telefonunuzda kullanabilirsiniz.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // İndirme & Aksiyon Butonları
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { triggerDownload() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("portal_download_apk_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("📥 Cep Versiyonunu İndir (.APK)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = { copyDownloadLink() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("portal_copy_link_btn"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.5.dp, BrandPrimary)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("📋 Bağlantıyı Kopyala", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 3. KAREKOD & KURULUM KARTI
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BrandGoldBg,
                                border = BorderStroke(1.dp, BrandGoldBorder),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.QrCode, contentDescription = null, tint = BrandGold, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "📱 Telefon Kameranızla Tarayın & İndirin",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextDark
                                )
                                Text(
                                    text = "Kameranızı karekoda doğrultarak cep versiyonunu doğrudan telefonunuza indirin.",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Gerçek ZXing Karekod
                        Box(
                            modifier = Modifier
                                .shadow(4.dp, RoundedCornerShape(18.dp))
                                .background(Color.White, RoundedCornerShape(18.dp))
                                .padding(8.dp)
                        ) {
                            QrCodeCanvas(
                                content = mobileDownloadUrl,
                                sizeDp = 180.dp,
                                darkColor = Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = mobileDownloadUrl,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF475569),
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(
                                    onClick = { copyDownloadLink() },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Kopyala", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Kurulum Adımları
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StatusSuccessBg,
                            border = BorderStroke(1.dp, StatusSuccessBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "💡 Cep Telefonuna Kurulum Adımları:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = StatusSuccessText
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("1. Yukarıdaki 'İndir' butonuna basın veya karekodu telefonunuzla tarayın.", fontSize = 12.sp, color = StatusSuccessText)
                                Text("2. İndirilen APK dosyasını açıp 'Yükle' seçeneğine dokunun.", fontSize = 12.sp, color = StatusSuccessText)
                                Text("3. Uygulama telefonunuza yüklenir; tüm özellikleri mobil olarak kullanın!", fontSize = 12.sp, color = StatusSuccessText)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // 4. SİTEDEN ÇALIŞAN CANLI CEP TELEFONU SİMÜLATÖRÜ
                // ==========================================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Canlı Cep Telefonu Simülatörü",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextDark
                                )
                            }
                            Text(
                                text = "Siteden çalışacak cep versiyonunu bu ekranda canlı test edebilirsiniz.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToMobile,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BrandPrimary),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Tam Ekran Aç ↗", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rol Önizleme Sekmeleri
                    TabRow(
                        selectedTabIndex = selectedPreviewTab,
                        containerColor = Color.White,
                        contentColor = BrandPrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedPreviewTab == 0,
                            onClick = { selectedPreviewTab = 0 },
                            text = { Text("🎒 Öğrenci Cep", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedPreviewTab == 1,
                            onClick = { selectedPreviewTab = 1 },
                            text = { Text("👨‍👩‍👧 Veli Cep", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedPreviewTab == 2,
                            onClick = { selectedPreviewTab = 2 },
                            text = { Text("👩‍🏫 Öğretmen Cep", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Realistic Smartphone Frame Mockup
                    Surface(
                        shape = RoundedCornerShape(36.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(4.dp, Color(0xFF0F172A)),
                        shadowElevation = 10.dp,
                        modifier = Modifier
                            .widthIn(max = 380.dp)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Phone Speaker & Dynamic Island Top
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 60.dp, height = 5.dp)
                                        .background(Color(0xFF334155), RoundedCornerShape(3.dp))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(Color(0xFF334155), CircleShape)
                                )
                            }

                            // Phone Screen Glass Area
                            Surface(
                                shape = RoundedCornerShape(26.dp),
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(480.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    // Mini Phone Top Bar
                                    Surface(
                                        color = BrandPrimary,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Din Keşif Atölyesi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = Color.White.copy(alpha = 0.25f)
                                            ) {
                                                Text(
                                                    text = when (selectedPreviewTab) {
                                                        0 -> "Öğrenci"
                                                        1 -> "Veli"
                                                        else -> "Öğretmen"
                                                    },
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Mini Phone Screen Content based on selected tab
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .background(Color(0xFFF8FAFC))
                                            .padding(12.dp)
                                    ) {
                                        when (selectedPreviewTab) {
                                            0 -> {
                                                // Student Mini Preview Card
                                                Column(modifier = Modifier.fillMaxSize()) {
                                                    Surface(
                                                        shape = RoundedCornerShape(14.dp),
                                                        color = BrandPrimaryContainer,
                                                        border = BorderStroke(1.dp, BrandBorderMint),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(12.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(24.dp))
                                                            Spacer(modifier = Modifier.width(10.dp))
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text("Değerli Öğrencim", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                                                                Text("45 💎 Elmas • 3 Günlük Seri 🔥", fontSize = 11.sp, color = BrandPrimary, fontWeight = FontWeight.SemiBold)
                                                            }
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(10.dp))

                                                    Text("Tanımlanan 20 Görev (Örnekler):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)

                                                    Spacer(modifier = Modifier.height(6.dp))

                                                    MiniTaskItem("Yatağımı topladım ve odamı düzenledim", "Ev Görevi", true)
                                                    MiniTaskItem("Ders araç gereçlerimi hazırladım", "Okul Görevi", true)
                                                    MiniTaskItem("Günün namaz ve dua vaktine riayet ettim", "Dini Sorumluluk", false)
                                                    MiniTaskItem("Yemekte ve konuşmada adap kurallarına uydum", "Adap & Görgü", false)

                                                    Spacer(modifier = Modifier.weight(1f))

                                                    Button(
                                                        onClick = onOpenStudentDemo,
                                                        modifier = Modifier.fillMaxWidth().height(38.dp),
                                                        shape = RoundedCornerShape(10.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                                                    ) {
                                                        Text("Öğrenci Canlı Ekranına Git (Değerli Öğrencim)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }

                                            1 -> {
                                                // Parent Mini Preview Card
                                                Column(modifier = Modifier.fillMaxSize()) {
                                                    Surface(
                                                        shape = RoundedCornerShape(14.dp),
                                                        color = Color(0xFFFEF3C7),
                                                        border = BorderStroke(1.dp, Color(0xFFD97706)),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(12.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(24.dp))
                                                            Spacer(modifier = Modifier.width(10.dp))
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text("Saygıdeğer Velim", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF78350F))
                                                                Text("Öğrenci: Değerli Öğrencim • 3 Onay Bekleyen", fontSize = 11.sp, color = Color(0xFFB45309), fontWeight = FontWeight.SemiBold)
                                                            }
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(10.dp))

                                                    Text("Veli Kontrol Paneli Özellikleri:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                                                    Spacer(modifier = Modifier.height(6.dp))

                                                    MiniFeatureItem("✓ Çocuğun görevlerini tek dokunuşla onaylama")
                                                    MiniFeatureItem("✓ 20 Özel Görev belirleme ve özelleştirme")
                                                    MiniFeatureItem("✓ Aylık ödül ve elmas hedefini belirleme")

                                                    Spacer(modifier = Modifier.weight(1f))

                                                    Button(
                                                        onClick = onOpenParentDemo,
                                                        modifier = Modifier.fillMaxWidth().height(38.dp),
                                                        shape = RoundedCornerShape(10.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                                                    ) {
                                                        Text("Veli Canlı Ekranına Git (Saygıdeğer Velim)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    }
                                                }
                                            }

                                            2 -> {
                                                // Teacher Mini Preview Card
                                                Column(modifier = Modifier.fillMaxSize()) {
                                                    Surface(
                                                        shape = RoundedCornerShape(14.dp),
                                                        color = Color(0xFFDCFCE7),
                                                        border = BorderStroke(1.dp, Color(0xFF16A34A)),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(12.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(24.dp))
                                                            Spacer(modifier = Modifier.width(10.dp))
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text("Sayın Öğretmenim", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF14532D))
                                                                Text("5-A Sınıfı • 24 Öğrenci Kayıtlı", fontSize = 11.sp, color = Color(0xFF15803D), fontWeight = FontWeight.SemiBold)
                                                            }
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(10.dp))

                                                    Text("Öğretmen Masası İşlevleri:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                                                    Spacer(modifier = Modifier.height(6.dp))

                                                    MiniFeatureItem("✓ Sınıf içi liderlik sıralaması & elmas takibi")
                                                    MiniFeatureItem("✓ Günün Değerler Sözü ve sınıf duyuruları")
                                                    MiniFeatureItem("✓ Karekod ile hızlı öğrenci ve veli kaydı")

                                                    Spacer(modifier = Modifier.weight(1f))

                                                    Button(
                                                        onClick = onOpenTeacherDemo,
                                                        modifier = Modifier.fillMaxWidth().height(38.dp),
                                                        shape = RoundedCornerShape(10.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                                                    ) {
                                                        Text("Öğretmen Canlı Ekranına Git (Sayın Öğretmenim)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Mini Phone Bottom Home Bar
                                    Surface(
                                        color = Color.White,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(width = 80.dp, height = 4.dp)
                                                    .background(Color(0xFFCBD5E1), RoundedCornerShape(2.dp))
                                            )
                                        }
                                    }
                                }
                            }

                            // Bottom Chin with Home Indicator
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(width = 90.dp, height = 4.dp)
                                    .background(Color(0xFF475569), RoundedCornerShape(2.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "📱 Bu simülatör, cep telefonunuza kuracağınız APK'nın birebir aynısıdır.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // 5. DOĞRUDAN KAYIT & GİRİŞ KARTI
                // ==========================================
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🚀 Hemen Kullanmaya Başlayın",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Web üzerinden veya cep telefonunuzdan öğrenci, veli veya öğretmen olarak sisteme kaydolabilir veya giriş yapabilirsiniz.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onNavigateToRegister,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("portal_go_to_register_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Text("Lütfen Giriş ve Kayıt Bölümüne Geçiniz", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}

@Composable
private fun MiniTaskItem(
    title: String,
    category: String,
    done: Boolean
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, if (done) BrandBorderMint else Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (done) Icons.Default.CheckCircle else Icons.Default.Star,
                contentDescription = null,
                tint = if (done) BrandPrimary else Color(0xFFCBD5E1),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1, color = TextDark)
                Text(text = category, fontSize = 9.sp, color = TextMuted)
            }
            if (done) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BrandGoldBg,
                    border = BorderStroke(1.dp, BrandGoldBorder)
                ) {
                    Text("+5 💎", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandGoldText, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                }
            }
        }
    }
}

@Composable
private fun MiniFeatureItem(
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
    }
}
