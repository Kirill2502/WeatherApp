package com.example.weatherapp.domain.location

interface LocationTracker {
    suspend fun getCurrentLocation(): LocationResult
}