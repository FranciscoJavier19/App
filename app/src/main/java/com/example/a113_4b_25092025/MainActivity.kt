package com.example.a113_4b_25092025

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            irAHome(currentUser.uid) // Si ya hay sesión, pasamos el ID oficial
            return
        }

        val inputCorreo = findViewById<EditText>(R.id.inputCorreo)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val botonPrincipal = findViewById<Button>(R.id.botonPrincipal)
        val linkRegistrar = findViewById<Button>(R.id.linkRegistrar)
        val linkRecuperar = findViewById<Button>(R.id.linkRecuperar)
        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        botonPrincipal.setOnClickListener {
            val correo = inputCorreo.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (correo.isBlank() || password.isBlank()) {
                Toast.makeText(this, "⚠️ Por favor, ingresa tu correo y contraseña.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Si el login oficial funciona, pasamos el ID oficial
                        auth.currentUser?.uid?.let { irAHome(it) }
                    } else {
                        // Plan B: revisar Firestore
                        Log.d("MainActivity", "El login con Auth falló, intentando con Firestore...")
                        revisarFirestore(correo, password)
                    }
                }
        }

        linkRegistrar.setOnClickListener {
            startActivity(Intent(this, RegistrarCuentaActivity::class.java))
        }

        linkRecuperar.setOnClickListener {
            startActivity(Intent(this, RecuperarContraActivity::class.java))
        }
    }

    private fun revisarFirestore(correo: String, pass: String) {
        db.collection("usuarios")
            .whereEqualTo("email", correo)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    mostrarAlerta("❌ Error Definitivo", "El correo no está registrado.")
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    if (document.getString("contraseña") == pass) {
                        val userId = document.id // Obtenemos el ID del documento del usuario
                        Log.d("MainActivity", "¡Login exitoso con Firestore! Pasando userId: $userId")
                        irAHome(userId) // Pasamos el ID encontrado
                        return@addOnSuccessListener
                    }
                }

                mostrarAlerta("❌ Contraseña Incorrecta", "La contraseña no coincide.")
            }
            .addOnFailureListener { e ->
                mostrarAlerta("❌ Error de Conexión", "No se pudo conectar a la base de datos.")
            }
    }

    // Ahora esta función ACEPTA el ID del usuario
    private fun irAHome(userId: String) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("USER_ID", userId) // Lo añadimos al intent
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun mostrarAlerta(titulo: String, mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
