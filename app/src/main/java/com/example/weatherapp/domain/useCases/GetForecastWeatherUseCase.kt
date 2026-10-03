package com.example.weatherapp.domain.useCases

import com.example.weatherapp.domain.models.DayItem
import com.example.weatherapp.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetForecastWeatherUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(city: String): Flow<List<DayItem>> =
        repository.getWeather(city).map { it?.forecast ?: emptyList() }
}