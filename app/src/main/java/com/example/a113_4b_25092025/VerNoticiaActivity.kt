package com.example.a113_4b_25092025

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class VerNoticiaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // El código carga tu archivo de diseño
        setContentView(R.layout.activity_ver_noticia)

        // Conectamos el código a los componentes de tu XML usando sus IDs
        val verTitulo = findViewById<TextView>(R.id.ver_titulo)
        val verResumen = findViewById<TextView>(R.id.ver_resumen)
        val verContenido = findViewById<TextView>(R.id.ver_contenido)
        val verAutor = findViewById<TextView>(R.id.ver_autor)
        val verFecha = findViewById<TextView>(R.id.ver_fecha)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Obtenemos el objeto Noticia que nos pasaron desde la lista
        val noticia = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("noticia", Noticia::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("noticia") as? Noticia
        }

        // Si la noticia existe, rellenamos los campos
        if (noticia != null) {
            verTitulo.text = noticia.titulo
            verResumen.text = noticia.resumen
            verContenido.text = noticia.contenido
            verAutor.text = "Por: ${noticia.autor}"
            
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            verFecha.text = noticia.fecha?.toDate()?.let { sdf.format(it) } ?: "Sin fecha"
        } else {
            finish()
        }

        // Funcionalidad del botón de volver
        btnVolver.setOnClickListener { finish() }
    }
}
