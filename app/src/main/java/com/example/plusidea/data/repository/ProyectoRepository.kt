package com.example.plusidea.data.repository

import com.example.plusidea.domain.model.Proyecto
import kotlinx.coroutines.flow.Flow

interface ProyectoRepository {
    fun getAllProyectos(): Flow<List<Proyecto>>
    fun observeProyectoById(id: Long): Flow<Proyecto?>
    suspend fun getProyectoById(id: Long): Proyecto?
    suspend fun insertProyecto(proyecto: Proyecto)
    suspend fun deleteProyecto(proyecto: Proyecto)
}