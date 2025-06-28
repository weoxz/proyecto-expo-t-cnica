package com.example.loginscreen

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfesorActividad : AppCompatActivity() {

    private var nombreEstudianteBuscado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profesor_actividad)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSeccionesACargo = findViewById<Button>(R.id.btnSeccionesACargo)
        val btnBuscarEstudiante = findViewById<Button>(R.id.btnBuscarEstudiante)
        val btnEnviarPadre = findViewById<Button>(R.id.btnEnviarPadre)
        val btnEnviarHijo = findViewById<Button>(R.id.btnEnviarHijo)
        val btnDatosEstudiante = findViewById<Button>(R.id.btnDatosEstudiante)
        val tvDatosProfesor = findViewById<TextView>(R.id.tvDatosProfesor)

        btnSeccionesACargo.setOnClickListener {
            cambiarColorBoton(it as Button)
            Toast.makeText(this, "Secciones a cargo: 7-1, 8-2, 9-3", Toast.LENGTH_SHORT).show()
        }

        btnBuscarEstudiante.setOnClickListener {
            cambiarColorBoton(it as Button)

            val input = EditText(this)
            input.hint = "Escriba el nombre del estudiante"

            AlertDialog.Builder(this)
                .setTitle("Buscar estudiante")
                .setView(input)
                .setPositiveButton("Buscar") { _, _ ->
                    val nombre = input.text.toString().trim()
                    if (nombre.isNotEmpty()) {
                        nombreEstudianteBuscado = nombre
                        Toast.makeText(this, "Estudiante '$nombre' encontrado.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Nombre vacío. Intente de nuevo.", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnEnviarPadre.setOnClickListener {
            cambiarColorBoton(it as Button)
            if (nombreEstudianteBuscado.isNullOrBlank()) {
                Toast.makeText(this, "Primero busque al estudiante", Toast.LENGTH_SHORT).show()
            } else {
                mostrarDialogoMensaje("padre", nombreEstudianteBuscado!!)
            }
        }

        btnEnviarHijo.setOnClickListener {
            cambiarColorBoton(it as Button)
            if (nombreEstudianteBuscado.isNullOrBlank()) {
                Toast.makeText(this, "Primero busque al estudiante", Toast.LENGTH_SHORT).show()
            } else {
                mostrarDialogoMensaje("estudiante", nombreEstudianteBuscado!!)
            }
        }

        btnDatosEstudiante.setOnClickListener {
            cambiarColorBoton(it as Button)
            tvDatosProfesor.visibility = View.VISIBLE
        }
    }

    private fun cambiarColorBoton(boton: Button) {
        boton.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_gray))
    }

    private fun mostrarDialogoMensaje(destino: String, nombreEstudiante: String) {
        val editText = EditText(this)
        editText.hint = "Escriba el mensaje para el $destino"

        AlertDialog.Builder(this)
            .setTitle("Mensaje al $destino de $nombreEstudiante")
            .setView(editText)
            .setPositiveButton("Enviar") { _, _ ->
                val mensaje = editText.text.toString()
                if (mensaje.isNotBlank()) {
                    Toast.makeText(this, "Mensaje enviado al $destino: $mensaje", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "El mensaje está vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
