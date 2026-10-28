package com.example.weatherapp.presentation.holders

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

//этот класс нужен что бы связать данные mainViewModel и daysViewModel(клик по айтему и передача данных в карточку)

@ActivityRetainedScoped
class SelectedDayHolder @Inject constructor() {
    private val _selectedDay = MutableStateFlow<LocalDate?>(null)
    val selectedDay = _selectedDay.asStateFlow()
    fun select(date: LocalDate) {
        _selectedDay.value = date
    }

    fun clear() {
        _selectedDay.value = null
    }
}