package com.example.weatherapp.presentation.holders

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
//связывает MainViewModel с DaysViewModel и HoursViewModel
//(передаёт текущий город для загрузки прогноза)
@Singleton
class CityHolder @Inject constructor() {
    private val _currentCity = MutableStateFlow("")
    val currentCity = _currentCity.asStateFlow()
    fun set(city: String){
        _currentCity.value = city
    }
}