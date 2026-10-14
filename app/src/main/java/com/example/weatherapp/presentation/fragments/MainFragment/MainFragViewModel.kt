package com.example.weatherapp.presentation.fragments.MainFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.domain.error.AppError
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.useCases.GetCurrentWeatherUseCase
import com.example.weatherapp.domain.useCases.RefreshWeatherUseCase
import com.example.weatherapp.presentation.holders.CityHolder
import com.example.weatherapp.presentation.holders.SelectedDayHolder
import com.example.weatherapp.presentation.mappers.toCardUi
import com.example.weatherapp.presentation.models.CurrentCardUi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase,
    private val selectedDayHolder: SelectedDayHolder,
    private val cityHolder: CityHolder
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()
    private var currentCity: String? = null
    private var collectJob: Job? = null
    private var todayWeather: CurrentWeather? = null

    init {
        viewModelScope.launch {
            selectedDayHolder.selectedDay.collect {
                updateCard()
            }
        }
    }


    fun onEvent(event: MainFragmentUiEvent) {
        when (event) {
            is MainFragmentUiEvent.LoadWeather -> {
                loadWeather(event.city)
            }

            is MainFragmentUiEvent.RefreshWeather -> {
                currentCity?.let { refresh(it) }//добавить обновление по свайпу вниз!
            }

            is MainFragmentUiEvent.LoadWeatherCoord -> {
                loadWeather(event.coords)
            }

            is MainFragmentUiEvent.ErrorShow -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun observeCurrent(city: String) {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            getCurrentWeatherUseCase(city).collect { current ->
                todayWeather = current
               _uiState.update { it.copy(city = city) }
                updateCard()

            }
        }
    }
    // Карточка: выбран день → показываем его, иначе — текущую погоду.
    // Название города берём из текущей погоды: в прогнозе по дням его нет.
    private fun updateCard() {
        val selectedDay = selectedDayHolder.selectedDay.value
        val card = when {
            selectedDay != null -> selectedDay.toCardUi(todayWeather?.cityName.orEmpty())
            todayWeather != null -> todayWeather!!.toCardUi()
            else -> CurrentCardUi()
        }
        _uiState.update { it.copy(currentCard = card) }
    }

    private fun refresh(city: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            refreshWeatherUseCase(city)
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.toUserMessage( )) }


                }
        }
    }

    private fun Throwable.toUserMessage(): String = when (this) {
        is AppError.NetworkError -> context.getString(R.string.error_network)
        is AppError.ParseError -> context.getString(R.string.error_parsing)
        is AppError.ServerError -> context.getString(R.string.error_server, code)
        is AppError.DataBaseError -> context.getString(R.string.error_database)
        is AppError.UnknownError -> context.getString(R.string.error_unknown)
        else -> message ?: context.getString(R.string.error_else)
    }


    // общая логика — и для города, и для координат
    private fun loadWeather(city: String) {
        currentCity = city
        cityHolder.set(city)
        observeCurrent(city)
        refresh(city)
    }


}