package com.example.plusidea.ui.proyectos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.plusidea.domain.model.TipoContenido
import com.example.plusidea.ui.util.FechaUtils

private val FormaCampo = RoundedCornerShape(12.dp)

// ---------------------------------------------------------------
// Route (stateful): único punto que conoce el ViewModel
// ---------------------------------------------------------------
@Composable
fun ProyectoFormRoute(
    onBack: () -> Unit,
    viewModel: ProyectoFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Al guardar con éxito, volvemos atrás
    val guardado = (state as? ProyectoFormUiState.Success)?.form?.guardado == true
    LaunchedEffect(guardado) { if (guardado) onBack() }

    ProyectoFormContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

// ---------------------------------------------------------------
// Content (stateless): solo recibe estado y eventos
// ---------------------------------------------------------------
@Composable
fun ProyectoFormContent(
    state: ProyectoFormUiState,
    onEvent: (ProyectoFormEvent) -> Unit,
    onBack: () -> Unit
) {
    when (state) {
        ProyectoFormUiState.Loading ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        is ProyectoFormUiState.Error ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(state.mensaje, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = onBack) { Text("Volver") }
            }

        is ProyectoFormUiState.Success ->
            ProyectoFormSuccess(state.form, onEvent, onBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProyectoFormSuccess(
    state: ProyectoFormState,
    onEvent: (ProyectoFormEvent) -> Unit,
    onBack: () -> Unit
) {
    var mostrarFecha by remember { mutableStateOf(false) }
    var mostrarHora by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.imePadding(),
        bottomBar = {
            Column(
                Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                state.error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Button(
                    onClick = { onEvent(ProyectoFormEvent.Guardar) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        if (state.esEdicion) "Guardar cambios" else "Guardar Proyecto",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cancelar / Aceptar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onBack) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = { onEvent(ProyectoFormEvent.Guardar) }) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            }

            Campo(
                valor = state.titulo,
                onChange = { v -> onEvent(ProyectoFormEvent.TituloCambio(v)) },
                placeholder = "Título del Proyecto",
                singleLine = true
            )

            // Tipo de contenido (chips)
            Text("Tipo de contenido", style = MaterialTheme.typography.labelMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                TipoContenido.entries.forEach { t ->
                    FilterChip(
                        selected = state.tipo == t,
                        onClick = { onEvent(ProyectoFormEvent.TipoCambio(t)) },
                        label = { Text(t.etiqueta) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = t.color,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Fecha y hora
            CampoClickable(
                etiqueta = "Fecha de grabación",
                emoji = "📅",
                texto = FechaUtils.formatear(state.fechaMillis),
                esPlaceholder = false,
                onClick = { mostrarFecha = true }
            )
            CampoClickable(
                etiqueta = "Hora de inicio",
                emoji = "🕒",
                texto = FechaUtils.formatearHora(state.horaMinutos),
                esPlaceholder = false,
                onClick = { mostrarHora = true }
            )

            // Empresa
            CampoSelector(
                etiqueta = "Empresa",
                emoji = "🏢",
                placeholder = "Seleccionar o + nueva",
                valor = state.empresas.firstOrNull { it.id == state.empresaId }?.nombre,
                opciones = state.empresas.map { e ->
                    e.nombre to { onEvent(ProyectoFormEvent.EmpresaElegida(e.id)) }
                } + ("➕ Nueva empresa" to { onEvent(ProyectoFormEvent.EmpresaElegida(null)) })
            )
            if (state.empresaId == null) {
                Campo(
                    valor = state.nuevaEmpresa,
                    onChange = { v -> onEvent(ProyectoFormEvent.NuevaEmpresaCambio(v)) },
                    placeholder = "Nombre de la nueva empresa",
                    singleLine = true
                )
            }

            // Locación
            CampoSelector(
                etiqueta = "Locación",
                emoji = "📍",
                placeholder = "Seleccionar o + nueva",
                valor = state.locaciones.firstOrNull { it.id == state.locacionId }?.nombre,
                opciones = state.locaciones.map { l ->
                    l.nombre to { onEvent(ProyectoFormEvent.LocacionElegida(l.id)) }
                } + ("➕ Nueva locación" to { onEvent(ProyectoFormEvent.LocacionElegida(null)) })
            )
            if (state.locacionId == null) {
                Campo(
                    valor = state.nuevaLocacion,
                    onChange = { v -> onEvent(ProyectoFormEvent.NuevaLocacionCambio(v)) },
                    placeholder = "Nombre de la nueva locación",
                    singleLine = true
                )
                Campo(
                    valor = state.nuevaDireccion,
                    onChange = { v -> onEvent(ProyectoFormEvent.NuevaDireccionCambio(v)) },
                    placeholder = "Dirección"
                )
            }

            Campo(
                valor = state.guion,
                onChange = { v -> onEvent(ProyectoFormEvent.GuionCambio(v)) },
                placeholder = "Guion / descripción",
                minLines = 3
            )

            // Cuadrícula de dos columnas
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Campo(
                    valor = state.materiales,
                    onChange = { v -> onEvent(ProyectoFormEvent.MaterialesCambio(v)) },
                    placeholder = "Materiales",
                    modifier = Modifier.weight(1f)
                )
                Campo(
                    valor = state.tiempoEstimado,
                    onChange = { v -> onEvent(ProyectoFormEvent.TiempoEstimadoCambio(v)) },
                    placeholder = "Tiempo estimado",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Campo(
                    valor = state.outfit,
                    onChange = { v -> onEvent(ProyectoFormEvent.OutfitCambio(v)) },
                    placeholder = "Outfit",
                    modifier = Modifier.weight(1f)
                )
                Campo(
                    valor = state.pago,
                    onChange = { v -> onEvent(ProyectoFormEvent.PagoCambio(v)) },
                    placeholder = "Pago Acordado",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
            }

            Campo(
                valor = state.enlaceInspiracion,
                onChange = { v -> onEvent(ProyectoFormEvent.EnlaceCambio(v)) },
                placeholder = "Enlace de inspiración",
                singleLine = true,
                keyboardType = KeyboardType.Uri
            )

            Spacer(Modifier.height(8.dp))
        }
    }

    // Selector de fecha (no permite fechas pasadas)
    if (mostrarFecha) {
        val hoy = remember { FechaUtils.hoyUtcMillis() }
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.fechaMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= hoy
                override fun isSelectableYear(year: Int) = year >= java.time.LocalDate.now().year
            }
        )
        DatePickerDialog(
            onDismissRequest = { mostrarFecha = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        onEvent(ProyectoFormEvent.FechaCambio(millis))
                    }
                    mostrarFecha = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarFecha = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = pickerState) }
    }

    // Selector de hora (si la fecha es hoy, no permite horas pasadas)
    if (mostrarHora) {
        val timeState = rememberTimePickerState(
            initialHour = state.horaMinutos / 60,
            initialMinute = state.horaMinutos % 60,
            is24Hour = true
        )
        val esHoy = state.fechaMillis == FechaUtils.hoyUtcMillis()
        val horaPasada = esHoy && (timeState.hour * 60 + timeState.minute) < FechaUtils.minutosAhora()

        AlertDialog(
            onDismissRequest = { mostrarHora = false },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TimePicker(state = timeState)
                    if (horaPasada) {
                        Text(
                            "Esa hora ya pasó. Elige una posterior.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !horaPasada,
                    onClick = {
                        onEvent(ProyectoFormEvent.HoraCambio(timeState.hour * 60 + timeState.minute))
                        mostrarHora = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarHora = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun Campo(
    valor: String,
    onChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        placeholder = { Text(placeholder) },
        singleLine = singleLine,
        minLines = minLines,
        shape = FormaCampo,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun CampoClickable(
    etiqueta: String,
    emoji: String,
    texto: String,
    esPlaceholder: Boolean,
    onClick: () -> Unit,
    conFlecha: Boolean = false
) {
    Column {
        Text(
            etiqueta,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Surface(
            onClick = onClick,
            shape = FormaCampo,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(emoji)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = texto,
                    fontWeight = if (esPlaceholder) FontWeight.Normal else FontWeight.SemiBold,
                    color = if (esPlaceholder) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (conFlecha) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun CampoSelector(
    etiqueta: String,
    emoji: String,
    placeholder: String,
    valor: String?,
    opciones: List<Pair<String, () -> Unit>>
) {
    var expandido by remember { mutableStateOf(false) }
    Box {
        CampoClickable(
            etiqueta = etiqueta,
            emoji = emoji,
            texto = valor ?: placeholder,
            esPlaceholder = valor == null,
            onClick = { expandido = true },
            conFlecha = true
        )
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { (texto, accion) ->
                val seleccionado = texto == valor
                DropdownMenuItem(
                    text = {
                        Text(
                            texto,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    trailingIcon = if (seleccionado) {
                        { Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                    } else null,
                    onClick = { accion(); expandido = false }
                )
            }
        }
    }
}