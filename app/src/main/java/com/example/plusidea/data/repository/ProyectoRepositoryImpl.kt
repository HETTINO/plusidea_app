package com.example.plusidea.data.repository

import com.example.plusidea.data.local.dao.ProyectoDao
import com.example.plusidea.data.mapper.toDomain
import com.example.plusidea.data.mapper.toEntity
import com.example.plusidea.domain.model.Proyecto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProyectoRepositoryImpl @Inject constructor(
    private val dao: ProyectoDao
) : ProyectoRepository {

    override fun getAllProyectos(): Flow<List<Proyecto>> =
        dao.getAllProyectos().map { lista -> lista.map { it.toDomain() } }

    override fun observeProyectoById(id: Long): Flow<Proyecto?> =
        dao.observeProyectoById(id).map { it?.toDomain() }

    override suspend fun getProyectoById(id: Long): Proyecto? =
        dao.getProyectoById(id)?.toDomain()

    override suspend fun insertProyecto(proyecto: Proyecto) =
        dao.insertProyecto(proyecto.toEntity())

    override suspend fun deleteProyecto(proyecto: Proyecto) =
        dao.deleteProyecto(proyecto.toEntity())
}