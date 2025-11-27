package com.example.a113_4b_25092025

import com.google.firebase.Timestamp
import java.io.Serializable

// Modelo de datos que representa una noticia, basado en tus requisitos.
data class Noticia(
    val id: String? = null,
    val titulo: String? = null,
    val resumen: String? = null,      // Corresponde a 'bajada'
    val contenido: String? = null,    // Corresponde a 'cuerpo'
    val autor: String? = null,        // Nuevo campo para el nombre del autor
    val fecha: Timestamp? = null      // Usamos Timestamp para poder ordenar
) : Serializable // Serializable para poder pasar el objeto entre pantallas
