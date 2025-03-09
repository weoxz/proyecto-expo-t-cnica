package com.example.loginscreen

import android.content.Intent // Importa la clase Intent
import android.os.Bundle
import android.widget.Button // Importa la clase Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class loginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_screen) // Asegúrate que este es el layout correcto
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Añade el código para el botón aquí:
        val nextScreenButton: Button = findViewById(R.id.NextScreen)
        nextScreenButton.setOnClickListener {
            val intent = Intent(this,SecondScreen ::class.java)
            startActivity(intent)
            //Si quieres que la pantalla loginScreen se cierre al cambiar de activity
            //agregas la siguiente linea de codigo.
            //finish()
        }
    }
}