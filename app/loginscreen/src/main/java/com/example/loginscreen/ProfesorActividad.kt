package com.example.loginscreen

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.EditText

class ProfesorActividad : AppCompatActivity() {

    private lateinit var contenedorMensajes: FrameLayout
    private lateinit var contenedorEnviar: FrameLayout


    private lateinit var remitnete: EditText
    private lateinit var destinatario: EditText
    private lateinit var mensaje: EditText

    private lateinit var recyclerView: RecyclerView
    lateinit var listaMensajes: MutableList<Mensaje>
    private lateinit var adapter: MensajeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profesor_actividad)

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
        contenedorEnviar = findViewById(R.id.contenedorEnviar)

        val btnMsj = findViewById<Button>(R.id.btnMsj)
        val btnEnviarMensaje = findViewById<Button>(R.id.btnEnviarMensaje)

        remitnete = findViewById(R.id.remitnete)
        destinatario = findViewById(R.id.destinatario)
        mensaje = findViewById(R.id.mensaje)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        btnMsj.setOnClickListener {
            cambiarColorBoton(it as Button)
            mostrarContenedor(contenedorMensajes)
            val nombreUsuario = obtenerNombreUsuario()
            if (nombreUsuario != null) {
                obtenerMensajes(nombreUsuario)
            } else {
                Toast.makeText(this, "Nombre de usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }

        btnEnviarMensaje.setOnClickListener {
            cambiarColorBoton(it as Button)
            mostrarContenedor(contenedorEnviar)
        }
    }
    private fun obtenerFechaActual(): String {
        val formato = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
        return formato.format(java.util.Date())
    }

    private fun obtenerIdUsuario(): String? {
        val sharedPref: SharedPreferences = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        return sharedPref.getString("id_usuario", null)
    }


    private fun cambiarColorBoton(boton: Button) {
        boton.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_gray))
    }

    private fun obtenerNombreUsuario(): String? {
        val sharedPref = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        return sharedPref.getString("nombre_usuario", null)
    }
    private fun mostrarContenedor(contenedorMostrado: FrameLayout) {
        contenedorMensajes.visibility = View.GONE
        contenedorEnviar.visibility = View.GONE
        contenedorMostrado.visibility = View.VISIBLE
    }

    fun ClickSendMesajes(view: View) {
        val url = "http://192.168.100.130/android_mysql_proyectExpotecnica/enviar_mensaje.php"
        val queue = Volley.newRequestQueue(view.context)

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

                Toast.makeText(view.context, "Datos ingresados: $response", Toast.LENGTH_SHORT)
                    .show()

            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    view.context,
                    "Error: ${error.message ?: "Error desconocido"}",
                    Toast.LENGTH_SHORT
                ).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val sharedPrefs = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
                val rolUsuario = sharedPrefs.getString("rol_usuario", "") ?: ""
                return mutableMapOf(
                    "remitente" to remitnete.text.toString(),
                    "destinatario" to destinatario.text.toString(),
                    "mensaje" to mensaje.text.toString(),
                    "rol" to rolUsuario
                )
            }
        }
        queue.add(resultadoPost)
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
