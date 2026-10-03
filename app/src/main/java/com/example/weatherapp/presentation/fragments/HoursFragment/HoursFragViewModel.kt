package com.example.weatherapp.presentation.fragments.HoursFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.useCases.GetHoursWeatherUseCase
import com.example.weatherapp.presentation.holders.CityHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HoursFragViewModel @Inject constructor(
    private val getHoursWeatherUseCase: GetHoursWeatherUseCase,
    private val cityHolder: CityHolder

) : ViewModel() {
    private val _uiState = MutableStateFlow(HoursUiState())
    val uiState = _uiState.asStateFlow()

    private var collectJob: Job? = null

init {
    viewModelScope.launch {
        cityHolder.currentCity.collect {currentCity->
            if (currentCity.isNotEmpty()){
                observeHours(currentCity)
            }

        }
    }
}

    private fun observeHours(city: String) {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            getHoursWeatherUseCase(city).collect { hours ->
                _uiState.update {
                    it.copy(
                        hoursListData =hours

                    )
                }
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        collectJob?.cancel()
    }
}