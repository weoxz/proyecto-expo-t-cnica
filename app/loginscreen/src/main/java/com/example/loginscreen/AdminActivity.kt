package com.example.loginscreen

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AdminActivity : AppCompatActivity() {

    private var nombreEstudianteBuscado: String? = null
    private val profesFaltantes = mutableListOf<String>()  // Para registrar profesores ausentes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val btnSeccionesACargo = findViewById<Button>(R.id.btnProfesAucentadosAvizo)
        val btnBuscarEstudiante = findViewById<Button>(R.id.btnBuscarEstudiante)
        val btnEnviarPadre = findViewById<Button>(R.id.btnEnviarPadre)
        val btnMensajesPorAprovar = findViewById<Button>(R.id.btnMensajesPorAprovar)
        val btnDatosEstudiante = findViewById<Button>(R.id.btnDatosEstudiante)
        val tvDatosProfesor = findViewById<TextView>(R.id.tvDatosProfesor)

        // Profesores ausentes (registro tipo mensaje)
        btnSeccionesACargo.setOnClickListener {
            cambiarColorBoton(it as Button)

            val input = EditText(this)
            input.hint = "Ej: Prof. Juan - incapacidad"

            AlertDialog.Builder(this)
                .setTitle("Registrar profesor ausente")
                .setView(input)
                .setPositiveButton("Guardar") { _, _ ->
                    val nombre = input.text.toString().trim()
                    if (nombre.isNotEmpty()) {
                        profesFaltantes.add(nombre)
                        Toast.makeText(this, "Registrado: $nombre", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Campo vacío", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNeutralButton("Ver lista") { _, _ ->
                    if (profesFaltantes.isEmpty()) {
                        Toast.makeText(this, "No hay profesores registrados como ausentes.", Toast.LENGTH_SHORT).show()
                    } else {
                        mostrarListaProfesFaltantes(profesFaltantes)
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Buscar estudiante
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

        // Enviar mensaje a padre
        btnEnviarPadre.setOnClickListener {
            cambiarColorBoton(it as Button)
            if (nombreEstudianteBuscado.isNullOrBlank()) {
                Toast.makeText(this, "Primero busque al estudiante", Toast.LENGTH_SHORT).show()
            } else {
                mostrarDialogoMensaje("padre", nombreEstudianteBuscado!!)
            }
        }

        // Ver/aprobar mensajes
        btnMensajesPorAprovar.setOnClickListener {
            cambiarColorBoton(it as Button)
            mostrarMensajesPendientes()
        }

        // Mostrar datos del profesor
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

    private fun mostrarMensajesPendientes() {
        val mensajes = listOf(
            "Justificación por cita médica.",
            "Permiso por viaje familiar.",
            "Cambio de grupo por rendimiento."
        )

        val opciones = mensajes.mapIndexed { index, mensaje ->
            "Mensaje ${index + 1}: $mensaje"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Mensajes por aprobar")
            .setItems(opciones) { _, which ->
                val mensajeSeleccionado = mensajes[which]
                mostrarOpcionesDeAprobacion(mensajeSeleccionado)
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun mostrarOpcionesDeAprobacion(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Aprobar o rechazar")
            .setMessage(mensaje)
            .setPositiveButton("Aprobar") { _, _ ->
                Toast.makeText(this, "Mensaje aprobado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Rechazar") { _, _ ->
                Toast.makeText(this, "Mensaje rechazado", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Cancelar", null)
            .show()
    }

    private fun mostrarListaProfesFaltantes(lista: List<String>) {
        val mensaje = lista.joinToString("\n")
        AlertDialog.Builder(this)
            .setTitle("Profesores ausentes registrados")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
