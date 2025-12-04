package com.example.rtapp

import android.content.Context

class TFLiteLabels(private val context: Context) {

    fun loadLabels(): List<String> {
        val inputStream = context.assets.open("labels.txt")
        return inputStream.bufferedReader().readLines()
    }
}
