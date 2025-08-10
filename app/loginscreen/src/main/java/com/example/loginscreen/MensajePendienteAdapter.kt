package com.example.loginscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MensajePendienteAdapter(
    private val lista: List<Mensaje>,
    private val context: Context
) : RecyclerView.Adapter<MensajePendienteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val remitente: TextView = view.findViewById(R.id.txtRemitente)
        val mensaje: TextView = view.findViewById(R.id.txtMensaje)
        val aprobar: Button = view.findViewById(R.id.btnAprobar)
        val rechazar: Button = view.findViewById(R.id.btnRechazar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflamos el layout del ítem, NO el de la actividad
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_validar_mensajes, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mensaje = lista[position]
        holder.remitente.text = "De: ${mensaje.remitente}"
        holder.mensaje.text = mensaje.mensaje

        holder.aprobar.setOnClickListener {
            val url = "http://192.168.100.130/android_mysql_proyectExpotecnica/aprobar_mensaje.php"
            val request = object : StringRequest(Method.POST, url,
                { Toast.makeText(context, "Mensaje aprobado", Toast.LENGTH_SHORT).show() },
                { Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show() }) {
                override fun getParams() = hashMapOf("id" to mensaje.id.toString())
            }
            Volley.newRequestQueue(context).add(request)
        }

        holder.rechazar.setOnClickListener {
            val url = "http://192.168.100.130/android_mysql_proyectExpotecnica/eliminar_mensaje.php"
            val request = object : StringRequest(Method.POST, url,
                { Toast.makeText(context, "Mensaje eliminado", Toast.LENGTH_SHORT).show() },
                { Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show() }) {
                override fun getParams() = hashMapOf("id" to mensaje.id.toString())
            }
            Volley.newRequestQueue(context).add(request)
        }
    }

    override fun getItemCount(): Int = lista.size
}
