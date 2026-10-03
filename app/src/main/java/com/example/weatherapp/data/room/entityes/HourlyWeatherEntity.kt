package com.example.weatherapp.data.room.entityes

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "hourly_weather",
    primaryKeys = ["cityCoordinates", "hour"],
    foreignKeys = [
        ForeignKey(
            entity = CurrentWeatherEntity::class,
            parentColumns = ["cityCoordinates"],
            childColumns = ["cityCoordinates"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cityCoordinates")]
)
data class HourlyWeatherEntity(
    val cityCoordinates: String,
    val localName: String,
    val hour: String,        // это DTO.time для почасового элемента
    val condition: String,
    val imageUrl: String,
    val temp: String         // это DTO.currentTemp
)
