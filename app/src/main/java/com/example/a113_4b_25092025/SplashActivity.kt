package com.example.a113_4b_25092025

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        // Temporizador de 2 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            // CORRECCIÓN: Aseguramos que la navegación sea hacia MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cierra el Splash para no poder volver a él
        }, 2000)
    }
}
