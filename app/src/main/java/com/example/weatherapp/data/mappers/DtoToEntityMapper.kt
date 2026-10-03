package com.example.weatherapp.data.mappers

import com.example.weatherapp.data.models.WeatherResponseDTO
import com.example.weatherapp.data.room.entityes.CurrentWeatherEntity
import com.example.weatherapp.data.room.entityes.ForecastDayEntity
import com.example.weatherapp.data.room.entityes.HourlyWeatherEntity

data class WeatherEntities(
    val current: CurrentWeatherEntity,
    val forecast: List<ForecastDayEntity>,
    val hours: List<HourlyWeatherEntity>
)

fun WeatherResponseDTO.toEntities(city: String, updatedAt: Long): WeatherEntities {
    val cityKey = city

    // 1. Текущая погода
    val currentEntity = CurrentWeatherEntity(
        cityCoordinates = cityKey,
        localName = location.name,
        updatedAt = updatedAt,
        time = current.lastUpdated.fixEncoding(),
        condition = current.condition.text.fixEncoding(),
        imageUrl = current.condition.icon.fixEncoding(),
        currentTemp = formatTemp(current.tempC),
        maxTemp = formatTemp(forecast.forecastDay.firstOrNull()?.day?.maxTempC),
        minTemp = formatTemp(forecast.forecastDay.firstOrNull()?.day?.minTempC)
    )

    // 2. Прогноз по дням
    val forecastEntities = forecast.forecastDay.map { day ->
        ForecastDayEntity(
            cityCoordinates = cityKey,
            localName =location.name ,
            date = day.date.fixEncoding(),
            condition = day.day.condition.text.fixEncoding(),
            imageUrl = day.day.condition.icon.fixEncoding(),
            currentTemp = formatTemp(day.day.maxTempC),   // текущей за день нет, берём max
            maxTemp = formatTemp(day.day.maxTempC),
            minTemp = formatTemp(day.day.minTempC)
        )
    }

    // 3. Часы — берём часы из ПЕРВОГО дня (или всех, если нужно)
    val hourlyEntities = forecast.forecastDay
        .flatMap { it.hour }
        .map { hour ->
            HourlyWeatherEntity(
                cityCoordinates = cityKey,
                localName = location.name,
                hour = hour.time.fixEncoding(),
                condition = hour.condition.text.fixEncoding(),
                imageUrl = hour.condition.icon.fixEncoding(),
                temp = formatTemp(hour.tempC)
            )
        }

    return WeatherEntities(currentEntity, forecastEntities, hourlyEntities)
}

private fun formatTemp(value: Double?): String =
    if (value == null) "" else String.format("%.0f", value)

internal fun String.fixEncoding(): String =
    String(toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)


