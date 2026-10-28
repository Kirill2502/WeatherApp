package com.example.weatherapp.domain.useCases

import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.HourlyForecast
import com.example.weatherapp.domain.models.WeatherResult
import com.example.weatherapp.domain.repository.WeatherRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class GetHoursForDayUseCaseTest {

    private val repository = mockk<WeatherRepository>()
    private val useCase = GetHoursForDayUseCase(repository)

    private val day1 = LocalDate.of(2026, 10, 4)
    private val day2 = day1.plusDays(1)
    private val day3 = day1.plusDays(2)

    // 3 дня по 24 часа, как отдаёт API (days=3)
    private val allHours = listOf(day1, day2, day3).flatMap { date ->
        (0 until 24).map { h -> hour(date.atTime(h, 0)) }
    }

    private fun hour(time: LocalDateTime) = HourlyForecast(
        time = time,
        conditionText = "Sunny",
        conditionCode = 1000,
        iconUrl = "",
        tempC = 20.0
    )

    private fun weather(updatedAt: LocalDateTime) = WeatherResult(
        current = CurrentWeather(
            cityName = "Moscow",
            updatedAt = updatedAt,
            conditionText = "Sunny",
            conditionCode = 1000,
            iconUrl = "",
            tempC = 20.0,
            maxTempC = 25.0,
            minTempC = 15.0
        ),
        forecast = emptyList(),
        hours = allHours
    )

    @Test
    fun `no selected day - returns next 24 hours from city local time`() = runTest {
        every { repository.getWeather("Moscow") } returns
                flowOf(weather(updatedAt = day1.atTime(13, 45)))

        val result = useCase("Moscow", date = null).first()

        assertEquals(24, result.size)
        assertEquals(day1.atTime(13, 0), result.first().time) // текущий час включительно
        assertEquals(day2.atTime(12, 0), result.last().time)
    }

    @Test
    fun `selected day - returns only hours of that day`() = runTest {
        every { repository.getWeather("Moscow") } returns
                flowOf(weather(updatedAt = day1.atTime(13, 45)))

        val result = useCase("Moscow", date = day2).first()

        assertEquals(24, result.size)
        assertTrue(result.all { it.time.toLocalDate() == day2 })
    }

    @Test
    fun `no cached data - returns empty list`() = runTest {
        every { repository.getWeather("Moscow") } returns flowOf(null)

        val result = useCase("Moscow", date = null).first()

        assertTrue(result.isEmpty())
    }
}