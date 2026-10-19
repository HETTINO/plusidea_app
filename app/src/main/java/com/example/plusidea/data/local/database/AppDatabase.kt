package com.example.plusidea.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.plusidea.data.local.dao.EmpresaDao
import com.example.plusidea.data.local.dao.LocacionDao
import com.example.plusidea.data.local.dao.ProyectoDao
import com.example.plusidea.data.local.entity.EmpresaEntity
import com.example.plusidea.data.local.entity.LocacionEntity
import com.example.plusidea.data.local.entity.ProyectoEntity

@Database(
    entities = [EmpresaEntity::class, LocacionEntity::class, ProyectoEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun empresaDao(): EmpresaDao
    abstract fun locacionDao(): LocacionDao
    abstract fun proyectoDao(): ProyectoDao
}