package com.example.weatherapp.presentation.fragments.MainFragment

sealed interface MainFragmentUiEvent {
    data class LoadWeather(val city: String) : MainFragmentUiEvent
    data object RefreshWeather: MainFragmentUiEvent
    data object ErrorShow: MainFragmentUiEvent
    data class LoadWeatherCoord(val coords: String) : MainFragmentUiEvent
}