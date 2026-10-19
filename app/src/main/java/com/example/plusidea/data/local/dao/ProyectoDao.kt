package com.example.plusidea.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.plusidea.data.local.entity.ProyectoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoDao {

    @Query("SELECT * FROM proyectos WHERE id = :id")
    suspend fun getProyectoById(id: Long): ProyectoEntity?
    @Query("SELECT * FROM proyectos ORDER BY id DESC")
    fun getAllProyectos(): Flow<List<ProyectoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProyecto(proyecto: ProyectoEntity)

    @Query("SELECT * FROM proyectos WHERE id = :id")
    fun observeProyectoById(id: Long): Flow<ProyectoEntity?>

    @Delete
    suspend fun deleteProyecto(proyecto: ProyectoEntity)
}