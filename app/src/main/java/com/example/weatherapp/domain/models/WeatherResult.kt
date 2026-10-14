package com.example.weatherapp.domain.models

data class WeatherResult(
    val current: CurrentWeather,
    val forecast: List<DailyForecast>,
    val hours: List<HourlyForecast>
)
