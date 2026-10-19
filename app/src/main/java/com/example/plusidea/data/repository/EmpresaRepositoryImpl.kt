package com.example.plusidea.data.repository

import com.example.plusidea.data.local.dao.EmpresaDao
import com.example.plusidea.data.mapper.toDomain
import com.example.plusidea.data.mapper.toEntity
import com.example.plusidea.domain.model.Empresa
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmpresaRepositoryImpl @Inject constructor(
    private val dao: EmpresaDao
) : EmpresaRepository {

    override fun getAllEmpresas(): Flow<List<Empresa>> =
        dao.getAllEmpresas().map { lista -> lista.map { it.toDomain() } }

    override suspend fun insertEmpresa(empresa: Empresa): Long =
        dao.insertEmpresa(empresa.toEntity())

    override suspend fun updateEmpresa(empresa: Empresa) =
        dao.updateEmpresa(empresa.toEntity())

    override suspend fun deleteEmpresa(empresa: Empresa) =
        dao.deleteEmpresa(empresa.toEntity())
}