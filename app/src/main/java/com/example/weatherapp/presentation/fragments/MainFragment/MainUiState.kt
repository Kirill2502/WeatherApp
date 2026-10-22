package com.example.weatherapp.presentation.fragments.MainFragment

import com.example.weatherapp.presentation.models.CurrentCardUi


data class MainUiState(
    val currentCard: CurrentCardUi = CurrentCardUi(),
    val city: String = "",
    val isRefreshing: Boolean = false,
)
