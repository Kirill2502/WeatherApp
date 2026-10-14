package com.example.weatherapp.presentation.models

// UI-модель: всё уже отформатировано, View просто кладёт строки в TextView
data class WeatherListItemUi(
    val id: String,          // стабильный идентификатор для DiffUtil (дата или дата+час)
    val title: String,
    val conditionText: String,
    val tempText: String,
    val iconUrl: String
)
