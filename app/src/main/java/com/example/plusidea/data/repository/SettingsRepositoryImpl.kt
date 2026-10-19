package com.example.plusidea.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.plusidea.data.notifications.RecordatorioScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val scheduler: RecordatorioScheduler
) : SettingsRepository {

    private val claveTema = booleanPreferencesKey("tema_oscuro")
    private val claveNotificaciones = booleanPreferencesKey("notificaciones")

    override val temaOscuro: Flow<Boolean?> = dataStore.data.map { it[claveTema] }
    override val notificaciones: Flow<Boolean> = dataStore.data.map { it[claveNotificaciones] ?: false }

    override suspend fun setTemaOscuro(oscuro: Boolean) {
        dataStore.edit { it[claveTema] = oscuro }
    }

    override suspend fun setNotificaciones(activadas: Boolean) {
        dataStore.edit { it[claveNotificaciones] = activadas }
        if (activadas) scheduler.programar() else scheduler.cancelar()
    }
}