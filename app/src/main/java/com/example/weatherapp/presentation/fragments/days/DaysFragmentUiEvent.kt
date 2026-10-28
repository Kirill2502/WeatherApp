package com.example.weatherapp.presentation.fragments.days

sealed interface DaysFragmentUiEvent {
    // Из UI приходит только id элемента — доменный объект ViewModel найдёт сама
    data class OnDaySelect(val itemId: String ) : DaysFragmentUiEvent

}