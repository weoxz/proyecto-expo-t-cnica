package com.example.loginscreen

import android.content.ContextParams
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class loginScreen : AppCompatActivity() {
    var txtCedula:EditText?=null
    var txtCodigo:EditText?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        txtCedula=findViewById(R.id.txtCedula)
        txtCodigo=findViewById(R.id.txtCodigo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun clickButtoningresar(view:View){
        val url="http://192.168.100.130/android_mysql_proyectExpotecnica/insertar_Estudiantes.php"
        val queue=Volley.newRequestQueue(this)
        var resultadoPost = object : StringRequest(Request.Method.POST,url,
            Response.Listener<String> { response ->
                Toast.makeText(this,"datos ingresados", Toast.LENGTH_SHORT).show()
            },Response.ErrorListener { error ->
                Toast.makeText(this,"Error $error", Toast.LENGTH_SHORT).show()

            }){
          override fun getParams(): HashMap<String, String> {
              val parametros = HashMap<String, String>()
              parametros.put("Numero-de-Cedula", txtCedula?.text.toString())
              parametros.put("codigo-de-entrada-estudiante", txtCodigo?.text.toString())
              return parametros
          }
        }
        queue.add(resultadoPost)
    }
}