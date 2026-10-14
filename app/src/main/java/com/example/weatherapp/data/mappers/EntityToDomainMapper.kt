package com.example.weatherapp.data.mappers

import com.example.weatherapp.data.room.WeatherWithDetails
import com.example.weatherapp.data.room.entityes.CurrentWeatherEntity
import com.example.weatherapp.data.room.entityes.ForecastDayEntity
import com.example.weatherapp.data.room.entityes.HourlyWeatherEntity
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.DailyForecast
import com.example.weatherapp.domain.models.HourlyForecast
import com.example.weatherapp.domain.models.WeatherResult
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val API_DATE_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
fun WeatherWithDetails.toDomain(): WeatherResult {
    return WeatherResult(
        current = current.toDomain(),
        forecast = forecast.map { it.toDomain() }.sortedBy { it.date },
        hours = hours.map { it.toDomain() }.sortedBy { it.time }
    )

}

private fun CurrentWeatherEntity.toDomain() = CurrentWeather(

    cityName = cityName,
    updatedAt = LocalDateTime.parse(lastUpdated, API_DATE_TIME),
    conditionText = conditionText,
    conditionCode = conditionCode,
    iconUrl = iconUrl,
    tempC = tempC,
    maxTempC = maxTempC,
    minTempC = minTempC
)

private fun ForecastDayEntity.toDomain() = DailyForecast(

    date = LocalDate.parse(date),
    conditionText = conditionText,
    conditionCode = conditionCode,
    iconUrl = iconUrl,
    maxTempC = maxTempC,
    minTempC = minTempC
)

private fun HourlyWeatherEntity.toDomain() = HourlyForecast(

    time = LocalDateTime.parse(time, API_DATE_TIME),
    conditionText = conditionText,
    conditionCode = conditionCode,
    iconUrl = iconUrl,
    tempC = tempC
)
