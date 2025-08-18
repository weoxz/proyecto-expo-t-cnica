package com.example.loginscreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import org.json.JSONObject

class SecondSignScreen : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_login_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtUsuario = findViewById<EditText>(R.id.txtUsuario)
        val txtContraseña = findViewById<EditText>(R.id.txtContraseña)
        val txtClave = findViewById<EditText>(R.id.txtCodigoEntra)
        val btnNext = findViewById<MaterialButton>(R.id.NextScreenUserr)

        requestQueue = Volley.newRequestQueue(this)

        btnNext.setOnClickListener {
            val usuario = txtUsuario.text.toString().trim()
            val contraseña = txtContraseña.text.toString().trim()
            val clave = txtClave.text.toString().trim()

            if (usuario.isEmpty() || contraseña.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = ApiConfig.BASE_URL + "registroLog.php"

            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    try {
                        val json = JSONObject(response)
                        if (json.getBoolean("success")) {
                            val rol = json.optString("rol", "desconocido")
                            Toast.makeText(this, "Usuario registrado como $rol", Toast.LENGTH_LONG).show()

                            val sharedPref = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("nombre_usuario", usuario)
                                apply()
                            }

                            when (rol) {
                                "estudiante" -> startActivity(Intent(this, ProfileOfStudents::class.java))
                                "padre" -> startActivity(Intent(this, AftherEverything::class.java))
                                "docente" -> startActivity(Intent(this, ProfesorActividad::class.java))
                                "admin" -> startActivity(Intent(this, AdminActivity::class.java))
                                // Agrega otros roles si tienes
                                else -> Toast.makeText(this, "Rol desconocido: $rol", Toast.LENGTH_SHORT).show()
                            }

                            // Aquí puedes guardar el rol o enviarlo a otra pantalla
                            // Por ejemplo, podrías usar SharedPreferences:
                            val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
                            prefs.edit().putString("rol", rol).apply()

                        } else {
                            val error = json.optString("error", "Error desconocido")
                            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error procesando respuesta", Toast.LENGTH_SHORT).show()
                        Log.e("VolleyResponse", "Error: ${e.message}")
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["nombre"] = usuario
                    params["password"] = contraseña
                    params["clave"] = clave
                    return params
                }
            }

            requestQueue.add(stringRequest)
        }
    }
}
