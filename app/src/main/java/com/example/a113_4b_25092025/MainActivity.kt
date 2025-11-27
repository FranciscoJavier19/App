package com.example.a113_4b_25092025

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // El código carga tu archivo de diseño
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Si el usuario ya inició sesión, va directo al Home
        if (auth.currentUser != null) {
            irAHome()
            return
        }

        // Conectamos el código a los componentes de tu XML usando sus IDs
        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val botonPrincipal = findViewById<Button>(R.id.botonPrincipal)
        val linkRegistrar = findViewById<Button>(R.id.linkRegistrar)
        val linkRecuperar = findViewById<Button>(R.id.linkRecuperar)

        // Funcionalidad del botón de Login
        botonPrincipal.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "⚠️ Por favor, ingresa tu correo y contraseña.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        irAHome()
                    } else {
                        mostrarAlerta("❌ Error de Autenticación", "El correo o la contraseña son incorrectos. Por favor, inténtalo de nuevo.")
                    }
                }
        }

        // Funcionalidad de los links de navegación
        linkRegistrar.setOnClickListener {
            startActivity(Intent(this, RegistrarCuentaActivity::class.java))
        }

        linkRecuperar.setOnClickListener {
            startActivity(Intent(this, RecuperarContraActivity::class.java))
        }
    }

    private fun irAHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
