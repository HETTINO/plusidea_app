package com.example.plusidea.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "proyectos")
data class ProyectoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val tipoContenido: String,
    val empresaId: Long,
    val locacionId: Long,
    val guion: String,
    val materiales: String,
    val tiempoEstimado: String,
    val outfit: String,
    val pagoAcordado: Double,
    val enlaceInspiracion: String = "",
    val fechaGrabacion: Long = System.currentTimeMillis(), // milisegundos
    val horaInicio: Int = 9 * 60   // minutos desde las 00:00 (9:00 por defecto)
)