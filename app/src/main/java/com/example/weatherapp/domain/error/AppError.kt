package com.example.weatherapp.domain.error

sealed class AppError : Exception() {
    class NetworkError : AppError()
    class ParseError : AppError()
    data class ServerError(val code: Int) : AppError()
    class DataBaseError : AppError()
    class UnknownError : AppError()
}