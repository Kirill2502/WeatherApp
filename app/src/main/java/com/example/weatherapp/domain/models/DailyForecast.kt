package com.example.weatherapp.domain.models

import java.time.LocalDate

data class DailyForecast(
    val date: LocalDate,
    val conditionText: String,
    val conditionCode: Int,
    val iconUrl: String,
    val maxTempC: Double,
    val minTempC: Double
)
