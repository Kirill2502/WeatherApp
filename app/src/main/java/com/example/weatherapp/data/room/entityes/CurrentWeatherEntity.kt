package com.example.weatherapp.data.room.entityes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey val cityCoordinates: String,// // ключ запроса: координаты или название города
    val cityName: String,// Название города
    val updatedAt: Long,                       // System.currentTimeMillis()
    val lastUpdated: String,                   // ← ISO-строка "yyyy-MM-dd HH:mm"
    val conditionText: String,
    val conditionCode: Int,                    // ← код
    val iconUrl: String,                       // ← ПОЛНЫЙ URL
    val tempC: Double,                         // ← Double, не String
    val maxTempC: Double?,
    val minTempC: Double?

)