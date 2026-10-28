package com.example.weatherapp.presentation.fragments.main

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.domain.error.AppError
import com.example.weatherapp.domain.location.LocationResult
import com.example.weatherapp.domain.location.LocationTracker
import com.example.weatherapp.domain.useCases.GetCurrentWeatherUseCase
import com.example.weatherapp.domain.useCases.GetForecastWeatherUseCase
import com.example.weatherapp.domain.useCases.RefreshWeatherUseCase
import com.example.weatherapp.presentation.holders.CityHolder
import com.example.weatherapp.presentation.holders.SelectedDayHolder
import com.example.weatherapp.presentation.mappers.toCardUi
import com.example.weatherapp.presentation.models.CurrentCardUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastWeatherUseCase: GetForecastWeatherUseCase,// <--- НУЖЕН, ЧТОБЫ ИСКАТЬ ДЕНЬ ПО ДАТЕ
    private val refreshWeatherUseCase: RefreshWeatherUseCase,
    private val selectedDayHolder: SelectedDayHolder,
    private val cityHolder: CityHolder,
    private val locationTracker: LocationTracker

) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    // Channel доставит каждый эффект ровно один раз.
// Если хранить ошибку в состоянии, после поворота экрана Toast показался бы снова.
    private val _effects = Channel<MainFragmentUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    private var currentCity: String? = null

    //Это защита от повторного запроса GPS при повороте экрана.
    private var initialLoadRequested = false


    fun onEvent(event: MainFragmentUiEvent) {
        when (event) {
            is MainFragmentUiEvent.ScreenOpened -> {
                // Геолокацию запрашиваем только один раз — при первом открытии экрана
                if (!initialLoadRequested) {
                    initialLoadRequested = true
                    loadWeatherByLocation(askToEnableLocation = true)
                }
            }

            is MainFragmentUiEvent.LoadWeather -> {
                loadWeather(event.city)
            }

            is MainFragmentUiEvent.RefreshWeather -> {
                // Город ещё не выбран (например, не было разрешения) — пробуем геолокацию
                currentCity?.let { refresh(it) }
                    ?: loadWeatherByLocation(askToEnableLocation = true)
            }
            //Это событие прилетает, когда пользователь нажал на кнопку синхронизации,
            // или вернулся из настроек, или только что выдал разрешение.
            is MainFragmentUiEvent.LoadWeatherByLocation -> {
                loadWeatherByLocation(event.askToEnableLocation)
            }


        }
    }

    private fun loadWeatherByLocation(askToEnableLocation: Boolean) {
        viewModelScope.launch {
            when (val result = locationTracker.getCurrentLocation()) {
                is LocationResult.Success -> loadWeather(result.coordinates.toQuery())

                LocationResult.PermissionDenied ->
                    _effects.send(MainFragmentUiEffect.RequestLocationPermission)

                LocationResult.LocationDisabled ->
                    if (askToEnableLocation) {
                        _effects.send(MainFragmentUiEffect.ShowLocationDisabledDialog)
                    }

                LocationResult.Error ->
                    _effects.send(
                        MainFragmentUiEffect.ShowError(
                            R.string.error_location
                        )
                    )
            }

        }
    }

    private fun observeWeather(city: String) {
        viewModelScope.launch {
            // combine следит за тремя потоками одновременно
            combine(
                getCurrentWeatherUseCase(city),// 1. Погода сейчас
                getForecastWeatherUseCase(city),// 2. Список дней
                selectedDayHolder.selectedDay// 3. Выбранная дата
            ) { current, forecast, selectedDate ->
                // Если дата выбрана, ищем этот день в прогнозе (совпадает ли дата)
                val selectedDay = forecast.firstOrNull { it.date == selectedDate }
                // Собираем карточку в зависимости от того, что есть
                val card = when {
                    // Если день выбран — берем данные дня, а имя города из текущей погоды
                    selectedDay != null -> selectedDay.toCardUi(current?.cityName.orEmpty())
                    // Если день не выбран, но есть текущая погода — показываем её
                    current != null -> current.toCardUi()
                    // Если вообще ничего нет — пустая карточка
                    else -> CurrentCardUi()
                }
                card // Возвращаем готовую карточку
            }.collect { card ->
                // Обновляем экран
                _uiState.update { it.copy(currentCard = card, city = city) }
            }
        }


    }


    private fun refresh(city: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                val result = refreshWeatherUseCase(city)
                result.onFailure { e ->
                    _effects.send(MainFragmentUiEffect.ShowError(e.toUserMessage()))
                }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }


        }
    }

    @StringRes
    private fun Throwable.toUserMessage(): Int = when (this) {
        is AppError.NetworkError -> R.string.error_network
        is AppError.ParseError -> R.string.error_parsing
        is AppError.ServerError -> R.string.error_server
        is AppError.DataBaseError -> R.string.error_database
        is AppError.UnknownError -> R.string.error_unknown
        else -> R.string.error_else
    }


    // общая логика — и для города, и для координат
    private fun loadWeather(city: String) {
        currentCity = city
        cityHolder.set(city)
        selectedDayHolder.clear()// <--- СБРАСЫВАЕМ СТАРУЮ ДАТУ ПРИ СМЕНЕ ГОРОДА
        observeWeather(city)// <--- ЗАПУСКАЕМ НОВЫЙ КОНВЕЙЕР
        refresh(city)
    }
}