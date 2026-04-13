package com.example.weatherapp.domain.Repo

import com.example.weatherapp.domain.model.WeatherResult

interface Repository {
    suspend fun getWeather(city: String): WeatherResult
}