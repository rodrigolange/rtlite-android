package com.example.rtapp

import android.app.Activity
import android.os.Bundle
import android.util.Log

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loader = TFLiteLoader(this)
        val interpreter = loader.createInterpreter()

        // Print input / output details to Logcat
        val inputShape = interpreter.getInputTensor(0).shape()
        val inputType = interpreter.getInputTensor(0).dataType()

        val outputShape = interpreter.getOutputTensor(0).shape()
        val outputType = interpreter.getOutputTensor(0).dataType()

        Log.d("RTApp", "Input shape: ${inputShape.contentToString()}")
        Log.d("RTApp", "Input type: $inputType")
        Log.d("RTApp", "Output shape: ${outputShape.contentToString()}")
        Log.d("RTApp", "Output type: $outputType")

        Log.d("RTApp", "Label count: ${TFLiteLabels(this).loadLabels().size}")
    }

}
