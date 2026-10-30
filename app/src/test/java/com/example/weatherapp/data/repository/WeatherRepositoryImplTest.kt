package com.example.weatherapp.data.repository

import com.example.weatherapp.data.models.ConditionDTO
import com.example.weatherapp.data.models.CurrentDTO
import com.example.weatherapp.data.models.ForecastDTO
import com.example.weatherapp.data.models.LocationDTO
import com.example.weatherapp.data.models.WeatherResponseDTO
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.room.dao.WeatherDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class WeatherRepositoryImplTest {

    private val remote = mockk<WeatherRemoteDataSource>()
    private val dao = mockk<WeatherDao>()
    private val repository = WeatherRepositoryImpl(remote, dao)

    private val dto = WeatherResponseDTO(
        location = LocationDTO("Moscow", "Moscow City", "Russia", "2026-10-04 13:45"),
        current = CurrentDTO("2026-10-04 13:30", 11.0, ConditionDTO("Ясно", "//icon.png", 1000)),
        forecast = ForecastDTO(emptyList())
    )

    @Test
    fun `refresh success - saves to database and returns success`() = runTest {
        coEvery { remote.requestWeatherData("Moscow") } returns dto
        coEvery { dao.replaceWeather(any(), any(), any()) } just runs

        val result = repository.refreshWeather("Moscow")

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { dao.replaceWeather(match { it.cityCoordinates == "Moscow" }, any(), any()) }
    }

    @Test
    fun `refresh network error - returns failure instead of swallowing it`() = runTest {
        coEvery { remote.requestWeatherData("Moscow") } throws IOException("no internet")

        val result = repository.refreshWeather("Moscow")

        assertTrue(result.exceptionOrNull() is IOException)
        coVerify(exactly = 0) { dao.replaceWeather(any(), any(), any()) }
    }

    @Test(expected = CancellationException::class)
    fun `refresh cancelled - cancellation is rethrown`() = runTest {
        coEvery { remote.requestWeatherData("Moscow") } throws CancellationException("cancelled")

        repository.refreshWeather("Moscow")
    }
}