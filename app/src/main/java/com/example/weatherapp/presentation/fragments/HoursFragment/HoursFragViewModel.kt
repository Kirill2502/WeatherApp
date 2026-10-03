package com.example.weatherapp.presentation.fragments.HoursFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.models.DayItem
import com.example.weatherapp.domain.useCases.GetHoursWeatherUseCase
import com.example.weatherapp.presentation.holders.CityHolder
import com.example.weatherapp.presentation.holders.SelectedDayHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HoursFragViewModel @Inject constructor(
    private val getHoursWeatherUseCase: GetHoursWeatherUseCase,
    private val cityHolder: CityHolder,
    private val selectedDayHolder: SelectedDayHolder,

    ) : ViewModel() {
    private val _uiState = MutableStateFlow(HoursUiState())
    val uiState = _uiState.asStateFlow()
    private var collectJob: Job? = null
    private var allHours: List<DayItem> = emptyList() // ← кэш ВСЕХ часов


    init {
        viewModelScope.launch {
            cityHolder.currentCity.collect { currentCity ->
                if (currentCity.isNotEmpty()) {
                    observeHours(currentCity)
                }

            }
        }
        viewModelScope.launch {
            selectedDayHolder.selectedDay.collect { day ->
                filterHoursByDay(day)

            }
        }
    }

    private fun filterHoursByDay(day: DayItem?) {
        val filtered = if (day == null) {
            // Ничего не выбрано → показываем первые 24 часа (сегодня)
            allHours.take(24)
        } else {
            // День выбран → фильтруем часы по дате
            allHours.filter { it.time.startsWith(day.time) }
        }
        _uiState.update { it.copy(hoursListData = filtered) }
    }

    private fun observeHours(city: String) {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            getHoursWeatherUseCase(city).collect { hours ->
                allHours = hours// ← сохранили
                filterHoursByDay(selectedDayHolder.selectedDay.value)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        collectJob?.cancel()
    }
}