package com.example.plusidea.data.di

import com.example.plusidea.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindProyectoRepository(impl: ProyectoRepositoryImpl): ProyectoRepository

    @Binds @Singleton
    abstract fun bindEmpresaRepository(impl: EmpresaRepositoryImpl): EmpresaRepository

    @Binds @Singleton
    abstract fun bindLocacionRepository(impl: LocacionRepositoryImpl): LocacionRepository

    @Binds @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}