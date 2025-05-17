package com.example.loginscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
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
    private lateinit var numCedula: EditText
    private lateinit var numTelefonico: EditText
    private lateinit var gradoEst: EditText

    private lateinit var imagePreview: ImageView
    private lateinit var cameraPreview: PreviewView

    private lateinit var cameraExecutor: ExecutorService

    // Permiso cámara
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Permiso almacenamiento (galería)
    private val requestStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImageChooser()
        } else {
            Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Selección de imagen con ActivityResultLauncher
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                imagePreview.setImageURI(uri)
                imagePreview.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen_afther_log)

        // Inicializar views
        nameComplete = findViewById(R.id.NameComplete)
        seccionEstudiante = findViewById(R.id.SeccionEstudiante)
        numCedula = findViewById(R.id.NumCedula)
        numTelefonico = findViewById(R.id.numTelefonico)
        gradoEst = findViewById(R.id.GradoEst)

        imagePreview = findViewById(R.id.imagePreview)
        cameraPreview = findViewById(R.id.cameraPreview)

        cameraExecutor = Executors.newSingleThreadExecutor()

        val myTextView = findViewById<TextView>(R.id.textView)
        myTextView.background = ContextCompat.getDrawable(this, R.drawable.rounded_box)

        // Botón seleccionar imagen desde galería
        findViewById<Button>(R.id.btnSelectImage).setOnClickListener {
            checkStoragePermissionAndSelectImage()
        }

        // Botón para iniciar cámara y verificar
        findViewById<Button>(R.id.btn_verificarr).setOnClickListener {
            checkCameraPermission()
        }

        // Botón para enviar datos y avanzar pantalla
        findViewById<Button>(R.id.NextScrenAfterData).setOnClickListener {
            val intent = Intent(this, AftherEverything::class.java)
            startActivity(intent)
        }

        // Manejo de insets para UI adaptativa
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
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun checkStoragePermissionAndSelectImage() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                openImageChooser()
            }
            else -> {
                requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(cameraPreview.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            try {
                                processImage(imageProxy)
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this,
                                        "Error al procesar imagen: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } finally {
                                imageProxy.close()
                            }
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

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
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImage(imageProxy: ImageProxy) {
        // Aquí puedes integrar análisis de imagen, reconocimiento facial, etc.
        // Por ahora solo muestra un Toast (puedes quitarlo para no saturar)
        runOnUiThread {
            Toast.makeText(this, "Procesando imagen", Toast.LENGTH_SHORT).show()
        }
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
                    "Sesión" to seccionEstudiante.text.toString(),
                    "Numero-de-Cedula" to numCedula.text.toString(),
                    "Numero-de-Telefono" to numTelefonico.text.toString(),
                    "Grado" to gradoEst.text.toString()
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
