package com.example.plusidea.ui.proyectos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.plusidea.domain.model.TipoContenido
import com.example.plusidea.ui.util.FechaUtils

@Composable
fun ProjectsRoute(
    onNuevoProyecto: () -> Unit,
    onProyectoClick: (Long) -> Unit,
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProjectsContent(
        state = state,
        onNuevoProyecto = onNuevoProyecto,
        onProyectoClick = onProyectoClick,
        onBusquedaChange = viewModel::onBusquedaChange,
        onFiltroChange = viewModel::onFiltroChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsContent(
    state: ProjectsUiState,
    onNuevoProyecto: () -> Unit,
    onProyectoClick: (Long) -> Unit,
    onBusquedaChange: (String) -> Unit,
    onFiltroChange: (TipoContenido?) -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Mis Proyectos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            FilledIconButton(onClick = onNuevoProyecto) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo proyecto")
            }
        }

        when (state) {
            ProjectsUiState.Loading ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            is ProjectsUiState.Error ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        state.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(32.dp)
                    )
                }

            is ProjectsUiState.Success ->
                ProjectsLista(state, onProyectoClick, onBusquedaChange, onFiltroChange)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectsLista(
    state: ProjectsUiState.Success,
    onProyectoClick: (Long) -> Unit,
    onBusquedaChange: (String) -> Unit,
    onFiltroChange: (TipoContenido?) -> Unit
) {
    val opcionesFiltro = listOf<TipoContenido?>(null) + TipoContenido.entries

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.busqueda,
            onValueChange = onBusquedaChange,
            placeholder = { Text("Buscar proyecto...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            items(opcionesFiltro) { tipo ->
                FilterChip(
                    selected = state.filtro == tipo,
                    onClick = { onFiltroChange(tipo) },
                    label = { Text(tipo?.etiqueta ?: "Todos") },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        if (state.proyectos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No hay proyectos para mostrar.\nCrea el primero con el botón +.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.proyectos, key = { it.proyecto.id }) { item ->
                    ProyectoCard(item, onClick = { onProyectoClick(item.proyecto.id) })
                }
            }
        }
    }
}

private val coloresAvatar = listOf(
    Color(0xFFD81B60), // rosa
    Color(0xFF8E24AA), // morado
    Color(0xFF5E35B1), // violeta
    Color(0xFF3949AB), // índigo
    Color(0xFF0277BD), // azul
    Color(0xFF00897B), // turquesa
    Color(0xFF388E3C), // verde
    Color(0xFFE65100), // naranja
    Color(0xFFF4511E), // coral
    Color(0xFF6D4C41), // café
    Color(0xFF546E7A), // gris azulado
    Color(0xFFC62828)  // rojo
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProyectoCard(item: ProyectoUi, onClick: () -> Unit) {
    val p = item.proyecto
    val tipo = p.tipoContenido
    val iniciales = item.empresaNombre.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
    val colorAvatar = coloresAvatar[(p.empresaId % coloresAvatar.size).toInt()]

    Card(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        colorAvatar,
                        androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(iniciales, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    p.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(item.empresaNombre, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${tipo.icono} ${tipo.etiqueta}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                tipo.color,
                                androidx.compose.foundation.shape.RoundedCornerShape(50)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${FechaUtils.formatearCorto(p.fechaGrabacion)} • ${
                            FechaUtils.formatearHora(
                                p.horaInicio
                            )
                        }",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}