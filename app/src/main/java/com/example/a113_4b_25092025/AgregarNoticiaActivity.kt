package com.example.a113_4b_25092025

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AgregarNoticiaActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var noticiaId: String? = null
    private var isEditMode = false

    private lateinit var inputTitulo: EditText
    private lateinit var inputResumen: EditText
    private lateinit var inputImagenUrl: EditText
    private lateinit var inputContenido: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnBorrar: Button
    private lateinit var titleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_noticia)

        db = FirebaseFirestore.getInstance()

        inputTitulo = findViewById(R.id.inputTitulo)
        inputResumen = findViewById(R.id.inputResumen)
        inputImagenUrl = findViewById(R.id.inputImagenUrl)
        inputContenido = findViewById(R.id.inputContenido)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnBorrar = findViewById(R.id.btnBorrar)
        titleTextView = findViewById(R.id.agregar_noticia_title)

        // El ID de la noticia (si existe para editar) viene del Intent.
        // Ya no nos preocupamos por el USER_ID.
        noticiaId = intent.getStringExtra("NOTICIA_ID")
        isEditMode = noticiaId != null

        if (isEditMode) {
            setupEditMode()
            cargarDatosNoticia()
        } else {
            titleTextView.text = "Agregar Noticia"
        }

        btnGuardar.setOnClickListener { validarYGuardar() }
        btnCancelar.setOnClickListener { finish() }
        btnBorrar.setOnClickListener { confirmarBorrado() }
    }

    private fun setupEditMode() {
        titleTextView.text = "Editar Noticia"
        btnGuardar.text = "Actualizar Noticia"
        btnBorrar.visibility = View.VISIBLE
    }

    private fun cargarDatosNoticia() {
        noticiaId?.let {
            db.collection("noticias").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        inputTitulo.setText(document.getString("titulo"))
                        inputResumen.setText(document.getString("resumen"))
                        inputImagenUrl.setText(document.getString("imagenUrl"))
                        inputContenido.setText(document.getString("contenido"))
                    } else {
                        Toast.makeText(this, "Error: No se encontró la noticia.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al cargar la noticia: ${e.message}", Toast.LENGTH_LONG).show()
                    finish()
                }
        }
    }

    private fun validarYGuardar() {
        val titulo = inputTitulo.text.toString().trim()
        val resumen = inputResumen.text.toString().trim()
        val imagenUrl = inputImagenUrl.text.toString().trim()
        val contenido = inputContenido.text.toString().trim()

        if (titulo.isBlank() || resumen.isBlank() || imagenUrl.isBlank() || contenido.isBlank()) {
            Toast.makeText(this, "⚠️ Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Llamamos directamente a guardarNoticia, sin buscar el nombre del autor.
        guardarNoticia(titulo, resumen, imagenUrl, contenido)
    }

    private fun guardarNoticia(titulo: String, resumen: String, imagenUrl: String, contenido: String) {
        val datosNoticia: HashMap<String, Any> = hashMapOf(
            "titulo" to titulo,
            "resumen" to resumen,
            "imagenUrl" to imagenUrl,
            "contenido" to contenido
        )

        if (isEditMode) {
            // MODO ACTUALIZAR: Solo actualiza los campos, no toca el autor ni la fecha.
            noticiaId?.let {
                db.collection("noticias").document(it).update(datosNoticia)
                    .addOnSuccessListener {
                        Toast.makeText(this, "✅ ¡Noticia actualizada!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "❌ Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        } else {
            // MODO CREAR: Añade "Anónimo" como autor y la fecha actual.
            datosNoticia["autor"] = "Anónimo"
            datosNoticia["fecha"] = Date()

            db.collection("noticias").add(datosNoticia)
                .addOnSuccessListener {
                    Toast.makeText(this, "✅ ¡Noticia guardada con éxito!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "❌ Error al guardar la noticia: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun confirmarBorrado() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Borrado")
            .setMessage("¿Estás seguro de que quieres borrar esta noticia? Esta acción no se puede deshacer.")
            .setPositiveButton("Borrar") { _, _ ->
                borrarNoticia()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun borrarNoticia() {
        noticiaId?.let {
            db.collection("noticias").document(it).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Noticia borrada con éxito.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al borrar la noticia: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
