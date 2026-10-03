package com.example.weatherapp.data.room

import androidx.room.Embedded
import androidx.room.Relation
import com.example.weatherapp.data.room.entityes.CurrentWeatherEntity
import com.example.weatherapp.data.room.entityes.ForecastDayEntity
import com.example.weatherapp.data.room.entityes.HourlyWeatherEntity

data class WeatherWithDetails(
    @Embedded
    val current: CurrentWeatherEntity,

    @Relation(parentColumn = "cityCoordinates", entityColumn = "cityCoordinates")
    val forecast: List<ForecastDayEntity>,

    @Relation(parentColumn = "cityCoordinates", entityColumn = "cityCoordinates")
    val hours: List<HourlyWeatherEntity>
)
