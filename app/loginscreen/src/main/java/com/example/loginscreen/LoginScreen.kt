package com.example.loginscreen

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import org.json.JSONObject

class LoginScreen : AppCompatActivity() {

    private lateinit var txtUsuario: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnIrRegistro: MaterialButton

    private val url = "http://192.168.100.130/android_mysql_proyectExpotecnica/login.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        txtUsuario = findViewById(R.id.txtCedula)
        txtPassword = findViewById(R.id.txtCodigo)
        btnLogin = findViewById(R.id.button)
        btnIrRegistro = findViewById(R.id.NextScreenButton)

        btnLogin.setOnClickListener {
            val usuario = txtUsuario.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUsuario(usuario, password)
        }

        btnIrRegistro.setOnClickListener {
            startActivity(Intent(this, ProfesorActividad::class.java))
        }
    }

    private fun loginUsuario(usuario: String, password: String) {
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val json = JSONObject(response)
                    if (json.getBoolean("success")) {
                        val rol = json.getString("rol")
                        when (rol) {
                            "estudiante" -> startActivity(Intent(this, AftherEverything::class.java))
                            //"padre456" -> startActivity(Intent(this, PadreActivity::class.java))
                            //"docente789" -> startActivity(Intent(this, DocenteActivity::class.java))
                            //"admin000" -> startActivity(Intent(this, AdminActivity::class.java))
                            else -> Toast.makeText(this, "Rol desconocido: $rol", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, json.getString("error"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error en la conexión con el servidor", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("nombre" to usuario, "contraseña" to password)
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }
}
