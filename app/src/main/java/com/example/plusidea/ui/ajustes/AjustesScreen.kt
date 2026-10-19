package com.example.plusidea.ui.ajustes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home // ⬅️ ¡AQUÍ ESTÁ LO NUEVO! Import del icono
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AjustesRoute(
    onIrABienvenida: () -> Unit,
    viewModel: AjustesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AjustesContent(
        state = state,
        onTemaOscuroChange = viewModel::setTemaOscuro,
        onNotificacionesChange = viewModel::setNotificaciones,
        onIrABienvenida = onIrABienvenida
    )
}

@Composable
fun AjustesContent(
    state: AjustesUiState,
    onTemaOscuroChange: (Boolean) -> Unit,
    onNotificacionesChange: (Boolean) -> Unit,
    onIrABienvenida: () -> Unit
) {
    when (state) {
        AjustesUiState.Loading ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        is AjustesUiState.Error ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    state.mensaje,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(32.dp)
                )
            }

        is AjustesUiState.Success ->
            AjustesSuccess(state, onTemaOscuroChange, onNotificacionesChange, onIrABienvenida)
    }
}

@Composable
private fun AjustesSuccess(
    state: AjustesUiState.Success,
    onTemaOscuroChange: (Boolean) -> Unit,
    onNotificacionesChange: (Boolean) -> Unit,
    onIrABienvenida: () -> Unit
) {
    val temaGuardado = state.temaOscuro
    val notificaciones = state.notificaciones
    val context = LocalContext.current
    val oscuro = temaGuardado ?: isSystemInDarkTheme()
    var mostrarAcerca by remember { mutableStateOf(false) }
    var permisoDenegado by remember { mutableStateOf(false) }

    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoDenegado = !concedido
        if (concedido) onNotificacionesChange(true)
    }

    fun alternarNotificaciones(activar: Boolean) {
        if (!activar) {
            permisoDenegado = false
            onNotificacionesChange(false)
            return
        }
        val necesitaPermiso = Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
        if (necesitaPermiso) pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        else onNotificacionesChange(true)
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            "Ajustes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Tema oscuro
        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Tema oscuro", fontWeight = FontWeight.SemiBold)
                    Text(if (oscuro) "Oscuro" else "Claro", style = MaterialTheme.typography.bodySmall)
                }
                Switch(
                    checked = oscuro,
                    onCheckedChange = onTemaOscuroChange,
                    thumbContent = if (oscuro) {
                        { Icon(Icons.Default.Check, null, Modifier.size(SwitchDefaults.IconSize)) }
                    } else null,
                    colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                )
            }
        }

        // Notificaciones
        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Notificaciones", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Aviso diario si grabas hoy o mañana",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = notificaciones,
                        onCheckedChange = ::alternarNotificaciones,
                        thumbContent = if (notificaciones) {
                            { Icon(Icons.Default.Check, null, Modifier.size(SwitchDefaults.IconSize)) }
                        } else null,
                        colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                    )
                }
                if (permisoDenegado) {
                    Text(
                        "Sin el permiso de notificaciones no se pueden enviar avisos. " +
                                "Puedes activarlo en Ajustes del teléfono > Apps > Plusidea.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }

        // Acerca de
        Card(
            onClick = { mostrarAcerca = true },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Acerca de Plusidea", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(16.dp))
        }

        // ⬅️ ¡AQUÍ ESTÁ LO NUEVO! Opción para regresar a la pantalla de bienvenida
        Card(
            onClick = onIrABienvenida,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    "Volver a la bienvenida",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }

    if (mostrarAcerca) {
        AlertDialog(
            onDismissRequest = { mostrarAcerca = false },
            title = { Text("Plusidea") },
            text = {
                Text(
                    "Organiza tus grabaciones sin esfuerzo: proyectos, empresas y " +
                            "locaciones en un solo lugar.\n\nVersión 1.0"
                )
            },
            confirmButton = { TextButton(onClick = { mostrarAcerca = false }) { Text("Cerrar") } }
        )
    }
}