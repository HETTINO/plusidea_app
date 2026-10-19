package com.example.plusidea.ui.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.plusidea.ui.ajustes.AjustesRoute
import com.example.plusidea.ui.bienvenida.BienvenidaScreen
import com.example.plusidea.ui.empresas.EmpresasRoute
import com.example.plusidea.ui.locaciones.LocacionesRoute
import com.example.plusidea.ui.proyectos.ProjectsRoute
import com.example.plusidea.ui.proyectos.ProyectoDetalleRoute
import com.example.plusidea.ui.proyectos.ProyectoFormRoute

private data class Destino(val ruta: String, val etiqueta: String, val icono: ImageVector)

private val destinos = listOf(
    Destino(Rutas.PROYECTOS, "Proyectos", Icons.Default.Menu),
    Destino(Rutas.EMPRESAS, "Empresas", Icons.Default.Home),
    Destino(Rutas.LOCACIONES, "Locaciones", Icons.Default.LocationOn),
    Destino(Rutas.AJUSTES, "Ajustes", Icons.Default.Settings)
)

@Composable
fun PlusideaNavHost() {
    val navController = rememberNavController()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    Scaffold(
        bottomBar = {
            // La barra solo se ve en las 4 pantallas principales
            if (destinos.any { it.ruta == rutaActual }) {
                NavigationBar {
                    destinos.forEach { destino ->
                        NavigationBarItem(
                            selected = rutaActual == destino.ruta,
                            onClick = {
                                navController.navigate(destino.ruta) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destino.icono, contentDescription = destino.etiqueta) },
                            label = { Text(destino.etiqueta) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.BIENVENIDA,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {

            composable(Rutas.BIENVENIDA) {
                BienvenidaScreen(
                    onComenzar = {
                        navController.navigate(Rutas.PROYECTOS) {
                            popUpTo(Rutas.BIENVENIDA) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.PROYECTOS) {
                ProjectsRoute(
                    onNuevoProyecto = { navController.navigate(Rutas.formNuevo()) },
                    onProyectoClick = { id -> navController.navigate(Rutas.detalle(id)) }
                )
            }
            composable(
                route = Rutas.DETALLE,
                arguments = listOf(navArgument(Rutas.ARG_PROYECTO_ID) { type = NavType.LongType })
            ) {
                ProyectoDetalleRoute(
                    onBack = { navController.popBackStack() },
                    onEditar = { id -> navController.navigate(Rutas.formEditar(id)) }
                )
            }
            composable(
                route = Rutas.FORM,
                arguments = listOf(navArgument(Rutas.ARG_PROYECTO_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) {
                ProyectoFormRoute(onBack = { navController.popBackStack() })
            }
            composable(Rutas.EMPRESAS) { EmpresasRoute() }
            composable(Rutas.LOCACIONES) { LocacionesRoute() }

            composable(Rutas.AJUSTES) {
                AjustesRoute(
                    onIrABienvenida = {
                        navController.navigate(Rutas.BIENVENIDA) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}