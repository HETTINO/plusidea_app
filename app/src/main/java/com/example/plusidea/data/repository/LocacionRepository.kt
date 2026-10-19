package com.example.plusidea.data.repository

import com.example.plusidea.domain.model.Locacion
import kotlinx.coroutines.flow.Flow

interface LocacionRepository {
    fun getAllLocaciones(): Flow<List<Locacion>>
    suspend fun insertLocacion(locacion: Locacion): Long
    suspend fun updateLocacion(locacion: Locacion)
    suspend fun deleteLocacion(locacion: Locacion)
}