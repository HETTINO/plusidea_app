package com.example.plusidea.ui.locaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plusidea.data.repository.LocacionRepository
import com.example.plusidea.data.repository.ProyectoRepository
import com.example.plusidea.domain.model.Locacion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LocacionesUiState {
    data object Loading : LocacionesUiState

    data class Success(
        val locaciones: List<Locacion>,
        val mensaje: String? = null   // aviso puntual (snackbar)
    ) : LocacionesUiState

    data class Error(val mensaje: String) : LocacionesUiState
}

@HiltViewModel
class LocacionesViewModel @Inject constructor(
    private val locacionRepository: LocacionRepository,
    private val proyectoRepository: ProyectoRepository
) : ViewModel() {

    private val mensaje = MutableStateFlow<String?>(null)

    val uiState: StateFlow<LocacionesUiState> = combine(
        locacionRepository.getAllLocaciones(), mensaje
    ) { locaciones, msg ->
        armarEstado(locaciones, msg)
    }.catch { e ->
        emit(LocacionesUiState.Error(e.message ?: "No se pudieron cargar las locaciones"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LocacionesUiState.Loading
    )

    private fun armarEstado(locaciones: List<Locacion>, msg: String?): LocacionesUiState =
        LocacionesUiState.Success(locaciones, msg)

    fun guardar(id: Long?, nombre: String, direccion: String) {
        if (nombre.isBlank()) return
        viewModelScope.launch {
            if (id == null) {
                locacionRepository.insertLocacion(
                    Locacion(nombre = nombre.trim(), direccion = direccion.trim())
                )
            } else {
                locacionRepository.updateLocacion(
                    Locacion(id = id, nombre = nombre.trim(), direccion = direccion.trim())
                )
            }
        }
    }

    fun eliminar(locacion: Locacion) {
        viewModelScope.launch {
            val enUso = proyectoRepository.getAllProyectos().first().any { it.locacionId == locacion.id }
            if (enUso) mensaje.value = "No se puede eliminar \"${locacion.nombre}\": tiene proyectos asociados"
            else locacionRepository.deleteLocacion(locacion)
        }
    }

    fun mensajeMostrado() { mensaje.value = null }
}