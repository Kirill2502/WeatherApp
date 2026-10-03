package com.example.weatherapp.data.mappers

import com.example.weatherapp.data.room.WeatherWithDetails
import com.example.weatherapp.data.room.entityes.CurrentWeatherEntity
import com.example.weatherapp.data.room.entityes.ForecastDayEntity
import com.example.weatherapp.data.room.entityes.HourlyWeatherEntity
import com.example.weatherapp.domain.models.DayItem
import com.example.weatherapp.domain.models.WeatherResult

fun WeatherWithDetails.toDomain(): WeatherResult {
    return WeatherResult(
        current = current.toDayItem(),
        forecast = forecast.map { it.toDayItem() },
        hours = hours.map { it.toDayItem() }
    )

}

private fun CurrentWeatherEntity.toDayItem() = DayItem(
    cityCoordinates = cityCoordinates,
    localName = localName,
    time = time,
    condition = condition,
    imageUrl = imageUrl,
    currentTemp = currentTemp,
    maxTemp = maxTemp,
    minTemp = minTemp,
    hours = ""
)

private fun ForecastDayEntity.toDayItem() = DayItem(
    cityCoordinates = cityCoordinates,
    time = date,
    condition = condition,
    imageUrl = imageUrl,
    currentTemp = currentTemp,
    maxTemp = maxTemp,
    minTemp = minTemp,
    hours = ""
)

private fun HourlyWeatherEntity.toDayItem() = DayItem(
    cityCoordinates = cityCoordinates,
    time = hour,
    condition = condition,
    imageUrl = imageUrl,
    currentTemp = temp,
    maxTemp = "",
    minTemp = "",
    hours = ""
)
