package com.example.weatherapp.presentation.fragments.DaysFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.models.DailyForecast
import com.example.weatherapp.domain.useCases.GetForecastWeatherUseCase
import com.example.weatherapp.presentation.holders.CityHolder
import com.example.weatherapp.presentation.holders.SelectedDayHolder
import com.example.weatherapp.presentation.mappers.toListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DaysFragViewModel @Inject constructor(
    private val getForecastWeatherUseCase: GetForecastWeatherUseCase,
    private val selectedDayHolder: SelectedDayHolder,
    private val cityHolder: CityHolder

) : ViewModel() {
    private val _uiState = MutableStateFlow(DaysUiState())
    val uiState = _uiState.asStateFlow()

    private var collectJob: Job? = null
    private var forecast: List<DailyForecast> = emptyList()

    init {
        viewModelScope.launch {
            cityHolder.currentCity.collect { currentCity ->
                if (currentCity.isNotEmpty()) {
                    observeForecast(currentCity)
                }
            }
        }
    }


    fun onEvent(event: DaysFragmentUiEvent) {
        when (event) {
            is DaysFragmentUiEvent.OnDaySelect -> {
                val day = forecast.firstOrNull { it.date.toString() == event.itemId } ?: return
                val isToday = forecast.firstOrNull()?.date == day.date
                if (isToday) {
                    selectedDayHolder.clear()
                } else {
                    selectedDayHolder.select(day)
                }
            }


        }
    }

    private fun observeForecast(city: String) {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            getForecastWeatherUseCase(city).collect { days ->
                forecast = days
                _uiState.update {
                    it.copy(
                        forecastListData = days.map { day -> day.toListItem() }
                    )
                }
            }
        }
    }


}