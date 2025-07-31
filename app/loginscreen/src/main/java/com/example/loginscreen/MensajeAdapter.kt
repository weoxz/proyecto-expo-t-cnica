package com.example.loginscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MensajeAdapter(
    private val listaMensajes: MutableList<Mensaje>,
    private val onMensajeClick: (Mensaje) -> Unit
) : RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder>() {

    class MensajeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fechaTextView: TextView = view.findViewById(R.id.txtFecha)
        val remitenteTextView: TextView = view.findViewById(R.id.txtRemitente)
        val mensajeTextView: TextView = view.findViewById(R.id.txtMensaje)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mensaje, parent, false)
        return MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        val mensaje = listaMensajes[position]
        holder.fechaTextView.text = mensaje.fecha
        holder.remitenteTextView.text = "De: ${mensaje.remitente}"
        holder.mensajeTextView.text = mensaje.mensaje

        holder.itemView.setOnClickListener {
            onMensajeClick(mensaje)
        }
    }

    override fun getItemCount() = listaMensajes.size
}
