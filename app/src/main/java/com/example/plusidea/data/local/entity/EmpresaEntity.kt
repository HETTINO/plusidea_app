package com.example.plusidea.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "empresas")
data class EmpresaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String
)