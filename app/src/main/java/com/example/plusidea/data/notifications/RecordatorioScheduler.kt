package com.example.plusidea.data.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordatorioScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun programar() {
        val peticion = PeriodicWorkRequestBuilder<RecordatorioWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NOMBRE, ExistingPeriodicWorkPolicy.UPDATE, peticion
        )
    }

    fun cancelar() {
        WorkManager.getInstance(context).cancelUniqueWork(NOMBRE)
    }

    private companion object { const val NOMBRE = "recordatorio_diario" }
}