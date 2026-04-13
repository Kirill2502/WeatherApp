package com.example.weatherapp.presentation.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.UseCases.GetWeatherUseCase
import com.example.weatherapp.domain.model.DayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {
    val liveDataCurrent = MutableLiveData<DayItem>()
    val liveDataList = MutableLiveData<List<DayItem>>()
    val liveDataHours = MutableLiveData<List<DayItem>>()

    fun loadWeather(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val weather = getWeatherUseCase.invoke(city)
            withContext(Dispatchers.Main) {
                liveDataList.value = weather.forecast   // <--- берем только список
                liveDataCurrent.value = weather.current // <--- текущий день
                liveDataHours.value = weather.hours// <--- разбивка по часам
            }
        }

    }
}


