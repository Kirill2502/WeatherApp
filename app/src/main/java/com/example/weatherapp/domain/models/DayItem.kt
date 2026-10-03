package com.example.weatherapp.domain.models

data class DayItem(
    val localName: String = "",//название города
    val cityCoordinates: String = "",// латитут и ланхитут
    val time: String = "",//Дата
    val condition: String = "",// состояние погоды(солнечно,пасмурно)
    val imageUrl: String = "",//адресс картинки
    val currentTemp: String = "", //текущая температура
    val maxTemp: String = "", //макс температура дня
    val minTemp: String = "", //мин температура дня
    val hours: String = "" //температура по часам
)