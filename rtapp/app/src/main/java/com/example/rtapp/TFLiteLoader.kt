package com.example.rtapp

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TFLiteLoader(private val context: Context) {

    private fun loadModelFile(): ByteBuffer {
        val assetFile = context.assets.open("model.tflite")
        val bytes = assetFile.readBytes()
        val buffer = ByteBuffer.allocateDirect(bytes.size)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(bytes)
        return buffer
    }

    fun createInterpreter(): Interpreter {
        val model = loadModelFile()
        val options = Interpreter.Options()
        options.setNumThreads(4)  // Adjust if needed
        return Interpreter(model, options)
    }
}
