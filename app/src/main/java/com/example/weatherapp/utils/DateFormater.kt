package com.example.weatherapp.utils

import androidx.annotation.StringRes
import com.example.weatherapp.R
import java.time.LocalDate

object DateFormater {
    @StringRes
    fun getRelationDateRes(date: LocalDate, today: LocalDate?): Int? {
        return when (date) {
            today -> R.string.today
            today?.plusDays(1) -> R.string.tomorrow
            today?.plusDays(2) -> R.string.day_after_tomorrow
            else -> null// Если дата не сегодня/завтра — вернем null, значит будем показывать обычную дату
        }
    }
}