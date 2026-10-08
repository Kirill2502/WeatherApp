package com.example.weatherapp.domain.useCases

import com.example.weatherapp.domain.repository.Repository
import javax.inject.Inject

class RefreshWeatherUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(city: String): Result<Unit> =
        repository.refreshWeather(city)

}