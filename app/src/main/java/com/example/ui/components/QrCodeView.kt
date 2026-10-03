package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlin.math.floor

/**
 * Standart (ISO/IEC 18004 uyumlu) QR kod çizimi.
 *
 * Not: Önceki sürümdeki elle yazılmış "QR benzeri" matris üreteci geçerli bir QR kodu
 * üretmiyordu; hiçbir telefon kamerası okuyamıyordu. Artık ZXing ile gerçek QR üretiliyor.
 */
@Composable
fun QrCodeCanvas(
    content: String,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 200.dp,
    darkColor: Color = Color(0xFF1B1B1B),
    lightColor: Color = Color.White
) {
    // Matris yalnızca içerik değiştiğinde yeniden hesaplanır (her recomposition'da değil).
    val matrix = remember(content) { encodeQr(content) }

    Box(
        modifier = modifier
            .size(sizeDp)
            .background(lightColor, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        if (matrix == null) {
            Text(
                text = "Karekod oluşturulamadı",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9E9E9E),
                textAlign = TextAlign.Center
            )
        } else {
            val moduleCount = matrix.size
            Canvas(modifier = Modifier.matchParentSize()) {
                // Modül boyutunu tam sayıya yuvarlamak, kenarlardaki yarım piksel
                // kaymalarını ve okuma hatalarını engeller.
                val rawModule = size.minDimension / moduleCount
                val moduleSize = floor(rawModule).coerceAtLeast(1f)
                val drawn = moduleSize * moduleCount
                val offsetX = (size.width - drawn) / 2f
                val offsetY = (size.height - drawn) / 2f

                for (r in 0 until moduleCount) {
                    val row = matrix[r]
                    var c = 0
                    while (c < moduleCount) {
                        if (row[c]) {
                            // Yan yana siyah modülleri tek dikdörtgen olarak çiz: çizim maliyeti düşer.
                            var end = c
                            while (end + 1 < moduleCount && row[end + 1]) end++
                            drawRect(
                                color = darkColor,
                                topLeft = Offset(offsetX + c * moduleSize, offsetY + r * moduleSize),
                                size = Size(moduleSize * (end - c + 1), moduleSize)
                            )
                            c = end + 1
                        } else {
                            c++
                        }
                    }
                }
            }
        }
    }
}

/** İçeriği QR matrisine çevirir; hata durumunda null döner. */
private fun encodeQr(content: String): Array<BooleanArray>? {
    if (content.isBlank()) return null
    return runCatching {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1
        )
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 0, 0, hints)
        Array(bitMatrix.height) { y ->
            BooleanArray(bitMatrix.width) { x -> bitMatrix.get(x, y) }
        }
    }.getOrNull()
}
