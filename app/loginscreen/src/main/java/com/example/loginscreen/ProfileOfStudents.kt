package com.example.loginscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ProfileOfStudents : AppCompatActivity() {

    private lateinit var btnIngresarDatos: Button
    private lateinit var btnDatosEstudiante: Button
    private lateinit var btnDatosDiarios: Button
    private lateinit var contenedorDatos: FrameLayout
    private lateinit var contenedorDatosDiarios: FrameLayout  // Renombrado correctamente

    private lateinit var recyclerView: RecyclerView
    lateinit var listaMensajes: MutableList<Mensaje>
    private lateinit var adapter: MensajeAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_of_students)



        listaMensajes = mutableListOf()

        adapter = MensajeAdapter(listaMensajes) { mensaje ->
            // Autocompletar remitente y destinatario para responder

            // Mostrar formulario de envío directo (puedes ocultar los otros si prefieres)

            // O puedes solo enfocarte en edtMensajeRespuesta si quieres mantenerlo en el mismo frame

        }
        recyclerView = findViewById(R.id.recyclerMensajes)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.adapter = adapter

        verificarIdentidad()


        btnIngresarDatos = findViewById(R.id.btnIngresarDatos)
        btnDatosEstudiante = findViewById(R.id.btnDatosEstudiante)
        btnDatosDiarios = findViewById(R.id.btnDatosDiarios)
        contenedorDatos = findViewById(R.id.contenedorDatos)
        contenedorDatosDiarios = findViewById(R.id.contenedorDatosDiarios)  // ID correcto

        val gris = ContextCompat.getColor(this, R.color.gray)
        val negro = Color.BLACK

        btnDatosEstudiante.setOnClickListener {
            // Mostrar datos del estudiante
            contenedorDatos.visibility = View.VISIBLE
            contenedorDatosDiarios.visibility = View.GONE

            // Colores de los botones
            btnDatosEstudiante.setBackgroundColor(negro)
            btnDatosDiarios.setBackgroundColor(gris)

            val idUsuario = obtenerIdUsuario()
            if (idUsuario != null) {
                obtenerDatosEstudiante(idUsuario)
            } else {
                Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show()
            }

        }

        btnDatosDiarios.setOnClickListener {
            // Mostrar datos diarios
            contenedorDatosDiarios.visibility = View.VISIBLE
            contenedorDatos.visibility = View.GONE

            // Colores de los botones
            btnDatosDiarios.setBackgroundColor(negro)
            btnDatosEstudiante.setBackgroundColor(gris)

            val nombreUsuario = obtenerNombreUsuario()
            if (nombreUsuario != null) {
                obtenerMensajes(nombreUsuario)
            } else {
                Toast.makeText(this, "Nombre de usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }

        btnIngresarDatos.setOnClickListener {
            startActivity(Intent(this, ScreenAftherLog::class.java))
        }



    }
    private fun obtenerIdUsuario(): String? {
        val sharedPref: SharedPreferences = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        return sharedPref.getString("id_usuario", null)
    }

    private fun obtenerNombreUsuario(): String? {
        val sharedPref = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        return sharedPref.getString("nombre_usuario", null)
    }

    private fun obtenerDatosEstudiante(idUsuario: String) {
        val url =
            "http://192.168.100.130/android_mysql_proyectExpotecnica/registro.php?id=$idUsuario"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val json = JSONObject(response)

                    findViewById<TextView>(R.id.NomText).text =
                        "NOMBRE: ${json.getString("nombre")}"
                    findViewById<TextView>(R.id.numTextcedu).text =
                        "NUMERO DE CEDULA: ${json.getString("cedula")}"
                    findViewById<TextView>(R.id.numTeltext).text =
                        "NUMERO TELEFONICO: ${json.getString("telefono")}"
                    findViewById<TextView>(R.id.seccText).text =
                        "SECCIÓN: ${json.getString("seccion")}"
                    findViewById<TextView>(R.id.gradoText).text =
                        "GRADO: ${json.getString("grado")}"

                } catch (e: JSONException) {
                    Toast.makeText(this, "Error al procesar JSON: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error al obtener datos: ${error.message ?: "error desconocido"}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        Volley.newRequestQueue(this).add(stringRequest)
    }
    private fun verificarIdentidad() {
        val btnIngresarDatos = findViewById<Button>(R.id.btnIngresarDatos)
        val sharedPref = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        val nombreUsuario = sharedPref.getString("nombre_usuario", "")

        if (!nombreUsuario.isNullOrEmpty()) {
            val url = "http://192.168.100.130/android_mysql_proyectExpotecnica/verificar_identidad.php?nombre=$nombreUsuario"
            val requestQueue = Volley.newRequestQueue(this)

            val request = StringRequest(
                Request.Method.GET, url,
                Response.Listener { response ->
                    // Verificamos si la respuesta parece HTML por error
                    if (response.trim().startsWith("<")) {
                        Log.e("ERROR_JSON", "La respuesta no es JSON: $response")
                        Toast.makeText(this, "Error en el servidor", Toast.LENGTH_LONG).show()
                        btnIngresarDatos.visibility = View.GONE
                        return@Listener
                    }

                    try {
                        val json = JSONObject(response)
                        val identidadSubida = json.getBoolean("identidad_subida")
                        btnIngresarDatos.visibility = if (identidadSubida) View.GONE else View.VISIBLE
                    } catch (e: JSONException) {
                        Log.e("ERROR_JSON", "JSON mal formado: $response")
                        Toast.makeText(this, "Error al leer datos del servidor", Toast.LENGTH_SHORT).show()
                        btnIngresarDatos.visibility = View.GONE
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                    btnIngresarDatos.visibility = View.GONE
                }
            )

            requestQueue.add(request)
        } else {
            Toast.makeText(this, "No se encontró nombre de usuario", Toast.LENGTH_SHORT).show()
            btnIngresarDatos.visibility = View.GONE
        }
    }

    private fun obtenerMensajes(nombre: String) {
        val url =
            "http://192.168.100.130/android_mysql_proyectExpotecnica/obtener_mensajes.php?destinatario=${
                java.net.URLEncoder.encode(
                    nombre,
                    "UTF-8"
                )
            }"
        val queue = Volley.newRequestQueue(this)

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    listaMensajes.clear()
                    val jsonArray = org.json.JSONArray(response)

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val mensaje = Mensaje(
                            id = obj.getInt("id").toString(),
                            remitente = obj.getString("remitente"),
                            mensaje = obj.getString("mensaje"),
                            fecha = obj.getString("fecha")
                        )
                        listaMensajes.add(mensaje)
                    }

                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Error al procesar mensajes: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error al obtener mensajes: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            })

        queue.add(request)
    }
}
