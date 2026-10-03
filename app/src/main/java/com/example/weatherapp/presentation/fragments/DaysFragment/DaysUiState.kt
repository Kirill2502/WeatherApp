package com.example.weatherapp.presentation.fragments.DaysFragment

import com.example.weatherapp.domain.models.DayItem

data class DaysUiState(
    val forecastListData: List<DayItem> = emptyList(),
)