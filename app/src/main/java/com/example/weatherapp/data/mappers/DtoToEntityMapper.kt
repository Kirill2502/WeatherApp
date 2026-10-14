package com.example.weatherapp.data.mappers

import com.example.weatherapp.data.models.ConditionDTO
import com.example.weatherapp.data.models.WeatherResponseDTO
import com.example.weatherapp.data.room.entityes.CurrentWeatherEntity
import com.example.weatherapp.data.room.entityes.ForecastDayEntity
import com.example.weatherapp.data.room.entityes.HourlyWeatherEntity

data class WeatherEntities(
    val current: CurrentWeatherEntity,
    val forecast: List<ForecastDayEntity>,
    val hours: List<HourlyWeatherEntity>
)

// Маппер data-слоя только перекладывает данные. Никакого форматирования
// температур в строки — это забота presentation.
fun WeatherResponseDTO.toEntities(city: String, updatedAt: Long): WeatherEntities {
    val today = forecast.forecastDay.firstOrNull()?.day
    // 1. Текущая погода
    val currentEntity = CurrentWeatherEntity(
        cityCoordinates = city,
        cityName = location.name,
        updatedAt = updatedAt,
        lastUpdated = current.lastUpdated,
        conditionText = current.condition.text.fixEncoding(),
        conditionCode = current.condition.code,
        iconUrl = current.condition.toIconUrl(),
        tempC = current.tempC,
        maxTempC = today?.maxTempC,
        minTempC = today?.minTempC,

    )

    // 2. Прогноз по дням
    val forecastEntities = forecast.forecastDay.map { day ->
        ForecastDayEntity(
            cityCoordinates = city,
            date = day.date,
            conditionText = day.day.condition.text.fixEncoding(),
            conditionCode = day.day.condition.code,
            iconUrl = day.day.condition.toIconUrl(),
            maxTempC = day.day.maxTempC,
            minTempC = day.day.minTempC
        )
    }

    // 3. Часы — берём часы из ПЕРВОГО дня (или всех, если нужно)
    val hourlyEntities = forecast.forecastDay
        .flatMap { it.hour }
        .map { hour ->
            HourlyWeatherEntity(
                cityCoordinates = city,
                time = hour.time,
                conditionText = hour.condition.text.fixEncoding(),
                conditionCode = hour.condition.code,
                iconUrl = hour.condition.toIconUrl(),
                tempC = hour.tempC
            )
        }

    return WeatherEntities(currentEntity, forecastEntities, hourlyEntities)
}

// API отдаёт адрес без схемы ("//cdn.weatherapi.com/..."). Достраиваем полный URL здесь,
// чтобы остальные слои не знали об этой особенности API
private fun ConditionDTO.toIconUrl(): String =
    if (icon.startsWith("//")) "https:$icon" else icon
internal fun String.fixEncoding(): String =
    String(toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)


