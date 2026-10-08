package com.example.weatherapp.data.repository

import androidx.sqlite.SQLiteException
import com.example.weatherapp.data.mappers.toDomain
import com.example.weatherapp.data.mappers.toEntities
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.room.dao.WeatherDao
import com.example.weatherapp.domain.error.AppError
import com.example.weatherapp.domain.models.WeatherResult
import com.example.weatherapp.domain.repository.Repository
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImplement @Inject constructor(
    private val remoteData: WeatherRemoteDataSource,
    private val weatherDao: WeatherDao
) : Repository {
    override fun getWeather(city: String): Flow<WeatherResult?> =
        weatherDao.observeWeather(city)
            .map { it?.toDomain() }


    override suspend fun refreshWeather(city: String): Result<Unit> {
        return try {
            val dto = remoteData.requestWeatherData(city)

            val entities = dto.toEntities(
                city = city,
                updatedAt = System.currentTimeMillis()
            )

            weatherDao.replaceWeather(
                current = entities.current,
                forecast = entities.forecast,
                hours = entities.hours
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonSyntaxException) {
            Result.failure(AppError.ParseError())
        } catch (e: IOException) {
            Result.failure(AppError.NetworkError())
        } catch (e: SQLiteException) {
            Result.failure(AppError.DataBaseError())
        } catch (e: HttpException) {
            Result.failure(AppError.ServerError(e.code()))
        } catch (e: Exception) {
            Result.failure(AppError.UnknownError())
        }
    }


}