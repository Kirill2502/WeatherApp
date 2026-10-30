package com.example.weatherapp.presentation.mappers

import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.DailyForecast
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class WeatherUiMapperTest {

    @Test
    fun `temperature is rounded without minus zero`() {
        assertEquals("21°C", 20.6.toTempText())
        assertEquals("0°C", (-0.4).toTempText())
        assertEquals("-5°C", (-5.2).toTempText())
    }

    @Test
    fun `current weather card shows current temp and max-min line`() {
        val card = CurrentWeather(
            cityName = "Москва",
            updatedAt = LocalDateTime.of(2026, 10, 4, 13, 30),
            conditionText = "Ясно",
            conditionCode = 1000,
            iconUrl = "https://icon.png",
            tempC = 11.3,
            maxTempC = 14.0,
            minTempC = 6.0
        ).toCardUi()

        assertEquals("11°C", card.tempText)
        assertEquals("14°C/6°C", card.maxMinText)
        assertEquals("04.10.2026 13:30", card.dateText)
        assertTrue(card.showMaxMin)
    }

    @Test
    fun `forecast day card shows max-min instead of current temp`() {
        val card = DailyForecast(
            date = LocalDate.of(2026, 10, 5),
            conditionText = "Дождь",
            conditionCode = 1183,
            iconUrl = "",
            maxTempC = 9.0,
            minTempC = 3.0
        ).toCardUi(cityName = "Москва")

        assertEquals("Москва", card.cityName)
        assertEquals("9°C/3°C", card.tempText)
        assertFalse(card.showMaxMin)
    }
}