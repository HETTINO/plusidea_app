package com.example.plusidea.data.repository

import com.example.plusidea.data.local.dao.LocacionDao
import com.example.plusidea.data.mapper.toDomain
import com.example.plusidea.data.mapper.toEntity
import com.example.plusidea.domain.model.Locacion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocacionRepositoryImpl @Inject constructor(
    private val dao: LocacionDao
) : LocacionRepository {

    override fun getAllLocaciones(): Flow<List<Locacion>> =
        dao.getAllLocaciones().map { lista -> lista.map { it.toDomain() } }

    override suspend fun insertLocacion(locacion: Locacion): Long =
        dao.insertLocacion(locacion.toEntity())

    override suspend fun updateLocacion(locacion: Locacion) =
        dao.updateLocacion(locacion.toEntity())

    override suspend fun deleteLocacion(locacion: Locacion) =
        dao.deleteLocacion(locacion.toEntity())
}