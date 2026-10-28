package com.example.weatherapp.presentation.fragments.days

import com.example.weatherapp.presentation.models.WeatherListItemUi

data class DaysUiState(
    val forecastListData: List<WeatherListItemUi> = emptyList(),
)