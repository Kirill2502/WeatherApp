package com.example.weatherapp.presentation.fragments.MainFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.useCases.GetCurrentWeatherUseCase
import com.example.weatherapp.domain.useCases.RefreshWeatherUseCase
import com.example.weatherapp.presentation.holders.CityHolder
import com.example.weatherapp.presentation.holders.SelectedDayHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase,
    private val selectedDayHolder: SelectedDayHolder,
    private val cityHolder: CityHolder
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()
    private var currentCity: String? = null
    private var collectJob: Job? = null
    init {
        viewModelScope.launch {
            selectedDayHolder.selectedDay.collect {day->
                if (day!=null){
                    _uiState.update { it.copy(currentData = day) }
                }

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
    private fun observeCurrent(city: String){
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            getCurrentWeatherUseCase(city).collect {current->
                // обновляем currentData только если день не выбран
                android.util.Log.d("WeatherDebug", "UI получил current: ${current.cityCoordinates}, temp=${current.currentTemp}")
                if (selectedDayHolder.selectedDay.value == null){
                    _uiState.update { it.copy(currentData = current, city = city) }
                }else{
                    _uiState.update { it.copy(city = city) }// city всё равно обновляем
                }


            }
        }
    }
    private fun refresh(city: String){
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true)}
            try {
                refreshWeatherUseCase(city)
            }catch (e: Exception){
                _uiState.update { it.copy(error = e.message?:"Ошибка обновления") }
            }finally {
                _uiState.update { it.copy(isRefreshing = false)}
            }
        }
    }
    // общая логика — и для города, и для координат
    private fun loadWeather(city: String) {
        currentCity = city
        cityHolder.set(city)
        observeCurrent(city)
        refresh(city)
    }

    override fun onCleared() {
        super.onCleared()
        collectJob?.cancel()
    }

}