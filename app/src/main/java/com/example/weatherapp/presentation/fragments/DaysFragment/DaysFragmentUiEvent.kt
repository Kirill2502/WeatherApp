package com.example.weatherapp.presentation.fragments.DaysFragment

import com.example.weatherapp.domain.models.DailyForecast
import com.example.weatherapp.presentation.models.WeatherListItemUi

sealed interface DaysFragmentUiEvent {
    // Из UI приходит только id элемента — доменный объект ViewModel найдёт сама
    data class OnDaySelect(val itemId: String ) : DaysFragmentUiEvent

}