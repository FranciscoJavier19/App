package com.example.a113_4b_25092025

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp

class HomeActivity : AppCompatActivity(), NoticiasAdapter.OnNoticiaInteractionListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    
    private val listaNoticias = mutableListOf<Noticia>()
    private lateinit var adapter: NoticiasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.recyclerViewNoticias)
        progressBar = findViewById(R.id.progressBar)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        setupRecyclerView()

        btnCerrarSesion.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        fab.setOnClickListener {
            // Ya no enviamos el USER_ID, solo abrimos la pantalla.
            val intent = Intent(this, AgregarNoticiaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser == null) {
             val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Tu sesión ha expirado.", Toast.LENGTH_LONG).show()
        } else {
             cargarNoticias()
        }
    }

    private fun setupRecyclerView() {
        adapter = NoticiasAdapter(this, listaNoticias, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun cargarNoticias() {
        progressBar.visibility = View.VISIBLE

        db.collection("noticias")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                listaNoticias.clear()
                for (document in result) {
                    val noticia = Noticia(
                        id = document.id,
                        titulo = document.getString("titulo"),
                        resumen = document.getString("resumen"),
                        contenido = document.getString("contenido"),
                        autor = document.getString("autor"),
                        imagenUrl = document.getString("imagenUrl"),
                        fecha = (document.get("fecha") as? Timestamp)?.toDate()
                    )
                    listaNoticias.add(noticia)
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Log.w("HomeActivity", "Error al cargar noticias.", exception)
                Toast.makeText(this, "❌ Error al cargar las noticias.", Toast.LENGTH_SHORT).show()
            }
    }

    // --- Implementación de la Interfaz del Adaptador ---

    override fun onNoticiaEdit(noticiaId: String) {
        // Solo enviamos el ID de la noticia, que es lo que importa para editar.
        val intent = Intent(this, AgregarNoticiaActivity::class.java).apply {
            putExtra("NOTICIA_ID", noticiaId)
        }
        startActivity(intent)
    }

    override fun onNoticiaDelete(noticiaId: String, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Borrado")
            .setMessage("¿Estás seguro de que quieres borrar esta noticia? La acción no se puede deshacer.")
            .setPositiveButton("Borrar") { _, _ ->
                db.collection("noticias").document(noticiaId).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Noticia borrada.", Toast.LENGTH_SHORT).show()
                        adapter.removerNoticia(position)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al borrar: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
