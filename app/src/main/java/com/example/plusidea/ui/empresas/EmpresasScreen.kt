package com.example.plusidea.ui.empresas

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
import com.example.plusidea.domain.model.Empresa
import com.example.plusidea.ui.components.ConfirmarEliminarDialog
import com.example.plusidea.ui.components.EncabezadoLista

@Composable
fun EmpresasRoute(
    viewModel: EmpresasViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    EmpresasContent(
        state = state,
        onGuardar = { id, nombre -> viewModel.guardar(id, nombre) },
        onEliminar = { empresa -> viewModel.eliminar(empresa) },
        onMensajeMostrado = { viewModel.mensajeMostrado() }
    )
}

@Composable
fun EmpresasContent(
    state: EmpresasUiState,
    onGuardar: (Long?, String) -> Unit,
    onEliminar: (Empresa) -> Unit,
    onMensajeMostrado: () -> Unit
) {
    when (state) {
        EmpresasUiState.Loading ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        is EmpresasUiState.Error ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    state.mensaje,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(32.dp)
                )
            }

        is EmpresasUiState.Success ->
            EmpresasSuccess(state, onGuardar, onEliminar, onMensajeMostrado)
    }
}

@Composable
private fun EmpresasSuccess(
    state: EmpresasUiState.Success,
    onGuardar: (Long?, String) -> Unit,
    onEliminar: (Empresa) -> Unit,
    onMensajeMostrado: () -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var enEdicion by remember { mutableStateOf<Empresa?>(null) }
    var porEliminar by remember { mutableStateOf<Empresa?>(null) }
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
            EncabezadoLista("Empresas") { enEdicion = null; mostrarDialogo = true }

            if (state.empresas.isEmpty()) {
                Text(
                    "Aún no hay empresas.\nAgrega una con el botón +.",
                    modifier = Modifier.padding(32.dp)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.empresas, key = { it.id }) { empresa ->
                        Card(
                            onClick = { enEdicion = empresa; mostrarDialogo = true },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                Modifier.padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    empresa.nombre,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { porEliminar = empresa }) {
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
        EmpresaDialog(
            inicial = enEdicion,
            onGuardar = { nombre ->
                onGuardar(enEdicion?.id, nombre)
                mostrarDialogo = false
            },
            onCancelar = { mostrarDialogo = false }
        )
    }

    porEliminar?.let { empresa ->
        ConfirmarEliminarDialog(
            mensaje = "¿Eliminar la empresa \"${empresa.nombre}\"? Esta acción no se puede deshacer.",
            onConfirmar = {
                onEliminar(empresa)
                porEliminar = null
            },
            onCancelar = { porEliminar = null }
        )
    }
}

@Composable
private fun EmpresaDialog(
    inicial: Empresa?,
    onGuardar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf(inicial?.nombre ?: "") }
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (inicial == null) "Nueva empresa" else "Editar empresa") },
        text = {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { onGuardar(nombre) }, enabled = nombre.isNotBlank()) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}