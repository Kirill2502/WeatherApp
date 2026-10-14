package com.example.weatherapp.data.room.entityes

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "forecast_days",
    primaryKeys = ["cityCoordinates", "date"],
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
    val date: String,
    val conditionText: String,
    val conditionCode: Int,
    val iconUrl: String,
    val maxTempC: Double,
    val minTempC: Double
)
