package com.example.weatherapp.domain.location

sealed interface LocationResult {
    data class Success(val coordinates: Coordinates) : LocationResult
    data object PermissionDenied : LocationResult
    data object LocationDisabled : LocationResult
    data object Error : LocationResult
}