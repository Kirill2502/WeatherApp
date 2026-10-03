package com.example.weatherapp.data.room.entityes

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
@Entity(
    tableName = "forecast_days",
    primaryKeys = ["cityCoordinates","date"],
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
data class ForecastDayEntity(
    val cityCoordinates: String,
    val localName: String,
    val date: String,
    val condition: String,
    val imageUrl: String,
    val currentTemp: String,
    val maxTemp: String,
    val minTemp: String
)
