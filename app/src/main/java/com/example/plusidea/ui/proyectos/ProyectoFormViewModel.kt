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
import com.example.plusidea.domain.model.TipoContenido
import com.example.plusidea.ui.navigation.Rutas
import com.example.plusidea.ui.util.FechaUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/** Contenido editable del formulario. */
data class ProyectoFormState(
    val titulo: String = "",
    val tipo: TipoContenido = TipoContenido.REEL,
    val guion: String = "",
    val materiales: String = "",
    val tiempoEstimado: String = "",
    val outfit: String = "",
    val enlaceInspiracion: String = "",
    val pago: String = "",
    val fechaMillis: Long = FechaUtils.hoyUtcMillis(),
    val horaMinutos: Int = FechaUtils.horaSiguiente(),
    val empresaId: Long? = null,      // null = crear empresa nueva
    val nuevaEmpresa: String = "",
    val locacionId: Long? = null,     // null = crear locación nueva
    val nuevaLocacion: String = "",
    val nuevaDireccion: String = "",
    val empresas: List<Empresa> = emptyList(),
    val locaciones: List<Locacion> = emptyList(),
    val esEdicion: Boolean = false,
    val error: String? = null,        // error de validación o de guardado
    val guardado: Boolean = false
)

/** Estado de la pantalla: cargando el proyecto a editar, formulario listo, o fallo de carga. */
sealed interface ProyectoFormUiState {
    data object Loading : ProyectoFormUiState
    data class Success(val form: ProyectoFormState) : ProyectoFormUiState
    data class Error(val mensaje: String) : ProyectoFormUiState
}

/** Todo lo que el usuario puede hacer en el formulario. La UI solo envía estos eventos. */
sealed interface ProyectoFormEvent {
    data class TituloCambio(val valor: String) : ProyectoFormEvent
    data class TipoCambio(val tipo: TipoContenido) : ProyectoFormEvent
    data class FechaCambio(val millis: Long) : ProyectoFormEvent
    data class HoraCambio(val minutos: Int) : ProyectoFormEvent
    data class EmpresaElegida(val id: Long?) : ProyectoFormEvent          // null = nueva empresa
    data class NuevaEmpresaCambio(val valor: String) : ProyectoFormEvent
    data class LocacionElegida(val id: Long?) : ProyectoFormEvent         // null = nueva locación
    data class NuevaLocacionCambio(val valor: String) : ProyectoFormEvent
    data class NuevaDireccionCambio(val valor: String) : ProyectoFormEvent
    data class GuionCambio(val valor: String) : ProyectoFormEvent
    data class MaterialesCambio(val valor: String) : ProyectoFormEvent
    data class TiempoEstimadoCambio(val valor: String) : ProyectoFormEvent
    data class OutfitCambio(val valor: String) : ProyectoFormEvent
    data class PagoCambio(val valor: String) : ProyectoFormEvent
    data class EnlaceCambio(val valor: String) : ProyectoFormEvent
    data object Guardar : ProyectoFormEvent
}

