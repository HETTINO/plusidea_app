package com.example.plusidea.ui.proyectos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plusidea.data.repository.EmpresaRepository
import com.example.plusidea.data.repository.ProyectoRepository
import com.example.plusidea.domain.model.Empresa
import com.example.plusidea.domain.model.Proyecto
import com.example.plusidea.domain.model.TipoContenido
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    proyectoRepository: ProyectoRepository,
    empresaRepository: EmpresaRepository
) : ViewModel() {

    private val busqueda = MutableStateFlow("")
    private val filtro = MutableStateFlow<TipoContenido?>(null)

    val uiState: StateFlow<ProjectsUiState> = combine(
        proyectoRepository.getAllProyectos(),
        empresaRepository.getAllEmpresas(),
        busqueda,
        filtro
    ) { proyectos, empresas, q, f ->
        armarEstado(proyectos, empresas, q, f)
    }.catch { e ->
        emit(ProjectsUiState.Error(e.message ?: "No se pudieron cargar los proyectos"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProjectsUiState.Loading
    )

    private fun armarEstado(
        proyectos: List<Proyecto>,
        empresas: List<Empresa>,
        q: String,
        f: TipoContenido?
    ): ProjectsUiState {
        val nombres = empresas.associate { it.id to it.nombre }
        val lista = proyectos
            .filter { f == null || it.tipoContenido == f }
            .filter {
                q.isBlank() ||
                        it.titulo.contains(q, ignoreCase = true) ||
                        (nombres[it.empresaId] ?: "").contains(q, ignoreCase = true)
            }
            .map { ProyectoUi(it, nombres[it.empresaId] ?: "Sin empresa") }
        return ProjectsUiState.Success(proyectos = lista, busqueda = q, filtro = f)
    }

    fun onBusquedaChange(texto: String) { busqueda.value = texto }
    fun onFiltroChange(tipo: TipoContenido?) { filtro.value = tipo }
}