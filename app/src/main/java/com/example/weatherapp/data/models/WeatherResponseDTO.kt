package com.example.weatherapp.data.models

import com.google.gson.annotations.SerializedName

data class WeatherResponseDTO(
    @SerializedName("location") val location: LocationDTO,
    @SerializedName("current") val current: CurrentDTO,
    @SerializedName("forecast") val forecast: ForecastDTO
)

data class LocationDTO(
    @SerializedName("name") val name: String,
    @SerializedName("region") val region: String,
    @SerializedName("country") val country: String,
    @SerializedName("localtime") val localtime: String
)

data class CurrentDTO(
    @SerializedName("last_updated") val lastUpdated: String,
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("condition") val condition: ConditionDTO
)

data class ConditionDTO(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("code") val code: Int
)

data class ForecastDTO(
    @SerializedName("forecastday") val forecastDay: List<ForecastDayDTO>
)

data class ForecastDayDTO(
    @SerializedName("date") val date: String,
    @SerializedName("day") val day: DayDTO,
    @SerializedName("hour") val hour: List<HourDTO>
)

data class DayDTO(
    @SerializedName("maxtemp_c") val maxTempC: Double,
    @SerializedName("mintemp_c") val minTempC: Double,
    @SerializedName("condition") val condition: ConditionDTO
)

data class HourDTO(
    @SerializedName("time") val time: String,
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("condition") val condition: ConditionDTO
)