package com.example.a113_4b_25092025

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RecuperarContraActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // El código carga tu archivo de diseño
        setContentView(R.layout.recuperar_contrasena)

        db = FirebaseFirestore.getInstance()

        // Conectamos el código a los componentes de tu XML usando sus IDs
        val inputCorreo = findViewById<EditText>(R.id.inputCorreoRecuperar)
        val inputNuevaContra = findViewById<EditText>(R.id.inputNuevaContra)
        val btnRecuperar = findViewById<Button>(R.id.btnEnviarRecuperacion)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Funcionalidad de los botones
        btnVolver.setOnClickListener { finish() }

        btnRecuperar.setOnClickListener {
            val email = inputCorreo.text.toString().trim()
            val nuevaPass = inputNuevaContra.text.toString().trim()

            if (email.isBlank() || nuevaPass.isBlank()) {
                mostrarAlerta("⚠️ Campos Incompletos", "Por favor, ingresa el correo y la nueva contraseña.")
                return@setOnClickListener
            }

            // Lógica para actualizar la contraseña en Firestore
            db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        mostrarAlerta("🤔 Usuario no Encontrado", "El correo no está registrado en nuestra base de datos.")
                    } else {
                        val documentId = documents.documents[0].id
                        db.collection("usuarios").document(documentId)
                            .update("contraseña", nuevaPass)
                            .addOnSuccessListener {
                                Toast.makeText(this, "✅ ¡Contraseña actualizada con éxito!", Toast.LENGTH_LONG).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                mostrarAlerta("❌ Error de Actualización", "No se pudo actualizar la contraseña.")
                                Log.e("RecuperarContra", "Error técnico al actualizar: ", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    mostrarAlerta("❌ Error de Conexión", "No se pudo realizar la consulta. Revisa tu conexión a internet.")
                    Log.e("RecuperarContra", "Error técnico de conexión: ", e)
                }
        }
    }

    private fun mostrarAlerta(titulo: String, mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
