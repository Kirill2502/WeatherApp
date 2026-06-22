package com.example.weatherapp.presentation.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.di.DispatchersModule
import com.example.weatherapp.di.IoDispatcher
import com.example.weatherapp.di.MainDispatcher
import com.example.weatherapp.domain.UseCases.GetWeatherUseCase
import com.example.weatherapp.domain.model.DayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val getWeatherUseCase: GetWeatherUseCase,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher
    private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {
    val liveDataCurrent = MutableLiveData<DayItem>()
    val liveDataList = MutableLiveData<List<DayItem>>()
    val liveDataHours = MutableLiveData<List<DayItem>>()

    fun loadWeather(city: String) {
        viewModelScope.launch(ioDispatcher) {
            val weather = getWeatherUseCase.invoke(city)
            withContext(mainDispatcher) {
                liveDataList.value = weather.forecast   // <--- берем только список
                liveDataCurrent.value = weather.current // <--- текущий день
                liveDataHours.value = weather.hours// <--- разбивка по часам
            }
        }

    }
}


