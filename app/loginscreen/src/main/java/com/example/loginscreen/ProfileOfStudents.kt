package com.example.loginscreen

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ProfileOfStudents : AppCompatActivity() {

    private lateinit var btnDatosEstudiante: Button
    private lateinit var btnDatosDiarios: Button
    private lateinit var contenedorDatos: FrameLayout
    private lateinit var contenedorDatosDiarios: FrameLayout  // Renombrado correctamente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_of_students)

        btnDatosEstudiante = findViewById(R.id.btnDatosEstudiante)
        btnDatosDiarios = findViewById(R.id.btnDatosDiarios)
        contenedorDatos = findViewById(R.id.contenedorDatos)
        contenedorDatosDiarios = findViewById(R.id.contenedorDatosDiarios)  // ID correcto

        val gris = ContextCompat.getColor(this, R.color.gray)
        val negro = Color.BLACK

        btnDatosEstudiante.setOnClickListener {
            // Mostrar datos del estudiante
            contenedorDatos.visibility = View.VISIBLE
            contenedorDatosDiarios.visibility = View.GONE

            // Colores de los botones
            btnDatosEstudiante.setBackgroundColor(negro)
            btnDatosDiarios.setBackgroundColor(gris)
        }

        btnDatosDiarios.setOnClickListener {
            // Mostrar datos diarios
            contenedorDatosDiarios.visibility = View.VISIBLE
            contenedorDatos.visibility = View.GONE

            // Colores de los botones
            btnDatosDiarios.setBackgroundColor(negro)
            btnDatosEstudiante.setBackgroundColor(gris)
        }
    }
}
