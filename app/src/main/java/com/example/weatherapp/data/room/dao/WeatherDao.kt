package com.example.weatherapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.weatherapp.data.room.WeatherWithDetails
import com.example.weatherapp.data.room.entityes.CurrentWeatherEntity
import com.example.weatherapp.data.room.entityes.ForecastDayEntity
import com.example.weatherapp.data.room.entityes.HourlyWeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Transaction
    @Query("SELECT * FROM current_weather WHERE cityCoordinates = :city LIMIT 1")
    fun observeWeather(city: String): Flow<WeatherWithDetails?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrent(current: CurrentWeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: List<ForecastDayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHours(hours: List<HourlyWeatherEntity>)

    @Query("DELETE FROM forecast_days WHERE cityCoordinates = :city ")
    suspend fun clearForecast(city: String)

    @Query("DELETE FROM hourly_weather WHERE cityCoordinates = :city")
    suspend fun clearHours(city: String)

    @Transaction
    suspend fun replaceWeather(
        current: CurrentWeatherEntity,
        forecast: List<ForecastDayEntity>,
        hours: List<HourlyWeatherEntity>
    ) {
        clearForecast(current.cityCoordinates)
        clearHours(current.cityCoordinates)
        insertCurrent(current)
        insertForecast(forecast)
        insertHours(hours)

    }


}