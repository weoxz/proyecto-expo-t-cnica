package com.example.loginscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScreenAftherLog : AppCompatActivity() {

    private lateinit var nameComplete: EditText
    private lateinit var seccionEstudiante: EditText
    private lateinit var cameraExecutor: ExecutorService

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
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen_afther_log)

        nameComplete = findViewById(R.id.NameComplete)
        seccionEstudiante = findViewById(R.id.SeccionEstudiante)
        cameraExecutor = Executors.newSingleThreadExecutor()

        val myTextView = findViewById<TextView>(R.id.textView)
        myTextView.background = ContextCompat.getDrawable(this, R.drawable.rounded_box)

        findViewById<Button>(R.id.btnSelectImage).setOnClickListener {
            checkPermissionAndSelectImage()
        }

        findViewById<Button>(R.id.btn_verificarr).setOnClickListener {
            checkCameraPermission()
        }

        findViewById<Button>(R.id.NextScrenAfterData).setOnClickListener {
            val intent = Intent(this, AftherEverything::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(findViewById<PreviewView>(R.id.cameraPreview).surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, { imageProxy ->
                            try {
                                processImage(imageProxy)
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(this, "Error al procesar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            } finally {
                                imageProxy.close()
                            }
                        })
                    }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
                    runOnUiThread {
                        Toast.makeText(this, "Cámara Iniciada", Toast.LENGTH_SHORT).show()
                    }
                } catch (exc: Exception) {
                    runOnUiThread {
                        Toast.makeText(this, "Error al iniciar la cámara: ${exc.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al obtener el proveedor de cámara: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImage(imageProxy: ImageProxy) {
        runOnUiThread {
            Toast.makeText(this, "Procesando imagen", Toast.LENGTH_SHORT).show()
        }
        // Aquí podrías integrar la lógica de MediaPipe para la verificación facial
    }

    private fun checkPermissionAndSelectImage() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                openImageChooser()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, 100)
    }

    fun clickButtonUpdateDB(view: View) {
        val url = "http://192.168.100.130/android_mysql_proyectExpotecnica/insertar_Estudiantes.php"
        val queue = Volley.newRequestQueue(this)

        val resultadoPost = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this, "Datos ingresados: $response", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message ?: "Error desconocido"}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf(
                    "Nombre-de-Estudiantes" to nameComplete.text.toString(),
                    "Sesión" to seccionEstudiante.text.toString()
                )
            }
        }
        queue.add(resultadoPost)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
