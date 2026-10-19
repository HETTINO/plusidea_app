package com.example.plusidea.ui.proyectos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.plusidea.ui.components.ConfirmarEliminarDialog
import com.example.plusidea.ui.util.FechaUtils
import java.util.Locale

@Composable
fun ProyectoDetalleRoute(
    onBack: () -> Unit,
    onEditar: (Long) -> Unit,
    viewModel: ProyectoDetalleViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Si el proyecto ya no existe (fue eliminado), volvemos a la lista
    LaunchedEffect(state) { if (state is DetalleUiState.NoEncontrado) onBack() }

    ProyectoDetalleContent(
        state = state,
        onBack = onBack,
        onEditar = onEditar,
        onEliminar = viewModel::eliminar
    )
}

@Composable
fun ProyectoDetalleContent(
    state: DetalleUiState,
    onBack: () -> Unit,
    onEditar: (Long) -> Unit,
    onEliminar: () -> Unit
) {
    when (state) {
        DetalleUiState.Loading ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        // Pantalla vacía un instante: la Route ya está volviendo a la lista
        DetalleUiState.NoEncontrado -> Box(Modifier.fillMaxSize())

        is DetalleUiState.Error ->
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

        is DetalleUiState.Success -> DetalleSuccess(state, onBack, onEditar, onEliminar)
    }
}

@Composable
private fun DetalleSuccess(
    state: DetalleUiState.Success,
    onBack: () -> Unit,
    onEditar: (Long) -> Unit,
    onEliminar: () -> Unit
) {
    val p = state.proyecto
    var confirmar by remember { mutableStateOf(false) }

    val tipo = p.tipoContenido
    val coral = Color(0xFFFF6B6B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            FilledTonalIconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            FilledTonalIconButton(onClick = { onEditar(p.id) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
        }

        Text(
            p.titulo.uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            "${tipo.icono} ${tipo.etiqueta}",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .background(tipo.color, RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        )

        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Dato("🏢", state.empresa)
                Dato("📍", state.locacion)
                Dato("📅", "${FechaUtils.formatear(p.fechaGrabacion)} • ${FechaUtils.formatearHora(p.horaInicio)}")
                if (p.enlaceInspiracion.isNotBlank()) Dato("🔗", p.enlaceInspiracion)
                Dato("⏱️", "Tiempo: ${p.tiempoEstimado.ifBlank { "—" }}")
                if (p.outfit.isNotBlank()) Dato("👕", "Outfit: ${p.outfit}")
                if (p.materiales.isNotBlank()) Dato("🎒", "Materiales: ${p.materiales}")
                Text(
                    "Pago: $" + String.format(Locale.US, "%.2f", p.pagoAcordado),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (p.guion.isNotBlank()) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Guion", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(p.guion)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { onEditar(p.id) },
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f)
            ) { Text("Editar") }

            OutlinedButton(
                onClick = { confirmar = true },
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, coral),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = coral),
                modifier = Modifier.weight(1f)
            ) { Text("Eliminar") }
        }
    }

    if (confirmar) {
        ConfirmarEliminarDialog(
            mensaje = "¿Estás seguro de que quieres eliminar este proyecto? Esta acción no se puede deshacer.",
            onConfirmar = { confirmar = false; onEliminar() },
            onCancelar = { confirmar = false }
        )
    }
}

@Composable
private fun Dato(icono: String, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icono)
        Spacer(Modifier.width(12.dp))
        Text(texto)
    }
}