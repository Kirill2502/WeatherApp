package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.WeatherResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") city: String,
        @Query("days") days: Int = 3,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): WeatherResponseDTO
}
