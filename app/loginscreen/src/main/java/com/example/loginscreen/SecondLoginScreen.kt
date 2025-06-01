package com.example.loginscreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class SecondLoginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second_login_screen)

        // Aplicar padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a vistas
        val radioGroupRol = findViewById<RadioGroup>(R.id.radioGroupRol)
        val txtCedula = findViewById<EditText>(R.id.txtCedula)
        val txtCodigo = findViewById<EditText>(R.id.txtCodigo)
        val txtSeccion = findViewById<EditText>(R.id.TxtNombre)
        val btnNext = findViewById<MaterialButton>(R.id.NextScreenUserr)

        btnNext.setOnClickListener {
            val usuario = txtCedula.text.toString().trim()
            val contrasena = txtCodigo.text.toString().trim()
            val seccion = txtSeccion.text.toString().trim()

            // Validar campos vacíos
            if (usuario.isEmpty() || contrasena.isEmpty() || seccion.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtener rol seleccionado
            val selectedRole = when (radioGroupRol.checkedRadioButtonId) {
                R.id.radioEncargado -> "Encargado"
                R.id.radioEstudiante -> "Estudiante"
                else -> null
            }

            if (selectedRole == null) {
                Toast.makeText(this, "Por favor selecciona un rol", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Imprimir datos para depuración
            Log.d("LoginData", """
                Usuario: $usuario
                Contraseña: $contrasena
                Sección: $seccion
                Rol: $selectedRole
            """.trimIndent())

            // Ir a la siguiente pantalla
            val intent = Intent(this, ScreenAftherLog::class.java)
            startActivity(intent)
        }
    }
}
