package com.example.loginscreen

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class loginScreen : AppCompatActivity() {

    private lateinit var txtCedula: EditText
    private lateinit var txtCodigo: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        txtCedula = findViewById(R.id.txtCedula)
        txtCodigo = findViewById(R.id.txtCodigo)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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