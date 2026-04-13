package com.example.weatherapp.domain.UseCases

import com.example.weatherapp.domain.Repo.Repository
import com.example.weatherapp.domain.model.WeatherResult

class GetWeatherUseCase(
    private val repository: Repository
) {
    suspend operator fun invoke(city: String): WeatherResult {
        return repository.getWeather(city)
    }
}