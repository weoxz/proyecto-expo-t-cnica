package com.example.loginscreen

import android.Manifest
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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class ScreenAftherLog : AppCompatActivity() {

    private lateinit var NameComplete: EditText
    private lateinit var SeccionEstudiante: EditText
    private lateinit var  NumCedula: EditText
    private lateinit var  numTelefonico: EditText
    private lateinit var  GradoEst: EditText
    private lateinit var  button2: Button
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImageChooser()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                val imagePreview = findViewById<ImageView>(R.id.imagePreview)
                imagePreview.setImageURI(uri)
                imagePreview.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen_afther_log)
        NameComplete = findViewById(R.id.NameComplete)
        SeccionEstudiante = findViewById(R.id.SeccionEstudiante)
        NumCedula = findViewById(R.id.NumCedula)
        numTelefonico = findViewById(R.id.numTelefonico)
        GradoEst = findViewById(R.id.GradoEst)


        // Configuración del TextView existente
        val myTextView = findViewById<TextView>(R.id.textView)
        myTextView.background = ContextCompat.getDrawable(this, R.drawable.rounded_box)

        // Botón para seleccionar imagen
        findViewById<Button>(R.id.btnSelectImage).setOnClickListener {
            checkPermissionAndSelectImage()
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            val intent = Intent(this, AftherEverything::class.java)
            startActivity(intent)
        }

        // Manejo de insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkPermissionAndSelectImage() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
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
        pickImageLauncher.launch(intent)
    }

    fun clickButtonUpdateDB(view: View) {
        val url = "http://192.168.100.130/android_mysql_proyectExpotecnica/insertar_Estudiantes.php"
        val queue = Volley.newRequestQueue(this)

        val resultadoPost = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this, "Datos ingresados: $response", Toast.LENGTH_SHORT).show()
                // Guardar el ID del usuario en SharedPreferences
                val sharedPref = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("id_usuario", response.trim()) // trim() por si el servidor devuelve con espacios
                    apply()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message ?: "Error desconocido"}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf(
                    "Nombre-de-Estudiantes" to NameComplete.text.toString(),
                    "Sesión" to SeccionEstudiante.text.toString(),
                    "Numero-de-Cedula" to NumCedula.text.toString(),
                    "Numero-de-Telefono" to numTelefonico.text.toString(),
                    "Grado" to GradoEst.text.toString()
                )
            }
        }

        //Yo si le sé a lo que es programar perrooo!!! posdata No cambie nada
        queue.add(resultadoPost)
    }

}