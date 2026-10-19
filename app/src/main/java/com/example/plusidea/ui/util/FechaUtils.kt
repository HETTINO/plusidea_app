package com.example.plusidea.ui.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object FechaUtils {
    private val localeEs: Locale = Locale.forLanguageTag("es")

    fun hoyUtcMillis(): Long =
        LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    fun formatear(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
            .format(DateTimeFormatter.ofPattern("dd MMM yyyy", localeEs))

    fun formatearCorto(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
            .format(DateTimeFormatter.ofPattern("dd-MMM", localeEs))

    fun formatearHora(minutos: Int): String =
        String.format(Locale.US, "%02d:%02d", minutos / 60, minutos % 60)

    // --- NUEVO: validación de fecha y hora ---

    /** Minutos transcurridos desde las 00:00 hasta ahora */
    fun minutosAhora(): Int = LocalTime.now().let { it.hour * 60 + it.minute }

    /** Próxima hora en punto, como valor por defecto del formulario */
    fun horaSiguiente(): Int = minOf((minutosAhora() / 60 + 1) * 60, 23 * 60 + 59)

    /** true si la fecha y hora indicadas ya pasaron */
    fun esPasado(fechaMillis: Long, horaMinutos: Int): Boolean {
        val hoy = hoyUtcMillis()
        return fechaMillis < hoy || (fechaMillis == hoy && horaMinutos < minutosAhora())
    }
}