package com.example.weatherapp.presentation.fragments.DaysFragment

import com.example.weatherapp.domain.models.DayItem

sealed interface DaysFragmentUiEvent {
    data class OnDaySelect(val dayItem: DayItem): DaysFragmentUiEvent

}