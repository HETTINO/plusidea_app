package com.example.plusidea.data.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.plusidea.MainActivity
import com.example.plusidea.data.repository.ProyectoRepository
import com.example.plusidea.ui.util.FechaUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class RecordatorioWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val proyectoRepository: ProyectoRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val hoy = FechaUtils.hoyUtcMillis()
        val manana = hoy + 24L * 60 * 60 * 1000

        val proximos = proyectoRepository.getAllProyectos().first()
            .filter { it.fechaGrabacion == hoy || it.fechaGrabacion == manana }

        if (proximos.isNotEmpty() && tienePermiso()) {
            val texto = if (proximos.size == 1) {
                val cuando = if (proximos[0].fechaGrabacion == hoy) "Hoy" else "Mañana"
                "$cuando grabas: ${proximos[0].titulo}"
            } else {
                "Tienes ${proximos.size} grabaciones entre hoy y mañana"
            }
            notificar(texto)
        }
        return Result.success()
    }

    private fun tienePermiso(): Boolean =
        Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

    private fun notificar(texto: String) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CANAL, "Recordatorios de grabación", NotificationManager.IMPORTANCE_DEFAULT)
        )
        val abrirApp = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Plusidea")
            .setContentText(texto)
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()
        manager.notify(1, notificacion)
    }

    private companion object { const val CANAL = "recordatorios" }
}