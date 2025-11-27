package com.example.a113_4b_25092025

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import java.text.SimpleDateFormat
import java.util.Locale

class NoticiasAdapter(
    private val context: Context,
    private val noticias: MutableList<Noticia>,
    private val listener: OnNoticiaInteractionListener // Eliminamos el userId de aquí
) : RecyclerView.Adapter<NoticiasAdapter.NoticiaViewHolder>() {

    interface OnNoticiaInteractionListener {
        fun onNoticiaEdit(noticiaId: String)
        fun onNoticiaDelete(noticiaId: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_noticia, parent, false)
        return NoticiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        // Ya no pasamos el userId aquí
        holder.bind(noticias[position], listener)
    }

    override fun getItemCount(): Int = noticias.size

    fun removerNoticia(position: Int) {
        noticias.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, noticias.size)
    }

    class NoticiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImagen: ImageView = itemView.findViewById(R.id.item_imagen)
        private val txtTitulo: TextView = itemView.findViewById(R.id.item_titulo)
        private val txtResumen: TextView = itemView.findViewById(R.id.item_resumen)
        private val txtFecha: TextView = itemView.findViewById(R.id.item_fecha)
        private val btnEditar: ImageButton = itemView.findViewById(R.id.btn_item_editar)
        private val btnBorrar: ImageButton = itemView.findViewById(R.id.btn_item_borrar)
        private val clickableArea: LinearLayout = itemView.findViewById(R.id.clickable_area)

        // Eliminamos el userId de la firma del método
        fun bind(noticia: Noticia, listener: OnNoticiaInteractionListener) {
            txtTitulo.text = noticia.titulo
            txtResumen.text = noticia.resumen

            itemImagen.load(noticia.imagenUrl) {
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_foreground)
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            txtFecha.text = noticia.fecha?.let { sdf.format(it) } ?: "Sin fecha"

            val context = itemView.context

            clickableArea.setOnClickListener {
                val intent = Intent(context, VerNoticiaActivity::class.java).apply {
                    putExtra("noticia", noticia)
                }
                context.startActivity(intent)
            }

            btnEditar.setOnClickListener {
                noticia.id?.let { listener.onNoticiaEdit(it) }
            }

            btnBorrar.setOnClickListener {
                noticia.id?.let { listener.onNoticiaDelete(it, adapterPosition) }
            }
        }
    }
}
