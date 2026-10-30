package com.example.weatherapp.presentation.fragments.days

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.models.DailyForecast
import com.example.weatherapp.domain.useCases.GetCurrentWeatherUseCase
import com.example.weatherapp.domain.useCases.GetForecastWeatherUseCase
import com.example.weatherapp.presentation.holders.CityHolder
import com.example.weatherapp.presentation.holders.SelectedDayHolder
import com.example.weatherapp.presentation.mappers.toListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DaysFragViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastWeatherUseCase: GetForecastWeatherUseCase,
    private val selectedDayHolder: SelectedDayHolder,
    private val cityHolder: CityHolder

) : ViewModel() {
    private val _uiState = MutableStateFlow(DaysUiState())
    val uiState = _uiState.asStateFlow()
    private var forecast: List<DailyForecast> = emptyList()

    init {
        observeForecast()
    }


    fun onEvent(event: DaysFragmentUiEvent) {
        when (event) {
            is DaysFragmentUiEvent.OnDaySelect -> {
                val day = forecast.firstOrNull { it.date.toString() == event.itemId } ?: return
                val isToday = forecast.firstOrNull()?.date == day.date
                if (isToday) {
                    selectedDayHolder.clear()
                } else {
                    selectedDayHolder.select(day.date)
                }
            }


        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeForecast() {
        viewModelScope.launch {
            cityHolder.currentCity
                .filter { it.isNotEmpty() }// Игнорируем пустой город при старте
                .flatMapLatest { city ->
                    // Склеиваем текущую погоду и прогноз, чтобы взять дату города
                    combine(
                        getCurrentWeatherUseCase(city),
                        getForecastWeatherUseCase(city)
                    ) { current, days ->
                        forecast = days// Сохраняем для обработки кликов
                        val today = current?.updatedAt?.toLocalDate() // <--- Берем дату города!
                        days.map { day -> day.toListItem(today) } // <--- Передаем её в маппер

                    }

                }.collect { uiList ->
                    _uiState.update { it.copy(forecastListData = uiList) }

                }

        }
    }


}