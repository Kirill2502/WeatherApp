package com.example.weatherapp.presentation.fragments.main

import androidx.annotation.StringRes

// Одноразовые эффекты (Toast, диалог, навигация) — не часть состояния экрана.
sealed interface MainFragmentUiEffect {
    // ViewModel не должна держать Context, поэтому передаём id строки,
    // а сам текст достаёт фрагмент через getString()
    data class ShowError(@StringRes val messageRes: Int) : MainFragmentUiEffect
    data object RequestLocationPermission : MainFragmentUiEffect
    data object ShowLocationDisabledDialog : MainFragmentUiEffect
}