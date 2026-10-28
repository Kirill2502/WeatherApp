package com.example.weatherapp.presentation.fragments.hours

import com.example.weatherapp.presentation.models.WeatherListItemUi

data class HoursUiState(
    val hoursListData: List<WeatherListItemUi> = emptyList(),
)