package com.example.weatherapp.data.model

data class DayItemDTO(
    val city: String,//название города
    val time: String,//Дата
    val condition: String,// состояние погоды(солнечно,пасмурно)
    val imageUrl: String,//адресс картинки
    val currentTemp: String, //текущая температура
    val maxTemp: String, //макс температура дня
    val minTemp: String, //мин температура дня
    val hours: String //температура по часам
)