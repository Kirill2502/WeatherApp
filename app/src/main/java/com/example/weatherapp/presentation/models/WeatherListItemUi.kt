package com.example.weatherapp.presentation.models

import androidx.annotation.StringRes

// UI-модель: всё уже отформатировано, View просто кладёт строки в TextView
data class WeatherListItemUi(
    val id: String, // стабильный идентификатор для DiffUtil (дата или дата+час)
    @StringRes val titleRes: Int? = null, // <--- ID строки "Сегодня"/"Завтра" (или null)
    val title: String,                    // <--- Обычная дата ("04.10.2026") как fallbac
    val conditionText: String,
    val tempText: String,
    val iconUrl: String
)
