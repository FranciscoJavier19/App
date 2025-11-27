package com.example.a113_4b_25092025

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrarCuentaActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // El código carga tu archivo de diseño
        setContentView(R.layout.registrar_cuenta)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Conectamos el código a los componentes de tu XML usando sus IDs
        val inputNombre = findViewById<EditText>(R.id.inputNombre)
        val inputCorreo = findViewById<EditText>(R.id.inputCorreo)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Añadimos la funcionalidad a los botones
        btnVolver.setOnClickListener { finish() }

        btnRegistrar.setOnClickListener {
            val nombre = inputNombre.text.toString().trim()
            val correo = inputCorreo.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (nombre.isBlank() || correo.isBlank() || password.isBlank()) {
                mostrarAlerta("⚠️ Campos Incompletos", "Por favor, rellena todos los campos para continuar.")
                return@setOnClickListener
            }

            // Lógica de registro en Firebase (Authentication y Firestore)
            auth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            val usuario = hashMapOf(
                                "id" to userId,
                                "nombre" to nombre,
                                "email" to correo,
                                "contraseña" to password
                            )
                            db.collection("usuarios").document(userId).set(usuario)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "✅ ¡Cuenta creada exitosamente!", Toast.LENGTH_LONG).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    mostrarAlerta("❌ Error de Base de Datos", "No se pudo guardar la información del usuario.")
                                }
                        }
                    } else {
                        mostrarAlerta("❌ Error de Registro", "No se pudo crear la cuenta. Es posible que el correo ya esté en uso o la contraseña sea muy débil.")
                    }
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
