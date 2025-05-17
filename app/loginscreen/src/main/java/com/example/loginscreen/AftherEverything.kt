@file:Suppress("UnusedImport")

package com.example.loginscreen

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.content.SharedPreferences
import org.json.JSONException


class AftherEverything : AppCompatActivity() {

    private lateinit var contenedorMensajes: FrameLayout
    private lateinit var contenedorDatos: FrameLayout
    private lateinit var contenedorEnviar: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_afther_everything)

        contenedorMensajes = findViewById(R.id.contenedorMensajes)
        contenedorDatos = findViewById(R.id.contenedorDatos)
        contenedorEnviar = findViewById(R.id.contenedorEnviar)

        val btnMsj = findViewById<Button>(R.id.btnMsj)
        val btnDatosEstudiante = findViewById<Button>(R.id.btnDatosEstudiante)
        val btnEnviarMensaje = findViewById<Button>(R.id.btnEnviarMensaje)

        btnMsj.setOnClickListener {
            mostrarContenedor(contenedorMensajes)
        }

        btnDatosEstudiante.setOnClickListener {
            mostrarContenedor(contenedorDatos)
            val idUsuario = obtenerIdUsuario()
            if (idUsuario != null) {
                obtenerDatosEstudiante(idUsuario)
            } else {
                Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }

        btnEnviarMensaje.setOnClickListener {
            mostrarContenedor(contenedorEnviar)
        }
    }

    private fun mostrarContenedor(contenedorMostrado: FrameLayout) {
        contenedorMensajes.visibility = View.GONE
        contenedorDatos.visibility = View.GONE
        contenedorEnviar.visibility = View.GONE
        contenedorMostrado.visibility = View.VISIBLE
    }

    private fun obtenerIdUsuario(): String? {
        val sharedPref: SharedPreferences = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        return sharedPref.getString("id_usuario", null)
    }

    private fun obtenerDatosEstudiante(idUsuario: String) {
        val url = "http://192.168.100.130/android_mysql_proyectExpotecnica/registro.php?id=$idUsuario"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val json = JSONObject(response)

                    findViewById<TextView>(R.id.NomText).text = "NOMBRE: ${json.getString("Nombre-de-Estudiantes")}"
                    findViewById<TextView>(R.id.numTextcedu).text = "NUMERO DE CEDULA: ${json.getString("Numero-de-Cedula")}"
                    findViewById<TextView>(R.id.numTeltext).text = "NUMERO TELEFONICO: ${json.getString("Numero-de-Telefono")}"
                    findViewById<TextView>(R.id.seccText).text = "SECCIÓN: ${json.getString("Sesión")}"
                    findViewById<TextView>(R.id.gradoText).text = "GRADO: ${json.getString("Grado")}"

                } catch (e: JSONException) {
                    Toast.makeText(this, "Error al procesar JSON: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al obtener datos: ${error.message ?: "error desconocido"}", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(stringRequest)
    }
}