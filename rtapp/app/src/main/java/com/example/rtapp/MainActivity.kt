package com.example.rtapp

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tv = TextView(this)
        tv.textSize = 16f
        tv.setPadding(30, 80, 30, 30)
        setContentView(tv)

        try {
            val loader = TFLiteLoader(this)
            val interpreter = loader.createInterpreter()

            // Create a dummy input image (260x260 red)
            val inputBitmap = Bitmap.createBitmap(260, 260, Bitmap.Config.ARGB_8888)
            inputBitmap.eraseColor(Color.RED)

            val inputTensor = ImageUtils.bitmapToInputTensor(inputBitmap)

            val output = Array(1) { FloatArray(4) }

            interpreter.run(inputTensor, output)

            val outStr = output[0].joinToString(prefix = "[", postfix = "]") { "%.4f".format(it) }

            tv.text = "Inference OK\n\n" +
                      "Model input shape: [1,260,260,3]\n" +
                      "Output shape: [1,4]\n\n" +
                      "Output values: $outStr"

        } catch (e: Exception) {
            tv.text = "CRASH:\n${e.javaClass.name}\n\n${e.message}"
        }
    }
}
