package com.example.weatherapp.presentation.holders

import com.example.weatherapp.domain.models.DayItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

//этот класс нужен что бы связать данные mainViewModel и daysViewModel(клик по айтему и передача данных в карточку)

@Singleton
class SelectedDayHolder @Inject constructor() {
    private val _selectedDay = MutableStateFlow<DayItem?>(null)
    val selectedDay = _selectedDay.asStateFlow()
    fun select(day: DayItem) {
        _selectedDay.value = day
    }
}