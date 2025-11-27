package com.example.a113_4b_25092025

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    
    // Declaramos las vistas que vamos a usar
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    
    private val listaNoticias = mutableListOf<Noticia>()
    private lateinit var adapter: NoticiasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // El código carga tu archivo de diseño
        setContentView(R.layout.activity_home)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Conectamos el código a los componentes de tu XML usando sus IDs
        recyclerView = findViewById(R.id.recyclerViewNoticias)
        progressBar = findViewById(R.id.progressBar)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        setupRecyclerView()

        // Funcionalidad de los botones
        btnCerrarSesion.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        fab.setOnClickListener {
            startActivity(Intent(this, AgregarNoticiaActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Cada vez que volvemos a esta pantalla, recargamos las noticias
        cargarNoticias()
    }

    private fun setupRecyclerView() {
        adapter = NoticiasAdapter(listaNoticias)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun cargarNoticias() {
        progressBar.visibility = View.VISIBLE // Mostramos la barra de carga

        // Lógica para obtener las noticias de Firestore
        db.collection("noticias")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                listaNoticias.clear()
                for (document in result) {
                    val noticia = document.toObject(Noticia::class.java).copy(id = document.id)
                    listaNoticias.add(noticia)
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE // Ocultamos la barra de carga
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Log.w("HomeActivity", "Error al cargar noticias.", exception)
                Toast.makeText(this, "❌ Error al cargar las noticias.", Toast.LENGTH_SHORT).show()
            }
    }
}
