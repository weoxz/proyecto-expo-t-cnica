package com.example.loginscreen

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONArray

class AdminActivity : AppCompatActivity() {

    private lateinit var contenedorMensajes: FrameLayout
    private lateinit var contenedorEnviar: FrameLayout
    private lateinit var linearLayoutAprobar: LinearLayout


    private lateinit var remitnete: EditText
    private lateinit var destinatario: EditText
    private lateinit var mensaje: EditText

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MensajePendienteAdapter
    private val listaMensajes = mutableListOf<Mensaje>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        recyclerView = findViewById(R.id.recyclerMensajes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MensajePendienteAdapter(listaMensajes, this)
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
               obtenerMensajesPendientes()
            } else {
                Toast.makeText(this, "Nombre de usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }

        btnEnviarMensaje.setOnClickListener {
            cambiarColorBoton(it as Button)
            mostrarContenedor(contenedorEnviar)
        }
        obtenerMensajesPendientes()
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
            Method.POST, url,
            Response.Listener<String> { response ->
                val sharedPrefs = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
                val rolUsuario = sharedPrefs.getString("rol_usuario", "") ?: ""

                val remitenteTxt = remitnete.text.toString()
                val destinatarioTxt = destinatario.text.toString()
                val mensajeTxt = mensaje.text.toString()
                "rol" to rolUsuario

                val fechaActual = obtenerFechaActual()
                Mensaje(remitenteTxt, destinatarioTxt, mensajeTxt, fechaActual)

// Añadir a la lista y notificar al adapter

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


    private fun obtenerMensajesPendientes() {
        val url = "http://192.168.100.130/android_mysql_proyectExpotecnica/obtener_mensajes_pendientes.php"
        val request = StringRequest(Request.Method.GET, url,
            { response ->
                val jsonArray = JSONArray(response)
                listaMensajes.clear()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    listaMensajes.add(
                        Mensaje(
                            obj.getString("id"),
                            obj.getString("remitente"),
                            obj.getString("destinatario"),
                            obj.getString("mensaje")
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}
