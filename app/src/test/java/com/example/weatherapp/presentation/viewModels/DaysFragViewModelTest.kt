package com.example.weatherapp.presentation.viewModels

import com.example.weatherapp.domain.models.DayItem
import com.example.weatherapp.domain.useCases.GetForecastWeatherUseCase
import com.example.weatherapp.presentation.fragments.DaysFragment.DaysFragmentUiEvent
import com.example.weatherapp.presentation.fragments.DaysFragment.DaysFragViewModel
import com.example.weatherapp.presentation.holders.CityHolder
import com.example.weatherapp.presentation.holders.SelectedDayHolder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DaysFragViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getForecastWeatherUseCase = mockk<GetForecastWeatherUseCase>()
    private lateinit var cityHolder: CityHolder
    private lateinit var selectedDayHolder: SelectedDayHolder

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        cityHolder = CityHolder()
        selectedDayHolder = SelectedDayHolder()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = DaysFragViewModel(
        getForecastWeatherUseCase = getForecastWeatherUseCase,
        selectedDayHolder = selectedDayHolder,
        cityHolder = cityHolder
    )

    @Test
    fun `initial state has empty forecast list`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(emptyList<DayItem>(), viewModel.uiState.value.forecastListData)
    }

    @Test
    fun `when city is set, forecast is loaded from use case`() = runTest(testDispatcher) {
        val forecast = listOf(forecastItem)
        every { getForecastWeatherUseCase("Moscow") } returns flowOf(forecast)

        val viewModel = createViewModel()
        cityHolder.set("Moscow")
        advanceUntilIdle()

        assertEquals(forecast, viewModel.uiState.value.forecastListData)
        verify(exactly = 1) { getForecastWeatherUseCase("Moscow") }
    }

    @Test
    fun `when OnDaySelect event, selected day is stored in holder`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.onEvent(DaysFragmentUiEvent.OnDaySelect(forecastItem))

        assertEquals(forecastItem, selectedDayHolder.selectedDay.value)
    }

    private val forecastItem = DayItem(
        cityCoordinates = "Moscow",
        time = "2026-09-22",
        condition = "Sunny",
        imageUrl = "//cdn.weatherapi.com/...",
        currentTemp = "20",
        maxTemp = "25",
        minTemp = "15",
        hours = ""
    )
}