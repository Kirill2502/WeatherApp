package com.example.weatherapp.presentation.fragments.DaysFragment

import com.example.weatherapp.presentation.models.WeatherListItemUi

data class DaysUiState(
    val forecastListData: List<WeatherListItemUi> = emptyList(),
)