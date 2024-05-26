package com.binay.shaw.justap.utilities.composeUtils

/** Taken from: https://gist.github.com/dev-niiaddy/8f936062291e3d328c7d10bb644273d0 */
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Creates a QR code bitmap from the given content and size.
 *
 * @param content The content to encode in the QR code.
 * @param size The size of the QR code bitmap in density-independent pixels (dp).
 *
 * @return A bitmap containing the generated QR code, or null if an error occurs.
 *
 * Sample:
 * ```
 * val bitmap = rememberQrBitmap(content = "https://example.com", size = 200.dp)
 * ```
 */
@Composable
fun rememberQrBitmap(content: String?, size: Dp): Bitmap? {
    val density = LocalDensity.current
    val sizePx = with(density) { size.roundToPx() }

    if (content == null) return null

    var bitmap by remember(content) {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(bitmap) {
        if (bitmap != null) return@LaunchedEffect

        launch(Dispatchers.IO) {
            val qrCodeWriter = QRCodeWriter()

            val encodeHints = mutableMapOf<EncodeHintType, Any?>().apply {
                this[EncodeHintType.MARGIN] = 0
            }

            val bitmapMatrix = try {
                qrCodeWriter.encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    sizePx,
                    sizePx,
                    encodeHints,
                )
            } catch (ex: WriterException) {
                null
            }

            val matrixWidth = bitmapMatrix?.width ?: sizePx
            val matrixHeight = bitmapMatrix?.height ?: sizePx

            val newBitmap = Bitmap.createBitmap(
                bitmapMatrix?.width ?: sizePx,
                bitmapMatrix?.height ?: sizePx,
                Bitmap.Config.ARGB_8888,
            )

            val pixels = IntArray(matrixWidth * matrixHeight)

            for (x in 0 until matrixWidth) {
                for (y in 0 until matrixHeight) {
                    val shouldColorPixel = bitmapMatrix?.get(x, y) ?: false
                    val pixelColor =
                        if (shouldColorPixel) android.graphics.Color.BLACK else android.graphics.Color.WHITE

                    pixels[y * matrixWidth + x] = pixelColor
                }
            }

            newBitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)

            bitmap = newBitmap
        }
    }

    return bitmap
}