@file:Suppress("UnusedImport")

package com.example.loginscreen

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.content.SharedPreferences
import android.widget.EditText
import com.android.volley.Response
import org.json.JSONException
import androidx.recyclerview.widget.RecyclerView


class AftherEverything : AppCompatActivity() {

    private lateinit var contenedorMensajes: FrameLayout
    private lateinit var contenedorDatos: FrameLayout
    private lateinit var contenedorEnviar: FrameLayout

    private lateinit var remitnete: EditText
    private lateinit var destinatario: EditText
    private lateinit var mensaje: EditText

    private lateinit var recyclerView: RecyclerView
    lateinit var listaMensajes: MutableList<Mensaje>
    private lateinit var adapter: MensajeAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_afther_everything)

        listaMensajes = mutableListOf()



        adapter = MensajeAdapter(listaMensajes) { mensaje ->
            // Autocompletar remitente y destinatario para responder
            remitnete.setText(obtenerNombreUsuario())
            destinatario.setText(mensaje.remitente)

            // Mostrar formulario de envío directo (puedes ocultar los otros si prefieres)
            mostrarContenedor(contenedorEnviar)

            // O puedes solo enfocarte en edtMensajeRespuesta si quieres mantenerlo en el mismo frame

        }


        recyclerView = findViewById(R.id.recyclerMensajes)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.adapter = adapter


        contenedorMensajes = findViewById(R.id.contenedorMensajes)
        contenedorDatos = findViewById(R.id.contenedorDatos)
        contenedorEnviar = findViewById(R.id.contenedorEnviar)

        val btnMsj = findViewById<Button>(R.id.btnMsj)
        val btnDatosEstudiante = findViewById<Button>(R.id.btnDatosEstudiante)
        val btnEnviarMensaje = findViewById<Button>(R.id.btnEnviarMensaje)



        remitnete = findViewById(R.id.remitnete)
        destinatario = findViewById(R.id.destinatario)
        mensaje = findViewById(R.id.mensaje)

        btnMsj.setOnClickListener {
            mostrarContenedor(contenedorMensajes)
            val nombreUsuario = obtenerNombreUsuario()
            if (nombreUsuario != null) {
                obtenerMensajes(nombreUsuario)
            } else {
                Toast.makeText(this, "Nombre de usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
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

    fun ClickSendMesajes(view: View) {
        val url = "http://192.168.100.130/android_mysql_proyectExpotecnica/enviar_mensaje.php"
        val queue = Volley.newRequestQueue(view.context)

        val sharedPref = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        val tipoUsuario = sharedPref.getString("rol_usuario", "") ?: ""

        val resultadoPost = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                val remitenteTxt = remitnete.text.toString()
                val destinatarioTxt = destinatario.text.toString()
                val mensajeTxt = mensaje.text.toString()

                val fechaActual = obtenerFechaActual()
                val nuevoMensaje = Mensaje(remitenteTxt, destinatarioTxt, mensajeTxt, fechaActual)

// Añadir a la lista y notificar al adapter
                listaMensajes.add(nuevoMensaje)
                adapter.notifyItemInserted(listaMensajes.size - 1)
                recyclerView.scrollToPosition(listaMensajes.size - 1)

// Limpiar el campo de texto
                mensaje.text.clear()


                Toast.makeText(view.context, "Datos ingresados: $response", Toast.LENGTH_SHORT).show()

            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    view.context,
                    "Error: ${error.message ?: "Error desconocido"}",
                    Toast.LENGTH_SHORT
                ).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf(
                    "remitente" to remitnete.text.toString(),
                    "destinatario" to destinatario.text.toString(),
                    "mensaje" to mensaje.text.toString(),
                    "rol" to tipoUsuario
                )
            }
        }
        // Validación para usuarios tipo padre
        if (tipoUsuario == "padre") {
            val destinatarioNombre = destinatario.text.toString().trim()

            // Verifica si es una respuesta a un docente o al remitente original
            val esRespuesta = listaMensajes.any {
                it.remitente == destinatarioNombre || destinatarioNombre.lowercase().contains("docente")
            }

            if (!esRespuesta) {
                Toast.makeText(view.context, "Solo puedes responder mensajes o escribir a docentes.", Toast.LENGTH_LONG).show()
                return
            }
        }
        queue.add(resultadoPost)
    }

    private fun obtenerNombreUsuario(): String? {
        val sharedPref = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        return sharedPref.getString("nombre_usuario", null)
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

    private fun obtenerFechaActual(): String {
        val formato = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
        return formato.format(java.util.Date())
    }

    private fun pruebaAgregarMensaje() {
        val mensajePrueba = Mensaje(
            id = "0",
            remitente = "UsuarioPrueba",
            mensaje = "Mensaje de prueba",
            fecha = "26/07/2025 15:00:00"
        )
        listaMensajes.add(mensajePrueba)
        adapter.notifyItemInserted(listaMensajes.size - 1)
    }


}