package com.example.plusidea.ui.proyectos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plusidea.data.repository.EmpresaRepository
import com.example.plusidea.data.repository.LocacionRepository
import com.example.plusidea.data.repository.ProyectoRepository
import com.example.plusidea.domain.model.Empresa
import com.example.plusidea.domain.model.Locacion
import com.example.plusidea.domain.model.Proyecto
import com.example.plusidea.ui.navigation.Rutas
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetalleUiState {
    data object Loading : DetalleUiState

    data class Success(
        val proyecto: Proyecto,
        val empresa: String,
        val locacion: String
    ) : DetalleUiState

    /** El proyecto no existe (por ejemplo, acaba de eliminarse): la Route vuelve atrás. */
    data object NoEncontrado : DetalleUiState

    data class Error(val mensaje: String) : DetalleUiState
}

@HiltViewModel
class ProyectoDetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val proyectoRepository: ProyectoRepository,
    empresaRepository: EmpresaRepository,
    locacionRepository: LocacionRepository
) : ViewModel() {

    private val proyectoId: Long = savedStateHandle[Rutas.ARG_PROYECTO_ID] ?: -1L

    val uiState: StateFlow<DetalleUiState> = combine(
        proyectoRepository.observeProyectoById(proyectoId),
        empresaRepository.getAllEmpresas(),
        locacionRepository.getAllLocaciones()
    ) { proyecto, empresas, locaciones ->
        armarEstado(proyecto, empresas, locaciones)
    }.catch { e ->
        emit(DetalleUiState.Error(e.message ?: "No se pudo cargar el proyecto"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetalleUiState.Loading
    )

    private fun armarEstado(
        proyecto: Proyecto?,
        empresas: List<Empresa>,
        locaciones: List<Locacion>
    ): DetalleUiState {
        if (proyecto == null) return DetalleUiState.NoEncontrado
        return DetalleUiState.Success(
            proyecto = proyecto,
            empresa = empresas.firstOrNull { it.id == proyecto.empresaId }?.nombre ?: "",
            locacion = locaciones.firstOrNull { it.id == proyecto.locacionId }?.nombre ?: ""
        )
    }

    fun eliminar() {
        val actual = uiState.value as? DetalleUiState.Success ?: return
        viewModelScope.launch { proyectoRepository.deleteProyecto(actual.proyecto) }
    }
}