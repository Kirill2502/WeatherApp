package com.example.weatherapp.data.room

import androidx.room.Embedded
import androidx.room.Relation
import com.example.weatherapp.data.room.entities.CurrentWeatherEntity
import com.example.weatherapp.data.room.entities.ForecastDayEntity
import com.example.weatherapp.data.room.entities.HourlyWeatherEntity

data class WeatherWithDetails(
    @Embedded
    val current: CurrentWeatherEntity,

    @Relation(parentColumn = "cityCoordinates", entityColumn = "cityCoordinates")
    val forecast: List<ForecastDayEntity>,

    @Relation(parentColumn = "cityCoordinates", entityColumn = "cityCoordinates")
    val hours: List<HourlyWeatherEntity>
)
