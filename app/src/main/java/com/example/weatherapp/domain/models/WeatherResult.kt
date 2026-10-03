package com.example.weatherapp.domain.models

data class WeatherResult(
    val current: DayItem,
    val forecast: List<DayItem>,
    val hours: List<DayItem>
)
