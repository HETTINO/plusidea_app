package com.example.plusidea.ui.proyectos

import androidx.compose.ui.graphics.Color
import com.example.plusidea.domain.model.TipoContenido

// Color e ícono son detalles de presentación: viven en la UI, no en el dominio.

val TipoContenido.icono: String
    get() = when (this) {
        TipoContenido.REEL -> "🎬"
        TipoContenido.VIDEO_LARGO -> "🎥"
        TipoContenido.FOTO_POST -> "📷"
        TipoContenido.HISTORIA -> "⏱️"
        TipoContenido.UNBOXING -> "📦"
    }

val TipoContenido.color: Color
    get() = when (this) {
        TipoContenido.REEL -> Color(0xFFE91E63)
        TipoContenido.VIDEO_LARGO -> Color(0xFF3F51B5)
        TipoContenido.FOTO_POST -> Color(0xFF009688)
        TipoContenido.HISTORIA -> Color(0xFFFF9800)
        TipoContenido.UNBOXING -> Color(0xFF795548)
    }