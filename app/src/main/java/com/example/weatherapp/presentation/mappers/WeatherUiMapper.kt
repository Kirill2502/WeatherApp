package com.example.weatherapp.presentation.mappers

import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.DailyForecast
import com.example.weatherapp.domain.models.HourlyForecast
import com.example.weatherapp.presentation.models.CurrentCardUi
import com.example.weatherapp.presentation.models.WeatherListItemUi
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// Всё форматирование для экрана живёт здесь, в presentation-слое
private val DATE_FORMATE: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val DATE_TIME_FORMATE: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
private val TIME_FORMATE: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

// roundToInt вместо "%.0f": не даёт "-0°C" и не зависит от локали
fun Double.toTempText(): String = "${roundToInt()}°C"

private fun maxMinText(max: Double?, min: Double?): String =
    if (max == null || min == null) "" else "${min.toTempText()}/${max.toTempText()}"

fun DailyForecast.toListItem() = WeatherListItemUi(
    id = date.toString(),
    title = date.format(DATE_FORMATE),
    conditionText = conditionText,
    tempText = maxMinText(maxTempC, minTempC),
    iconUrl = iconUrl
)

fun HourlyForecast.toListItem() = WeatherListItemUi(
    id = time.toString(),
    title = time.format(TIME_FORMATE),
    conditionText = conditionText,
    tempText = tempC.toTempText(),
    iconUrl = iconUrl
)

fun CurrentWeather.toCardUi() = CurrentCardUi(
    cityName = cityName,
    dateText = updatedAt.format(DATE_TIME_FORMATE),
    conditionText = conditionText,
    tempText = tempC.toTempText(),
    maxMinText = maxMinText(maxTempC, minTempC),
    showMaxMin = true,
    iconUrl = iconUrl
)

fun DailyForecast.toCardUi(cityName: String) = CurrentCardUi(
    cityName = cityName,
    dateText = date.format(DATE_FORMATE),
    conditionText = conditionText,
    tempText = maxMinText(maxTempC, minTempC),
    maxMinText = maxMinText(maxTempC, minTempC),
    showMaxMin = false,
    iconUrl = iconUrl
)




