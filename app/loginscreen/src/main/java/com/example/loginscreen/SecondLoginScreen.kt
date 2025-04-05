package com.example.loginscreen

import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import android.widget.EditText

class SecondLoginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second_login_screen)

        // Configuración del EdgeToEdge (como tenías originalmente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Implementación de la selección de rol
        val radioGroupRol = findViewById<RadioGroup>(R.id.radioGroupRol)
        val btnNext = findViewById<MaterialButton>(R.id.NextScreenUserr)
        val txtCedula = findViewById<EditText>(R.id.txtCedula)
        val txtCodigo = findViewById<EditText>(R.id.txtCodigo)
        val txtSeccion = findViewById<EditText>(R.id.TxtNombre)

        btnNext.setOnClickListener {
            // Obtener el rol seleccionado
            val selectedRole = when (radioGroupRol.checkedRadioButtonId) {
                R.id.radioEncargado -> "Encargado"
                R.id.radioEstudiante -> "Estudiante"
                else -> "No seleccionado"
            }

            // Obtener los demás valores
            val usuario = txtCedula.text.toString()
            val contrasena = txtCodigo.text.toString()
            val seccion = txtSeccion.text.toString()

            // Validación básica de campos
            if (usuario.isEmpty() || contrasena.isEmpty() || seccion.isEmpty()) {
                Log.e("LoginError", "Todos los campos son obligatorios")
                // Aquí podrías mostrar un Toast o Snackbar al usuario
                return@setOnClickListener
            }

            // Log de depuración (puedes eliminarlo en producción)
            Log.d("LoginData", """
                Usuario: $usuario
                Rol: $selectedRole
                Sección: $seccion
            """.trimIndent())

            // Aquí iría la lógica para procesar el login
            // Por ejemplo, navegar a otra actividad o hacer una petición HTTP
            // startActivity(Intent(this, NextActivity::class.java))
        }
    }
}