package com.example.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.example.weatherapp.data.room.dao.WeatherDao
import com.example.weatherapp.data.room.weatherDataBase.WeatherDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module()
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherDataBase =
        Room.databaseBuilder(
            context,
            WeatherDataBase::class.java,
            "weather.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    @Singleton
    fun providesDao(db: WeatherDataBase): WeatherDao = db.weatherDao()

}
