package com.example.rtapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Size
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var interpreterLoader: TFLiteLoader
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        interpreterLoader = TFLiteLoader(this)
        val interpreter = interpreterLoader.createInterpreter()

        // Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            return
        }

        val previewView = findViewById<PreviewView>(R.id.previewView)
        val btn = findViewById<Button>(R.id.btnCapture)
        val img = findViewById<ImageView>(R.id.imageResult)
        val txt = findViewById<TextView>(R.id.txtOutput)

        startCamera(previewView)

        btn.setOnClickListener {
            captureAndClassify(interpreter, img, txt)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            // IMPORTANT: Model input is 260×260
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(260, 260))
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureAndClassify(
        interpreter: org.tensorflow.lite.Interpreter,
        img: ImageView,
        txt: TextView
    ) {

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {

                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    try {
                        // Convert YUV → RGB Bitmap
                        val bitmap = imageProxyToBitmap(imageProxy)
                        img.setImageBitmap(bitmap)

                        // Resize to model input size
                        val resized = Bitmap.createScaledBitmap(bitmap, 260, 260, true)

                        // Convert bitmap → input tensor
                        val tensor = ImageUtils.bitmapToInputTensor(resized)

                        // Output for 4 classes
                        val output = Array(1) { FloatArray(4) }

                        try {
                            // Safe interpreter call
                            interpreter.run(tensor, output)
                        } catch (e: Exception) {
                            txt.text = "run() error: ${e.javaClass.name}: ${e.message}"
                            return
                        }

                        // Display result
                        val outStr = output[0].joinToString(
                            prefix = "[",
                            postfix = "]"
                        ) { "%.4f".format(it) }

                        txt.text = "Output = $outStr"

                    } catch (e: Exception) {
                        txt.text = "Error (outside run): ${e.javaClass.name}: ${e.message}"
                    } finally {
                        imageProxy.close()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    txt.text = "Capture error: ${exception.message}"
                }
            }
        )
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val converter = YuvToRgbConverter()
        return converter.yuvToRgb(image)
    }
}
