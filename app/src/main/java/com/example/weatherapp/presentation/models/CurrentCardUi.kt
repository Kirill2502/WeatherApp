package com.example.weatherapp.presentation.models

data class CurrentCardUi(
    val cityName: String = "",
    val dateText: String = "",
    val conditionText: String = "",
    val tempText: String = "",
    val maxMinText: String = "",
    // Для дня из прогноза нет текущей температуры: в tempText уже "макс/мин",
    // поэтому отдельную строку макс/мин скрываем
    val showMaxMin: Boolean = true,
    val iconUrl: String = ""
)
