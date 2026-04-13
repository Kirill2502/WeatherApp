package com.example.weatherapp.data.remote

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.example.weatherapp.data.model.WeatherResultDTO
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

const val API_KEY = "d169aff430f54f869b3100235252806"
@Singleton
class RemoteData @Inject constructor(private val weatherParsing: WeatherParsing,
                                     private val queue: RequestQueue) {

    suspend fun requestWeatherData(city: String): WeatherResultDTO =
        suspendCancellableCoroutine { continuation ->   //по ссылке с сайта получаем данные в джейсон формате
            val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                    API_KEY +
                    "&q=" +
                    city +
                    "&days=" +
                    "3" +
                    "&aqi=no&alerts=no"
            val request = StringRequest(
                Request.Method.GET,//метод получить
                url,
                { result ->//сам джейсон файл в виде результата
                    val parsed = weatherParsing.parseWeatherData(result)
                    continuation.resume(parsed)

                },
                { error ->//обработка ошибки на случай неверно указанных данных(API)
                    continuation.resumeWithException(error)

                }
            )
            queue.add(request)
        }
}