package com.example.plusidea.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val temaOscuro: Flow<Boolean?>
    val notificaciones: Flow<Boolean>
    suspend fun setTemaOscuro(oscuro: Boolean)
    suspend fun setNotificaciones(activadas: Boolean)
}