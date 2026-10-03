package com.example.weatherapp.data.room.weatherDataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.data.room.dao.WeatherDao
import com.example.weatherapp.data.room.entityes.CurrentWeatherEntity
import com.example.weatherapp.data.room.entityes.ForecastDayEntity
import com.example.weatherapp.data.room.entityes.HourlyWeatherEntity

@Database(
    entities = [
        CurrentWeatherEntity::class,
        ForecastDayEntity::class,
        HourlyWeatherEntity::class],
    version = 2,
    exportSchema = false

)
abstract class WeatherDataBase: RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}