package com.example.weatherapp.presentation.viewModels

import com.example.weatherapp.domain.models.DailyForecast
import com.example.weatherapp.domain.useCases.GetForecastWeatherUseCase
import com.example.weatherapp.presentation.fragments.days.DaysFragmentUiEvent
import com.example.weatherapp.presentation.fragments.days.DaysFragViewModel
import com.example.weatherapp.presentation.holders.CityHolder
import com.example.weatherapp.presentation.holders.SelectedDayHolder
import com.example.weatherapp.presentation.mappers.toListItem
import com.example.weatherapp.presentation.models.WeatherListItemUi
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

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

        assertEquals(emptyList<WeatherListItemUi>(), viewModel.uiState.value.forecastListData)
    }

    @Test
    fun `when city is set, forecast is loaded from use case`() = runTest(testDispatcher) {
        val forecast = listOf(today, tomorrow)
        every { getForecastWeatherUseCase("Moscow") } returns flowOf(forecast)

        val viewModel = createViewModel()
        cityHolder.set("Moscow")
        advanceUntilIdle()

        assertEquals(forecast.map { it.toListItem() }, viewModel.uiState.value.forecastListData)
        verify(exactly = 1) { getForecastWeatherUseCase("Moscow") }
    }

    @Test
    fun `when OnDaySelect event, selected day is stored in holder`() = runTest(testDispatcher) {
        every { getForecastWeatherUseCase("Moscow") } returns flowOf(listOf(today, tomorrow))
        val viewModel = createViewModel()
        cityHolder.set("Moscow")
        advanceUntilIdle()

        viewModel.onEvent(DaysFragmentUiEvent.OnDaySelect(tomorrow.date.toString()))

        assertEquals(tomorrow.date, selectedDayHolder.selectedDay.value)
    }

    @Test
    fun `when today is selected, selection is cleared`() = runTest(testDispatcher) {
        every { getForecastWeatherUseCase("Moscow") } returns flowOf(listOf(today, tomorrow))
        val viewModel = createViewModel()
        cityHolder.set("Moscow")
        advanceUntilIdle()
        selectedDayHolder.select(tomorrow.date)

        viewModel.onEvent(DaysFragmentUiEvent.OnDaySelect(today.date.toString()))

        assertNull(selectedDayHolder.selectedDay.value)
    }

    private val today = DailyForecast(
        date = LocalDate.of(2026, 9, 22),
        conditionText = "Sunny",
        conditionCode = 1000,
        iconUrl = "https://cdn.weatherapi.com/sunny.png",
        maxTempC = 25.0,
        minTempC = 15.0
    )

    private val tomorrow = today.copy(date = LocalDate.of(2026, 9, 23), maxTempC = 22.0)
}