package com.example.weatherapp.data.room.entityes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey val cityCoordinates: String,// ключ по координатам
    val localName: String,// Название города
    val updatedAt: Long,
    val time: String,//Дата
    val condition: String,// состояние погоды(солнечно,пасмурно)
    val imageUrl: String,//адресс картинки
    val currentTemp: String, //текущая температура
    val maxTemp: String, //макс температура дня
    val minTemp: String, //мин температура дня

)