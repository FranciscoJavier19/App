package com.example.a113_4b_25092025

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import java.text.SimpleDateFormat
import java.util.Locale

class VerNoticiaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_noticia)

        val verImagen = findViewById<ImageView>(R.id.ver_imagen)
        val verTitulo = findViewById<TextView>(R.id.ver_titulo)
        val verResumen = findViewById<TextView>(R.id.ver_resumen)
        val verContenido = findViewById<TextView>(R.id.ver_contenido)
        val verAutor = findViewById<TextView>(R.id.ver_autor)
        val verFecha = findViewById<TextView>(R.id.ver_fecha)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        val noticia = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("noticia", Noticia::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("noticia") as? Noticia
        }

        if (noticia != null) {
            verImagen.load(noticia.imagenUrl) {
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_foreground)
            }

            verTitulo.text = noticia.titulo
            verResumen.text = noticia.resumen
            verContenido.text = noticia.contenido
            verAutor.text = "Por: ${noticia.autor}"
            
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            // ¡CAMBIO! Como 'fecha' ya es un Date, no necesitamos .toDate()
            verFecha.text = noticia.fecha?.let { sdf.format(it) } ?: "Sin fecha"
        } else {
            finish()
        }

        btnVolver.setOnClickListener { finish() }
    }
}
