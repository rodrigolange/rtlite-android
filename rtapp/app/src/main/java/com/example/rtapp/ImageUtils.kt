package com.example.rtapp

import android.graphics.Bitmap
import java.nio.ByteBuffer
import java.nio.ByteOrder

object ImageUtils {

    // Returns tensor suitable for a 260×260×3 float32 TFLite model
    fun bitmapToInputTensor(bitmap: Bitmap): ByteBuffer {
        val width = bitmap.width
        val height = bitmap.height

        // 1 × 260 × 260 × 3 × 4 bytes (float32)
        val inputSize = 1 * width * height * 3 * 4
        val buffer = ByteBuffer.allocateDirect(inputSize)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        var pixelIndex = 0

        for (y in 0 until height) {
            for (x in 0 until width) {
                val p = pixels[pixelIndex++]

                val r = ((p shr 16) and 0xFF) / 255f
                val g = ((p shr 8) and 0xFF) / 255f
                val b = (p and 0xFF) / 255f

                buffer.putFloat(r)
                buffer.putFloat(g)
                buffer.putFloat(b)
            }
        }

        buffer.rewind()
        return buffer
    }
}
