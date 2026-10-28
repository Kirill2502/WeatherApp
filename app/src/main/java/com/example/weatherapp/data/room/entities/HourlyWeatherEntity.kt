package com.example.weatherapp.data.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "hourly_weather",
    primaryKeys = ["cityCoordinates", "time"],
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
    val time: String,             // "yyyy-MM-dd HH:mm"
    val conditionText: String,
    val conditionCode: Int,
    val iconUrl: String,
    val tempC: Double
)
