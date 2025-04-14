package com.example.loginscreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton // Importa MaterialButton

class loginScreen : AppCompatActivity() {

    private lateinit var txtCedula: EditText
    private lateinit var txtCodigo: EditText
    private lateinit var nextScreenButton: MaterialButton // Declara MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        txtCedula = findViewById(R.id.txtCedula)
        txtCodigo = findViewById(R.id.txtCodigo)
        nextScreenButton = findViewById(R.id.NextScreenButton) // Inicializa MaterialButton
        // Dentro del método onCreate(), añade esto:
        findViewById<Button>(R.id.button).setOnClickListener {
            startActivity(Intent(this, ScreenAftherLog::class.java))
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Añade el listener para el MaterialButton NextScreenButton
        nextScreenButton.setOnClickListener {
            val intent = Intent(this, SecondLoginScreen::class.java)
            startActivity(intent)
            // Si quieres cerrar loginScreen después de abrir SecondLoginScreen, usa:
            // finish()
        }
    }

    fun clickButtonIngresar(view: View) {
        val url = "http://192.168.100.130/android_mysql_proyectExpotecnica/insertar_Estudiantes.php"
        val queue = Volley.newRequestQueue(this)

        val resultadoPost = object : StringRequest(Request.Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this, "Datos ingresados: $response", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message ?: "Error desconocido"}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf(
                    "Numero-de-Cedula" to txtCedula.text.toString(),
                    "codigo-de-entrada-estudiante" to txtCodigo.text.toString()
                )
            }
        }
        queue.add(resultadoPost)
    }
}