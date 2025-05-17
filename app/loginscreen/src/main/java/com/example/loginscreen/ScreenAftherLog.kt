package com.example.loginscreen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mediapipe.framework.PacketGetter
import com.google.mediapipe.framework.AndroidPacketCreator
import com.google.mediapipe.framework.Packet
import com.google.mediapipe.framework.AndroidAssetUtil
import com.google.mediapipe.framework.AppTextureFrame
import com.google.mediapipe.framework.TextureFrame
import com.google.mediapipe.framework.AndroidPacketCreator
import com.google.mediapipe.components.FrameProcessor
import com.google.mediapipe.components.PermissionHelper
import com.google.mediapipe.glutil.EglManager
import com.google.mediapipe.modules.face_detection.FaceDetection
import com.google.mediapipe.modules.face_detection.FaceDetectionResult
import com.google.mediapipe.modules.face_detection.FaceDetectionOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScreenAftherLog : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView

    private lateinit var eglManager: EglManager
    private lateinit var frameProcessor: FrameProcessor

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_afther_log)

        previewView = findViewById(R.id.cameraPreview)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Inicializa MediaPipe asset manager (carga los archivos necesarios)
        AndroidAssetUtil.initializeNativeAssetManager(this)

        eglManager = EglManager(null)
        eglManager.createContext()

        // Inicializa FrameProcessor con el grafo de Face Detection
        frameProcessor = FrameProcessor(
            this,
            eglManager.nativeContext,
            "face_detection_mobile_gpu.binarypb",
            "input_video",
            "output_video"
        )

        frameProcessor.videoSurfaceOutput.setSurface(previewView.surfaceProvider.surface)

        frameProcessor.addPacketCallback("detections") { packet ->
            val faceDetections = PacketGetter.getProtoVector(packet, FaceDetectionResult.parser())
            runOnUiThread {
                if (faceDetections.isNotEmpty()) {
                    Toast.makeText(this, "Cara detectada!", Toast.LENGTH_SHORT).show()
                    // Aquí puedes agregar tu lógica para la verificación facial
                }
            }
        }

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                // Envía el frame al FrameProcessor para que MediaPipe lo analice
                frameProcessor.onNewFrame(imageProxy.image!!, rotationDegrees)
                imageProxy.close()
            }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (exc: Exception) {
                Toast.makeText(this, "Error al iniciar cámara: ${exc.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        frameProcessor.close()
        eglManager.release()
    }
}