@HiltViewModel
class ProyectoFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val proyectoRepository: ProyectoRepository,
    private val empresaRepository: EmpresaRepository,
    private val locacionRepository: LocacionRepository
) : ViewModel() {

    private val proyectoId: Long = savedStateHandle[Rutas.ARG_PROYECTO_ID] ?: -1L
    private val esEdicion = proyectoId != -1L
    private var fechaOriginal: Long? = null
    private var horaOriginal: Int? = null

    // Estados internos (privados): la UI solo ve el uiState de abajo
    private val form = MutableStateFlow(ProyectoFormState(esEdicion = esEdicion))
    private val cargando = MutableStateFlow(esEdicion)
    private val errorCarga = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProyectoFormUiState> = combine(form, cargando, errorCarga) { f, c, e ->
        armarEstado(f, c, e)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = armarEstado(form.value, cargando.value, errorCarga.value)
    )

    init {
        viewModelScope.launch {
            empresaRepository.getAllEmpresas()
                .catch { e -> errorCarga.value = e.message ?: "No se pudieron cargar las empresas" }
                .collect { lista -> form.update { it.copy(empresas = lista) } }
        }
        viewModelScope.launch {
            locacionRepository.getAllLocaciones()
                .catch { e -> errorCarga.value = e.message ?: "No se pudieron cargar las locaciones" }
                .collect { lista -> form.update { it.copy(locaciones = lista) } }
        }
        if (esEdicion) cargarProyecto()
    }

    private fun armarEstado(f: ProyectoFormState, cargando: Boolean, error: String?): ProyectoFormUiState =
        when {
            error != null -> ProyectoFormUiState.Error(error)
            cargando -> ProyectoFormUiState.Loading
            else -> ProyectoFormUiState.Success(f)
        }

    private fun cargarProyecto() {
        viewModelScope.launch {
            try {
                val p = proyectoRepository.getProyectoById(proyectoId)
                if (p == null) {
                    errorCarga.value = "No se encontró el proyecto"
                } else {
                    fechaOriginal = p.fechaGrabacion
                    horaOriginal = p.horaInicio
                    form.update {
                        it.copy(
                            titulo = p.titulo,
                            tipo = p.tipoContenido,
                            guion = p.guion,
                            materiales = p.materiales,
                            tiempoEstimado = p.tiempoEstimado,
                            outfit = p.outfit,
                            enlaceInspiracion = p.enlaceInspiracion,
                            pago = p.pagoAcordado.toString(),
                            fechaMillis = p.fechaGrabacion,
                            horaMinutos = p.horaInicio,
                            empresaId = p.empresaId,
                            locacionId = p.locacionId
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorCarga.value = e.message ?: "No se pudo cargar el proyecto"
            } finally {
                cargando.value = false
            }
        }
    }

    fun onEvent(evento: ProyectoFormEvent) {
        when (evento) {
            is ProyectoFormEvent.TituloCambio -> editar { it.copy(titulo = evento.valor) }
            is ProyectoFormEvent.TipoCambio -> editar { it.copy(tipo = evento.tipo) }
            is ProyectoFormEvent.FechaCambio -> editar { it.copy(fechaMillis = evento.millis) }
            is ProyectoFormEvent.HoraCambio -> editar { it.copy(horaMinutos = evento.minutos) }
            is ProyectoFormEvent.EmpresaElegida -> editar { it.copy(empresaId = evento.id) }
            is ProyectoFormEvent.NuevaEmpresaCambio -> editar { it.copy(nuevaEmpresa = evento.valor) }
            is ProyectoFormEvent.LocacionElegida -> editar { it.copy(locacionId = evento.id) }
            is ProyectoFormEvent.NuevaLocacionCambio -> editar { it.copy(nuevaLocacion = evento.valor) }
            is ProyectoFormEvent.NuevaDireccionCambio -> editar { it.copy(nuevaDireccion = evento.valor) }
            is ProyectoFormEvent.GuionCambio -> editar { it.copy(guion = evento.valor) }
            is ProyectoFormEvent.MaterialesCambio -> editar { it.copy(materiales = evento.valor) }
            is ProyectoFormEvent.TiempoEstimadoCambio -> editar { it.copy(tiempoEstimado = evento.valor) }
            is ProyectoFormEvent.OutfitCambio -> editar { it.copy(outfit = evento.valor) }
            is ProyectoFormEvent.PagoCambio -> editar { it.copy(pago = evento.valor) }
            is ProyectoFormEvent.EnlaceCambio -> editar { it.copy(enlaceInspiracion = evento.valor) }
            ProyectoFormEvent.Guardar -> guardar()
        }
    }

    private fun editar(cambio: (ProyectoFormState) -> ProyectoFormState) {
        form.update { cambio(it).copy(error = null) }
    }

    private fun guardar() {
        val s = form.value
        val pago = s.pago.replace(',', '.').toDoubleOrNull()
        val cambioFechaHora = !s.esEdicion ||
                s.fechaMillis != fechaOriginal ||
                s.horaMinutos != horaOriginal

        val error = when {
            s.titulo.isBlank() -> "El título es obligatorio"
            s.empresaId == null && s.nuevaEmpresa.isBlank() -> "Selecciona o escribe una empresa"
            s.locacionId == null && s.nuevaLocacion.isBlank() -> "Selecciona o escribe una locación"

            cambioFechaHora && FechaUtils.esPasado(s.fechaMillis, s.horaMinutos) ->
                "La fecha y hora de la grabación no pueden estar en el pasado"

            pago == null -> "El pago debe ser un número"
            else -> null
        }
        if (error != null || pago == null) {
            form.update { it.copy(error = error) }
            return
        }

        viewModelScope.launch {
            try {
                val empresaId = s.empresaId
                    ?: empresaRepository.insertEmpresa(Empresa(nombre = s.nuevaEmpresa.trim()))
                val locacionId = s.locacionId
                    ?: locacionRepository.insertLocacion(
                        Locacion(nombre = s.nuevaLocacion.trim(), direccion = s.nuevaDireccion.trim())
                    )
                // id = 0 crea uno nuevo; con un id existente, REPLACE actualiza el proyecto
                proyectoRepository.insertProyecto(
                    Proyecto(
                        id = if (proyectoId == -1L) 0 else proyectoId,
                        titulo = s.titulo.trim(),
                        tipoContenido = s.tipo,
                        empresaId = empresaId,
                        locacionId = locacionId,
                        guion = s.guion,
                        materiales = s.materiales,
                        tiempoEstimado = s.tiempoEstimado,
                        outfit = s.outfit,
                        pagoAcordado = pago,
                        enlaceInspiracion = s.enlaceInspiracion,
                        fechaGrabacion = s.fechaMillis,
                        horaInicio = s.horaMinutos
                    )
                )
                form.update { it.copy(guardado = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                form.update { it.copy(error = e.message ?: "No se pudo guardar el proyecto") }
            }
        }
    }
}