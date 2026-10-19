package com.example.plusidea.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.plusidea.data.local.entity.EmpresaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpresaDao {
    @Query("SELECT * FROM empresas ORDER BY nombre ASC")
    fun getAllEmpresas(): Flow<List<EmpresaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmpresa(empresa: EmpresaEntity): Long

    @Update
    suspend fun updateEmpresa(empresa: EmpresaEntity)

    @Delete
    suspend fun deleteEmpresa(empresa: EmpresaEntity)
}