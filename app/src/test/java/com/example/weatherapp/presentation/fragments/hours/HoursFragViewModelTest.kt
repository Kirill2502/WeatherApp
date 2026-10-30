package com.example.weatherapp.presentation.fragments.hours

import com.example.weatherapp.domain.models.HourlyForecast
import com.example.weatherapp.domain.useCases.GetHoursForDayUseCase
import com.example.weatherapp.presentation.holders.CityHolder
import com.example.weatherapp.presentation.holders.SelectedDayHolder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HoursFragViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val getHoursForDayUseCase = mockk<GetHoursForDayUseCase>()
    private val cityHolder = CityHolder()
    private val selectedDayHolder = SelectedDayHolder()

    private val tomorrow = LocalDate.of(2026, 10, 5)

    private fun hour(time: LocalDateTime) = HourlyForecast(time, "Ясно", 1000, "", 10.0)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = HoursFragViewModel(
        getHoursForDayUseCase = getHoursForDayUseCase,
        cityHolder = cityHolder,
        selectedDayHolder = selectedDayHolder
    )

    @Test
    fun `selected day is passed to use case and shown as HH-mm items`() = runTest(testDispatcher) {
        every { getHoursForDayUseCase("Moscow", null) } returns
                flowOf(listOf(hour(LocalDateTime.of(2026, 10, 4, 14, 0))))
        every { getHoursForDayUseCase("Moscow", tomorrow) } returns
                flowOf(listOf(hour(tomorrow.atTime(9, 0))))
        val viewModel = createViewModel()
        // uiState построен через stateIn(WhileSubscribed) — нужен подписчик
        backgroundScope.launch { viewModel.uiState.collect {} }

        cityHolder.set("Moscow")
        assertEquals(listOf("14:00"), viewModel.uiState.value.hoursListData.map { it.title })

        selectedDayHolder.select(tomorrow)
        assertEquals(listOf("09:00"), viewModel.uiState.value.hoursListData.map { it.title })
    }

    @Test
    fun `empty city - use case is not called`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect {} }

        assertEquals(emptyList<Any>(), viewModel.uiState.value.hoursListData)
        verify(exactly = 0) { getHoursForDayUseCase(any(), any()) }
    }
}