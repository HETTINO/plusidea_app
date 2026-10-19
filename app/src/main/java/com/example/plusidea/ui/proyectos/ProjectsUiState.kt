package com.example.plusidea.ui.proyectos

import com.example.plusidea.domain.model.Proyecto
import com.example.plusidea.domain.model.TipoContenido

data class ProyectoUi(
    val proyecto: Proyecto,
    val empresaNombre: String
)

sealed interface ProjectsUiState {
    data object Loading : ProjectsUiState

    data class Success(
        val proyectos: List<ProyectoUi>,
        val busqueda: String = "",
        val filtro: TipoContenido? = null   // null = "Todos"
    ) : ProjectsUiState

    data class Error(val mensaje: String) : ProjectsUiState
}