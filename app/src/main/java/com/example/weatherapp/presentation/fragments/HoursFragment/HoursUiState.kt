package com.example.weatherapp.presentation.fragments.HoursFragment

import com.example.weatherapp.presentation.models.WeatherListItemUi

data class HoursUiState(
    val hoursListData: List<WeatherListItemUi> = emptyList(),
)