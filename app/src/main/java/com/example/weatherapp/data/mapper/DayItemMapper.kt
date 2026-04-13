package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.model.DayItemDTO
import com.example.weatherapp.domain.model.DayItem

fun DayItemDTO.toDomain(): DayItem {
    return DayItem(
        city = city,
        time = time,
        condition = condition,
        imageUrl = imageUrl,
        currentTemp = currentTemp,
        maxTemp = maxTemp,
        minTemp = minTemp,
        hours = hours
    )
}