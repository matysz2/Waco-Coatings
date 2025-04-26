package com.example.waco.camera


import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class CameraAnalyzer(private val onBitmapReady: (Bitmap) -> Unit) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        val bitmap = image.toBitmap() ?: return
        onBitmapReady(bitmap)
        image.close()
    }

    private fun ImageProxy.toBitmap(): Bitmap? {
        val planeProxy = planes.firstOrNull() ?: return null
        val buffer = planeProxy.buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            copyPixelsFromBuffer(java.nio.ByteBuffer.wrap(bytes))
        }
    }
}
