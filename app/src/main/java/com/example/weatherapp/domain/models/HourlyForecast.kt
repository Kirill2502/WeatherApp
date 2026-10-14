package com.example.weatherapp.domain.models

import java.time.LocalDateTime

data class HourlyForecast(
    val time: LocalDateTime,
    val conditionText: String,
    val conditionCode: Int,
    val iconUrl: String,
    val tempC: Double
)
