package com.example.plusidea.ui.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plusidea.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AjustesUiState {
    data object Loading : AjustesUiState

    data class Success(
        val temaOscuro: Boolean?,    // null = el usuario aún no eligió: se usa el tema del sistema
        val notificaciones: Boolean
    ) : AjustesUiState

    data class Error(val mensaje: String) : AjustesUiState
}

@HiltViewModel
class AjustesViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<AjustesUiState> = combine(
        repository.temaOscuro, repository.notificaciones
    ) { tema, notificaciones ->
        armarEstado(tema, notificaciones)
    }.catch { e ->
        emit(AjustesUiState.Error(e.message ?: "No se pudieron leer los ajustes"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AjustesUiState.Loading
    )

    private fun armarEstado(tema: Boolean?, notificaciones: Boolean): AjustesUiState =
        AjustesUiState.Success(tema, notificaciones)

    fun setTemaOscuro(oscuro: Boolean) {
        viewModelScope.launch { repository.setTemaOscuro(oscuro) }
    }

    fun setNotificaciones(activadas: Boolean) {
        viewModelScope.launch { repository.setNotificaciones(activadas) }
    }
}