package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.models.WeatherResult
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getWeather(city: String): Flow<WeatherResult?>
    suspend fun refreshWeather(city: String)
}