package com.example.weatherapp.presentation.fragments.main

sealed interface MainFragmentUiEvent {
    // Экран открыт. ViewModel сама решит, нужна ли первая загрузка по геолокации
    data object ScreenOpened : MainFragmentUiEvent
    data class LoadWeather(val city: String) : MainFragmentUiEvent
    data object RefreshWeather : MainFragmentUiEvent
    /**
     * @param askToEnableLocation false — когда вернулись из настроек: если геолокацию
     * так и не включили, не показываем диалог повторно
     */
    data class LoadWeatherByLocation(
        val askToEnableLocation: Boolean = true
    ) : MainFragmentUiEvent

}