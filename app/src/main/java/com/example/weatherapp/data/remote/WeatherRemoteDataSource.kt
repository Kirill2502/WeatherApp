package com.example.weatherapp.data.remote

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.models.WeatherResponseDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRemoteDataSource @Inject constructor(
    private val weatherApi: WeatherApi,
) {

    suspend fun requestWeatherData(city: String): WeatherResponseDTO {
        android.util.Log.d("WeatherDebug", "Запрос в сеть: city=$city")
        val result = weatherApi.getForecast(apiKey = BuildConfig.WEATHER_API_KEY, city = city)
        return result
    }
}
