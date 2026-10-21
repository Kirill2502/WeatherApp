package com.example.weatherapp.domain.useCases

import com.example.weatherapp.domain.models.HourlyForecast
import com.example.weatherapp.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
// Бизнес-правило "какие часы показывать" живёт в domain, а не во ViewModel
class GetHoursForDayUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(city: String, date: LocalDate?): Flow<List<HourlyForecast>> =
        repository.getWeather(city).map { weather ->
            if (weather == null) return@map emptyList()
            if (date == null) {
                // "Сейчас" берём в местном времени ГОРОДА (время последнего обновления с сервера),
                // а не по часам телефона: для найденного города в другом часовом поясе
                // часы устройства дали бы неверный результат
                val now = weather.current.updatedAt.truncatedTo(ChronoUnit.HOURS)
                weather.hours.filter{!it.time.isBefore(now)}
                    .take(HOURS_IN_DAY)
            } else {
                weather.hours.filter { it.time.toLocalDate() == date }
            }

        }

    private companion object {
        const val HOURS_IN_DAY = 24
    }
}