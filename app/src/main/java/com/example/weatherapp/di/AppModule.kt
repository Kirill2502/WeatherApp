package com.example.weatherapp.di

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.weatherapp.data.remote.WeatherParsing
import com.example.weatherapp.domain.Repo.Repository
import com.example.weatherapp.domain.UseCases.GetWeatherUseCase
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
    fun provideWeatherParsing(): WeatherParsing = WeatherParsing()

    @Provides
    @Singleton
    fun provideGetWeatherUseCase(repo: Repository): GetWeatherUseCase = GetWeatherUseCase(repo)

    @Provides
    @Singleton
    fun provideRequestQueue(@ApplicationContext context: Context): RequestQueue {
        return Volley.newRequestQueue(context)
    }
}

