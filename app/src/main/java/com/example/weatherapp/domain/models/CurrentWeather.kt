package com.example.weatherapp.domain.models

import java.time.LocalDateTime
// Доменные модели описывают предметную область, а не экран:
// температуры — числа, даты — даты. Форматирование ("21°C") делает presentation.
data class CurrentWeather(
    val cityName: String,
    val updatedAt: LocalDateTime,
    val conditionText: String,
    val conditionCode: Int,
    val iconUrl: String,
    val tempC: Double,
    val maxTempC: Double?,
    val minTempC: Double?
)
