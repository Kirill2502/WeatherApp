package com.example.weatherapp.data.repository

import com.example.weatherapp.data.mappers.toDomain
import com.example.weatherapp.data.mappers.toEntities
import com.example.weatherapp.data.remoteDataSource.RemoteData
import com.example.weatherapp.data.room.dao.WeatherDao
import com.example.weatherapp.domain.models.WeatherResult
import com.example.weatherapp.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImplement @Inject constructor(
    private val remoteData: RemoteData,
    private val weatherDao: WeatherDao
) : Repository {
    override fun getWeather(city: String): Flow<WeatherResult?> =
        weatherDao.observeWeather(city)
            .map { it?.toDomain() }


    override suspend fun refreshWeather(city: String) {
        try {
            android.util.Log.d("WeatherDebug", "refreshWeather: $city")
            val dto = remoteData.requestWeatherData(city)
            android.util.Log.d("WeatherDebug", "DTO получен")
            val entities = dto.toEntities(
                city = city,
                updatedAt = System.currentTimeMillis()
            )
            android.util.Log.d("WeatherDebug", "Entity созданы, current.city=${entities.current.cityCoordinates}")
            weatherDao.replaceWeather(
                current = entities.current,
                forecast = entities.forecast,
                hours = entities.hours
            )
            android.util.Log.d("WeatherDebug", "Записано в БД")

        } catch (e: Exception) {
            android.util.Log.d("WeatherDebug", "Ошибка: ${e.message}",e)
        }
    }


}