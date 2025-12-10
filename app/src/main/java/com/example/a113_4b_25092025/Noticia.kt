package com.example.a113_4b_25092025

import java.io.Serializable
import java.util.Date // Importamos la fecha estándar de Java

// Modelo de datos que representa una noticia, basado en tus requisitos.
data class Noticia(
    val id: String? = null,
    val titulo: String? = null,
    val resumen: String? = null,
    val contenido: String? = null,
    val autor: String? = null,
    val fecha: Date? = null, // <-- ¡CAMBIO IMPORTANTE! Usamos Date en lugar de Timestamp
    val imagenUrl: String? = null
) : Serializable // Serializable para poder pasar el objeto entre pantallas
