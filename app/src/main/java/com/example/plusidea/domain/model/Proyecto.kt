package com.example.plusidea.domain.model

/** Modelo de dominio de una grabación programada. Independiente de Room. */
data class Proyecto(
    val id: Long = 0,
    val titulo: String,
    val tipoContenido: TipoContenido,
    val empresaId: Long,
    val locacionId: Long,
    val guion: String,
    val materiales: String,
    val tiempoEstimado: String,
    val outfit: String,
    val pagoAcordado: Double,
    val enlaceInspiracion: String = "",
    val fechaGrabacion: Long,   // milisegundos (medianoche UTC del día)
    val horaInicio: Int         // minutos desde las 00:00
)