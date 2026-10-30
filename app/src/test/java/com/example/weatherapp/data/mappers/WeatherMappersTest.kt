package com.example.weatherapp.data.mappers


import com.example.weatherapp.data.models.ConditionDTO
import com.example.weatherapp.data.models.CurrentDTO
import com.example.weatherapp.data.models.DayDTO
import com.example.weatherapp.data.models.ForecastDTO
import com.example.weatherapp.data.models.ForecastDayDTO
import com.example.weatherapp.data.models.HourDTO
import com.example.weatherapp.data.models.LocationDTO
import com.example.weatherapp.data.models.WeatherResponseDTO
import com.example.weatherapp.data.room.WeatherWithDetails
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class WeatherMappersTest {

    private val condition = ConditionDTO(
        text = "Солнечно",
        icon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
        code = 1000
    )

    private val dto = WeatherResponseDTO(
        location = LocationDTO("Москва", "Moscow City", "Russia", "2026-10-04 13:45"),
        current = CurrentDTO(lastUpdated = "2026-10-04 13:30", tempC = 11.3, condition = condition),
        forecast = ForecastDTO(
            listOf(
                ForecastDayDTO(
                    date = "2026-10-05", // специально не по порядку — маппер должен отсортировать
                    day = DayDTO(maxTempC = 14.0, minTempC = 6.0, condition = condition),
                    hour = listOf(HourDTO("2026-10-05 00:00", 7.0, condition))
                ),
                ForecastDayDTO(
                    date = "2026-10-04",
                    day = DayDTO(maxTempC = 12.5, minTempC = 4.1, condition = condition),
                    hour = listOf(
                        HourDTO("2026-10-04 01:00", 5.0, condition),
                        HourDTO("2026-10-04 00:00", 4.5, condition)
                    )
                )
            )
        )
    )

    @Test
    fun `dto to entities keeps raw values and builds full icon url`() {
        val entities = dto.toEntities(city = "55.75,37.61", updatedAt = 123L)

        with(entities.current) {
            assertEquals("55.75,37.61", cityCoordinates)
            assertEquals("Москва", cityName) // кириллица не искажается
            assertEquals(123L, updatedAt)
            assertEquals(11.3, tempC, 0.0)
            assertEquals(1000, conditionCode)
            assertEquals("https://cdn.weatherapi.com/weather/64x64/day/113.png", iconUrl)
        }
        assertEquals(2, entities.forecast.size)
        assertEquals(3, entities.hours.size) // часы всех дней
    }

    @Test
    fun `entities to domain parses dates and sorts by time`() {
        val entities = dto.toEntities(city = "Moscow", updatedAt = 0L)
        val details = WeatherWithDetails(entities.current, entities.forecast, entities.hours)

        val result = details.toDomain()

        assertEquals(LocalDateTime.of(2026, 10, 4, 13, 30), result.current.updatedAt)
        assertEquals(
            listOf(LocalDate.of(2026, 10, 4), LocalDate.of(2026, 10, 5)),
            result.forecast.map { it.date }
        )
        assertEquals(
            listOf(
                LocalDateTime.of(2026, 10, 4, 0, 0),
                LocalDateTime.of(2026, 10, 4, 1, 0),
                LocalDateTime.of(2026, 10, 5, 0, 0)
            ),
            result.hours.map { it.time }
        )
    }
}