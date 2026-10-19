package com.example.plusidea.ui.navigation

/**
 * Única fuente de verdad de las rutas de navegación.
 * Ningún otro archivo debe escribir una ruta como String suelto.
 */
object Rutas {

    // --- Destinos simples ---
    const val BIENVENIDA = "bienvenida"
    const val PROYECTOS = "proyectos"
    const val EMPRESAS = "empresas"
    const val LOCACIONES = "locaciones"
    const val AJUSTES = "ajustes"

    // --- Argumentos ---
    const val ARG_PROYECTO_ID = "proyectoId"

    // --- Patrones (se registran en el NavHost) ---
    const val DETALLE = "proyecto_detalle/{$ARG_PROYECTO_ID}"
    const val FORM = "proyecto_form?$ARG_PROYECTO_ID={$ARG_PROYECTO_ID}"

    // --- Constructores (se usan al navegar) ---
    fun detalle(id: Long): String = "proyecto_detalle/$id"

    /** Formulario para crear un proyecto nuevo. */
    fun formNuevo(): String = "proyecto_form"

    /** Formulario para editar un proyecto existente. */
    fun formEditar(id: Long): String = "proyecto_form?$ARG_PROYECTO_ID=$id"
}