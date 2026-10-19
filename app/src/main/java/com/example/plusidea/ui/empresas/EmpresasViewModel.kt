package com.example.plusidea.ui.empresas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plusidea.data.repository.EmpresaRepository
import com.example.plusidea.data.repository.ProyectoRepository
import com.example.plusidea.domain.model.Empresa
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

sealed interface EmpresasUiState {
    data object Loading : EmpresasUiState

    data class Success(
        val empresas: List<Empresa>,
        val mensaje: String? = null   // aviso puntual (snackbar)
    ) : EmpresasUiState

    data class Error(val mensaje: String) : EmpresasUiState
}

@HiltViewModel
class EmpresasViewModel @Inject constructor(
    private val empresaRepository: EmpresaRepository,
    private val proyectoRepository: ProyectoRepository
) : ViewModel() {

    private val mensaje = MutableStateFlow<String?>(null)

    val uiState: StateFlow<EmpresasUiState> = combine(
        empresaRepository.getAllEmpresas(), mensaje
    ) { empresas, msg ->
        armarEstado(empresas, msg)
    }.catch { e ->
        emit(EmpresasUiState.Error(e.message ?: "No se pudieron cargar las empresas"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EmpresasUiState.Loading
    )

    private fun armarEstado(empresas: List<Empresa>, msg: String?): EmpresasUiState =
        EmpresasUiState.Success(empresas, msg)

    fun guardar(id: Long?, nombre: String) {
        if (nombre.isBlank()) return
        viewModelScope.launch {
            if (id == null) empresaRepository.insertEmpresa(Empresa(nombre = nombre.trim()))
            else empresaRepository.updateEmpresa(Empresa(id = id, nombre = nombre.trim()))
        }
    }

    fun eliminar(empresa: Empresa) {
        viewModelScope.launch {
            val enUso = proyectoRepository.getAllProyectos().first().any { it.empresaId == empresa.id }
            if (enUso) mensaje.value = "No se puede eliminar \"${empresa.nombre}\": tiene proyectos asociados"
            else empresaRepository.deleteEmpresa(empresa)
        }
    }

    fun mensajeMostrado() { mensaje.value = null }
}