package com.example.plusidea.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.plusidea.data.local.entity.LocacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocacionDao {
    @Query("SELECT * FROM locaciones ORDER BY nombre ASC")
    fun getAllLocaciones(): Flow<List<LocacionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocacion(locacion: LocacionEntity): Long

    @Update
    suspend fun updateLocacion(locacion: LocacionEntity)

    @Delete
    suspend fun deleteLocacion(locacion: LocacionEntity)
}