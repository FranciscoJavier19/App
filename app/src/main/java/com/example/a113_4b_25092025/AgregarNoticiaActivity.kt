package com.example.a113_4b_25092025

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AgregarNoticiaActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // El código carga tu archivo de diseño
        setContentView(R.layout.activity_agregar_noticia)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Conectamos el código a los componentes de tu XML usando sus IDs
        val inputTitulo = findViewById<EditText>(R.id.inputTitulo)
        val inputResumen = findViewById<EditText>(R.id.inputResumen)
        val inputContenido = findViewById<EditText>(R.id.inputContenido)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        // Funcionalidad del botón de guardar
        btnGuardar.setOnClickListener {
            val titulo = inputTitulo.text.toString().trim()
            val resumen = inputResumen.text.toString().trim()
            val contenido = inputContenido.text.toString().trim()

            if (titulo.isBlank() || resumen.isBlank() || contenido.isBlank()) {
                Toast.makeText(this, "⚠️ Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            getAuthorNameAndSave(titulo, resumen, contenido)
        }
    }

    private fun getAuthorNameAndSave(titulo: String, resumen: String, contenido: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "❌ Error: No se pudo identificar al usuario.", Toast.LENGTH_LONG).show()
            return
        }

        // Lógica para obtener el nombre del autor y guardar la noticia
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                val autor = document.getString("nombre") ?: "Anónimo"
                guardarNoticia(titulo, resumen, contenido, autor)
            }
            .addOnFailureListener {
                guardarNoticia(titulo, resumen, contenido, "Anónimo")
            }
    }

    private fun guardarNoticia(titulo: String, resumen: String, contenido: String, autor: String) {
        val nuevaNoticia = hashMapOf(
            "titulo" to titulo,
            "resumen" to resumen,
            "contenido" to contenido,
            "autor" to autor,
            "fecha" to Timestamp.now()
        )

        db.collection("noticias")
            .add(nuevaNoticia)
            .addOnSuccessListener {
                Toast.makeText(this, "✅ ¡Noticia guardada con éxito!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "❌ Error al guardar la noticia.", Toast.LENGTH_LONG).show()
            }
    }
}
