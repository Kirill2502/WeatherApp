package com.example.weatherapp.domain.location

data class Coordinates(
    val latitude: Double,
    val longitude: Double
) {
    fun toQuery(): String = "$latitude,$longitude"
}
