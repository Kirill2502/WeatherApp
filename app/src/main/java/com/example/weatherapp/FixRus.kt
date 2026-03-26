package com.example.weatherapp

class FixRus {
    val weatherMap = mapOf("Partly Cloudy " to "Переменная облачность",
        "Partly Cloudy" to "Переменная облачность",
        "Partly cloudy" to "Переменная облачность",
        "Cloudy" to "Облачно",
        "Cloudy " to "Облачно",
        "Overcast " to "Пасмурно",
        "Patchy rain nearby" to "Приближается мелкий дождь",
        "Light drizzle" to "Мелкий моросящий дождь",
        "Sunny" to "Солнечно",
        "Clear " to "Безоблачно",
        "Mist" to "Туман",
        "Light rain shower" to "Небольшой кратковременный дождь",
        "Moderate rain" to "Умнеренный дождь",
        "Light rain" to "Небольшой дождь",
        "Thundery outbreaks in nearby" to "Громовые вспышки",
        "Patchy light rain in area with thunder" to "Местами небольшой дождь с грозой",
        "Frosty " to "Морозно",
        "Сold " to "Холодно",
        "Snow " to "Снег",
        "Blizzard " to "Метель",
        "Black ice" to "Гололед",
        "Patchy light drizzle" to "Мелкий моросящий дождь",
    )

     fun getWeatherDescription(type: String): String {
        return weatherMap[type] ?: "Неизвестная погода"}
}