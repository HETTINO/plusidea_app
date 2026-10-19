package com.example.plusidea.ui.locaciones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.plusidea.domain.model.Locacion
import com.example.plusidea.ui.components.ConfirmarEliminarDialog
import com.example.plusidea.ui.components.EncabezadoLista

@Composable
fun LocacionesRoute(
    viewModel: LocacionesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LocacionesContent(
        state = state,
        onGuardar = { id, nombre, direccion -> viewModel.guardar(id, nombre, direccion) },
        onEliminar = { locacion -> viewModel.eliminar(locacion) },
        onMensajeMostrado = { viewModel.mensajeMostrado() }
    )
}

@Composable
fun LocacionesContent(
    state: LocacionesUiState,
    onGuardar: (Long?, String, String) -> Unit,
    onEliminar: (Locacion) -> Unit,
    onMensajeMostrado: () -> Unit
) {
    when (state) {
        LocacionesUiState.Loading ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        is LocacionesUiState.Error ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    state.mensaje,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(32.dp)
                )
            }

        is LocacionesUiState.Success ->
            LocacionesSuccess(state, onGuardar, onEliminar, onMensajeMostrado)
    }
}

@Composable
private fun LocacionesSuccess(
    state: LocacionesUiState.Success,
    onGuardar: (Long?, String, String) -> Unit,
    onEliminar: (Locacion) -> Unit,
    onMensajeMostrado: () -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var enEdicion by remember { mutableStateOf<Locacion?>(null) }
    var porEliminar by remember { mutableStateOf<Locacion?>(null) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbar.showSnackbar(it)
            onMensajeMostrado()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            EncabezadoLista("Locaciones") { enEdicion = null; mostrarDialogo = true }

            if (state.locaciones.isEmpty()) {
                Text(
                    "Aún no hay locaciones.\nAgrega una con el botón +.",
                    modifier = Modifier.padding(32.dp)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.locaciones, key = { it.id }) { locacion ->
                        Card(
                            onClick = { enEdicion = locacion; mostrarDialogo = true },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                Modifier.padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(locacion.nombre, fontWeight = FontWeight.SemiBold)
                                    if (locacion.direccion.isNotBlank()) {
                                        Text(locacion.direccion, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                IconButton(onClick = { porEliminar = locacion }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        LocacionDialog(
            inicial = enEdicion,
            onGuardar = { nombre, direccion ->
                onGuardar(enEdicion?.id, nombre, direccion)
                mostrarDialogo = false
            },
            onCancelar = { mostrarDialogo = false }
        )
    }

    porEliminar?.let { locacion ->
        ConfirmarEliminarDialog(
            mensaje = "¿Eliminar la locación \"${locacion.nombre}\"? Esta acción no se puede deshacer.",
            onConfirmar = {
                onEliminar(locacion)
                porEliminar = null
            },
            onCancelar = { porEliminar = null }
        )
    }
}

@Composable
private fun LocacionDialog(
    inicial: Locacion?,
    onGuardar: (String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf(inicial?.nombre ?: "") }
    var direccion by remember { mutableStateOf(inicial?.direccion ?: "") }
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (inicial == null) "Nueva locación" else "Editar locación") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onGuardar(nombre, direccion) }, enabled = nombre.isNotBlank()) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}