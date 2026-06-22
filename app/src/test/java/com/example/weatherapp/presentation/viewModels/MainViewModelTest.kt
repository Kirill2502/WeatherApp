package com.example.weatherapp.presentation.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.domain.UseCases.GetWeatherUseCase
import com.example.weatherapp.domain.model.DayItem
import com.example.weatherapp.domain.model.WeatherResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @get:Rule//нужно что бы убрать асинхронность работы лайв даты
    val instantExecutorRule = InstantTaskExecutorRule()
    private val useCase = mockk<GetWeatherUseCase>()

    @Test
    fun loadWeather_updatesLiveData() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val viewModel = MainViewModel(
            getWeatherUseCase = useCase,
            ioDispatcher = testDispatcher,
            mainDispatcher = testDispatcher
        )
        
        val currentDay = DayItem(
            city = "Moscow",
            time = "",
            condition = "",
            imageUrl = "",
            currentTemp = "",
            maxTemp = "",
            minTemp = "",
            hours = ""
        )
        val forecastItem = DayItem(
            city = "",
            time = "",
            condition = "test condition",
            imageUrl = "",
            currentTemp = "",
            maxTemp = "",
            minTemp = "",
            hours = ""
        )
        val hoursItem = DayItem(
            city = "",
            time = "",
            condition = "",
            imageUrl = "",
            currentTemp = "",
            maxTemp = "",
            minTemp = "",
            hours = "test hours"
        )
        val forecastList = listOf(forecastItem)
        val hoursList = listOf(hoursItem)
        val weatherResult = WeatherResult(
            currentDay, forecast = forecastList, hours = hoursList
        )
        coEvery {
            useCase("Moscow")
        } returns weatherResult

        viewModel.loadWeather("Moscow")
        advanceUntilIdle()
        assertEquals(currentDay, viewModel.liveDataCurrent.value)
        assertEquals(forecastList, viewModel.liveDataList.value)
        assertEquals(hoursList, viewModel.liveDataHours.value)

        coVerify(exactly = 1) {
            useCase("Moscow")
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}