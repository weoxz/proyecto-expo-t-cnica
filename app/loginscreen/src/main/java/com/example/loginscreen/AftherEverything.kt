package com.example.loginscreen

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AftherEverything : AppCompatActivity() {

    // Declaración de variables para las vistas
    private lateinit var btnMsj: Button
    private lateinit var btnDatosEstudiante: Button
    private lateinit var btnEnviarMensaje: Button
    private lateinit var contenedorMensajes: FrameLayout
    private lateinit var contenedorDatos: FrameLayout
    private lateinit var contenedorEnviar: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_afther_everything)

        // Inicializar vistas
        initViews()

        // Configurar listeners
        setupButtonListeners()

        // Mostrar sección de mensajes por defecto
        mostrarSeccion(1)
    }

    private fun initViews() {
        btnMsj = findViewById(R.id.btnMsj)
        btnDatosEstudiante = findViewById(R.id.btnDatosEstudiante)
        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje)
        contenedorMensajes = findViewById(R.id.contenedorMensajes)
        contenedorDatos = findViewById(R.id.contenedorDatos)
        contenedorEnviar = findViewById(R.id.contenedorEnviar)
    }

    private fun setupButtonListeners() {
        btnMsj.setOnClickListener { mostrarSeccion(1) }
        btnDatosEstudiante.setOnClickListener { mostrarSeccion(2) }
        btnEnviarMensaje.setOnClickListener { mostrarSeccion(3) }
    }

    private fun mostrarSeccion(seccion: Int) {
        // Resetear todos los botones a gris
        resetButtons()

        when (seccion) {
            1 -> {
                btnMsj.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
                contenedorMensajes.visibility = View.VISIBLE
            }
            2 -> {
                btnDatosEstudiante.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
                contenedorDatos.visibility = View.VISIBLE
            }
            3 -> {
                btnEnviarMensaje.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
                contenedorEnviar.visibility = View.VISIBLE
            }
        }
    }

    private fun resetButtons() {
        // Poner todos los botones en gris
        listOf(btnMsj, btnDatosEstudiante, btnEnviarMensaje).forEach {
            it.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray)
        }

        // Ocultar todos los contenedores excepto el que se activará
        listOf(contenedorMensajes, contenedorDatos, contenedorEnviar).forEach {
            it.visibility = View.GONE
        }
    }
}