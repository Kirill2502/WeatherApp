package com.example.weatherapp.presentation.holders

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

//связывает MainViewModel с DaysViewModel и HoursViewModel
//(передаёт текущий город для загрузки прогноза)

// @ActivityRetainedScoped: один экземпляр на активити, переживает поворот экрана
// и уничтожается вместе с ней. @Singleton держал бы состояние весь процесс.
@ActivityRetainedScoped
class CityHolder @Inject constructor() {
    private val _currentCity = MutableStateFlow("")
    val currentCity = _currentCity.asStateFlow()
    fun set(city: String){
        _currentCity.value = city
    }
}