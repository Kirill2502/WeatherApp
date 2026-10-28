package com.example.weatherapp.domain.useCases

import com.example.weatherapp.domain.models.DailyForecast
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetForecastWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(city: String): Flow<List<DailyForecast>> =
        repository.getWeather(city).map { it?.forecast ?: emptyList() }
}