package com.example.weatherapp.presentation.fragments.main

import com.example.weatherapp.R
import com.example.weatherapp.domain.location.Coordinates
import com.example.weatherapp.domain.location.LocationResult
import com.example.weatherapp.domain.location.LocationTracker
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.useCases.GetCurrentWeatherUseCase
import com.example.weatherapp.domain.useCases.GetForecastWeatherUseCase
import com.example.weatherapp.domain.useCases.RefreshWeatherUseCase
import com.example.weatherapp.presentation.holders.CityHolder
import com.example.weatherapp.presentation.holders.SelectedDayHolder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class MainFragViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getCurrentWeatherUseCase = mockk<GetCurrentWeatherUseCase>()
    private val getForecastWeatherUseCase = mockk<GetForecastWeatherUseCase>()
    private val refreshWeatherUseCase = mockk<RefreshWeatherUseCase>()
    private val locationTracker = mockk<LocationTracker>()
    private val cityHolder = CityHolder()
    private val selectedDayHolder = SelectedDayHolder()

    private val current = CurrentWeather(
        cityName = "Москва",
        updatedAt = LocalDateTime.of(2026, 10, 4, 13, 30),
        conditionText = "Ясно",
        conditionCode = 1000,
        iconUrl = "https://icon.png",
        tempC = 11.0,
        maxTempC = 14.0,
        minTempC = 6.0
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getCurrentWeatherUseCase(any()) } returns flowOf(current)
        every { getForecastWeatherUseCase(any()) } returns flowOf(emptyList())
        coEvery { refreshWeatherUseCase(any()) } returns Result.success(Unit)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = MainFragViewModel(
        getCurrentWeatherUseCase = getCurrentWeatherUseCase,
        getForecastWeatherUseCase = getForecastWeatherUseCase,
        refreshWeatherUseCase = refreshWeatherUseCase,
        selectedDayHolder = selectedDayHolder,
        cityHolder = cityHolder,
        locationTracker = locationTracker
    )

    @Test
    fun `LoadWeather - sets city, refreshes and shows current weather card`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.onEvent(MainFragmentUiEvent.LoadWeather("Moscow"))
        advanceUntilIdle()

        assertEquals("Moscow", cityHolder.currentCity.value)
        assertEquals("Москва", viewModel.uiState.value.currentCard.cityName)
        assertEquals("11°C", viewModel.uiState.value.currentCard.tempText)
        assertFalse(viewModel.uiState.value.isRefreshing)
        coVerify(exactly = 1) { refreshWeatherUseCase("Moscow") }
    }

    @Test
    fun `refresh without internet - sends ShowError effect`() = runTest(testDispatcher) {
        coEvery { refreshWeatherUseCase("Moscow") } returns Result.failure(IOException())
        val viewModel = createViewModel()

        viewModel.onEvent(MainFragmentUiEvent.LoadWeather("Moscow"))
        advanceUntilIdle()

        assertEquals(
            MainFragmentUiEffect.ShowError(R.string.error_network),
            viewModel.effects.first()
        )
    }

    @Test
    fun `ScreenOpened with location - loads weather by coordinates only once`() = runTest(testDispatcher) {
        coEvery { locationTracker.getCurrentLocation() } returns
                LocationResult.Success(Coordinates(55.75, 37.61))
        val viewModel = createViewModel()

        viewModel.onEvent(MainFragmentUiEvent.ScreenOpened)
        advanceUntilIdle()
        viewModel.onEvent(MainFragmentUiEvent.ScreenOpened) // например, после поворота экрана
        advanceUntilIdle()

        assertEquals("55.75,37.61", cityHolder.currentCity.value)
        coVerify(exactly = 1) { locationTracker.getCurrentLocation() }
    }

    @Test
    fun `no location permission - asks fragment to request it`() = runTest(testDispatcher) {
        coEvery { locationTracker.getCurrentLocation() } returns LocationResult.PermissionDenied
        val viewModel = createViewModel()

        viewModel.onEvent(MainFragmentUiEvent.ScreenOpened)
        advanceUntilIdle()

        assertEquals(MainFragmentUiEffect.RequestLocationPermission, viewModel.effects.first())
    }
}