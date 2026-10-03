package com.example.weatherapp.presentation.fragments.HoursFragment

import com.example.weatherapp.domain.models.DayItem

data class HoursUiState(
    val hoursListData: List<DayItem> = emptyList(),
)