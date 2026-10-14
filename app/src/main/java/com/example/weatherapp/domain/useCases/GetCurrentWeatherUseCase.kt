package com.example.weatherapp.domain.useCases

import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// null — данных для города ещё нет в кэше (вместо "пустого" объекта-заглушки)
class GetCurrentWeatherUseCase @Inject constructor(private val repository: Repository) {
    operator fun invoke(city: String): Flow<CurrentWeather?> =
        repository.getWeather(city = city).map { it?.current }
}