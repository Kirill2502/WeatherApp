package com.example.weatherapp.data.model

data class WeatherResultDTO(
    val current: DayItemDTO,
    val forecast: List<DayItemDTO>,
    val hours: List<DayItemDTO>
)