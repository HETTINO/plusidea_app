package com.example.plusidea.data.repository

import com.example.plusidea.domain.model.Empresa
import kotlinx.coroutines.flow.Flow

interface EmpresaRepository {
    fun getAllEmpresas(): Flow<List<Empresa>>
    suspend fun insertEmpresa(empresa: Empresa): Long
    suspend fun updateEmpresa(empresa: Empresa)
    suspend fun deleteEmpresa(empresa: Empresa)
}