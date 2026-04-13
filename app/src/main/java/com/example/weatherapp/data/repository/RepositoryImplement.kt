package com.example.weatherapp.data.repository

import com.example.weatherapp.data.mapper.toDomain
import com.example.weatherapp.data.remote.RemoteData
import com.example.weatherapp.domain.Repo.Repository
import com.example.weatherapp.domain.model.WeatherResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImplement @Inject constructor(
    private val remote: RemoteData
): Repository {
    override suspend fun getWeather(city: String): WeatherResult {
        val result = remote.requestWeatherData(city).toDomain()
        return result
    }

}