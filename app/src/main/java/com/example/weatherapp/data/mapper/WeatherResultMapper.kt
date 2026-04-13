package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.model.WeatherResultDTO
import com.example.weatherapp.domain.model.WeatherResult

fun WeatherResultDTO.toDomain(): WeatherResult {
    return WeatherResult(
        current = current.toDomain(),
        forecast = forecast.map { it.toDomain() },
        hours = hours.map { it.toDomain() }
    )
}