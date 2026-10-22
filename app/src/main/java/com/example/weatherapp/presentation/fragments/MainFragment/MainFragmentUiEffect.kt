package com.example.weatherapp.presentation.fragments.MainFragment

// Одноразовые эффекты (Toast, диалог, навигация) — не часть состояния экрана.
sealed interface MainFragmentUiEffect {
    data class ShowError(val message: String): MainFragmentUiEffect
}