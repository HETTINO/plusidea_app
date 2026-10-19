package com.example.plusidea.data.di

import android.content.Context
import androidx.room.Room
import com.example.plusidea.data.local.dao.EmpresaDao
import com.example.plusidea.data.local.dao.LocacionDao
import com.example.plusidea.data.local.dao.ProyectoDao
import com.example.plusidea.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "plusidea_database"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideProyectoDao(database: AppDatabase): ProyectoDao = database.proyectoDao()

    @Provides
    fun provideEmpresaDao(database: AppDatabase): EmpresaDao = database.empresaDao()

    @Provides
    fun provideLocacionDao(database: AppDatabase): LocacionDao = database.locacionDao()
}