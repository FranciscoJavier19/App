package com.example.a113_4b_25092025

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class NoticiasAdapter(private val noticias: List<Noticia>) : RecyclerView.Adapter<NoticiasAdapter.NoticiaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_noticia, parent, false)
        return NoticiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        holder.bind(noticias[position])
    }

    override fun getItemCount(): Int = noticias.size

    class NoticiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Conectamos el código a los componentes del XML del item
        private val txtTitulo: TextView = itemView.findViewById(R.id.item_titulo)
        private val txtResumen: TextView = itemView.findViewById(R.id.item_resumen)
        private val txtFecha: TextView = itemView.findViewById(R.id.item_fecha)

        fun bind(noticia: Noticia) {
            // Asignamos los datos a las vistas
            txtTitulo.text = noticia.titulo
            txtResumen.text = noticia.resumen

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            txtFecha.text = noticia.fecha?.toDate()?.let { sdf.format(it) } ?: "Sin fecha"

            // Funcionalidad de clic para ir al detalle
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, VerNoticiaActivity::class.java)
                intent.putExtra("noticia", noticia)
                itemView.context.startActivity(intent)
            }
        }
    }
}
