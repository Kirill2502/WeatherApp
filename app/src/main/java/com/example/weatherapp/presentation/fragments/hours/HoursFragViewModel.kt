package com.example.weatherapp.presentation.fragments.hours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.useCases.GetHoursForDayUseCase
import com.example.weatherapp.presentation.holders.CityHolder
import com.example.weatherapp.presentation.holders.SelectedDayHolder
import com.example.weatherapp.presentation.mappers.toListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HoursFragViewModel @Inject constructor(
    cityHolder: CityHolder,
    selectedDayHolder: SelectedDayHolder,
    private val getHoursForDayUseCase: GetHoursForDayUseCase
) : ViewModel() {


    val uiState: StateFlow<HoursUiState> =
        combine(cityHolder.currentCity.filter {
            it.isNotEmpty()
        }, selectedDayHolder.selectedDay) { city, date -> city to date }
            .flatMapLatest { (city, date) -> getHoursForDayUseCase(city, date) }
            .map { hours -> HoursUiState(hoursListData = hours.map { it.toListItem() }) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HoursUiState())


}