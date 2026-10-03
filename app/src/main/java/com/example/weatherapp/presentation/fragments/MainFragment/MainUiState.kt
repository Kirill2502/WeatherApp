package com.example.weatherapp.presentation.fragments.MainFragment

import com.example.weatherapp.domain.models.DayItem


data class MainUiState(
    val currentData: DayItem = DayItem(),
    val city: String = "",
    val isRefreshing: Boolean = false,
    val error: String? = null,
)
