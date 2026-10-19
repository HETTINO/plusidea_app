package com.example.plusidea.domain.model

/**
 * Tipo de contenido de una grabación. Es un concepto de dominio puro:
 * no conoce colores ni íconos (eso vive en la capa de UI).
 */
enum class TipoContenido(val etiqueta: String) {
    REEL("Reel"),
    VIDEO_LARGO("Video largo"),
    FOTO_POST("Foto/Post"),
    HISTORIA("Historia"),
    UNBOXING("Unboxing");

    companion object {
        fun desdeEtiqueta(etiqueta: String): TipoContenido =
            entries.firstOrNull { it.etiqueta.equals(etiqueta, ignoreCase = true) } ?: REEL
    }
}