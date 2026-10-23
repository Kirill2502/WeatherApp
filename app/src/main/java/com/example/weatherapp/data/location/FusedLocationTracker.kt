package com.example.weatherapp.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.weatherapp.domain.location.Coordinates
import com.example.weatherapp.domain.location.LocationResult
import com.example.weatherapp.domain.location.LocationTracker
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class FusedLocationTracker @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationTracker {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    //Просто проверяет, дал ли пользователь разрешение
    private fun isGranted(permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    //Проверяет, включен ли сам GPS (или Wi-Fi геолокация) в шторке телефона
    private fun isLocationEnabled(): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission") // разрешения проверяются ниже
    override suspend fun getCurrentLocation(): LocationResult {
        val hasFine = isGranted(Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarse = isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (!hasFine && !hasCoarse) return LocationResult.PermissionDenied
        if (!isLocationEnabled()) return LocationResult.LocationDisabled

        val priority = if (hasFine) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }
        // Превращаем колбэк-API Google Play Services в suspend-функцию
        return suspendCancellableCoroutine { cont ->
            val cancellationToken = CancellationTokenSource()
            client.getCurrentLocation(priority, cancellationToken.token)
                .addOnSuccessListener { location ->
                    val result = if (location != null) {
                        LocationResult.Success(
                            Coordinates(
                                location.latitude,
                                location.longitude
                            )
                        )
                    } else {
                        LocationResult.Error
                    }
                    cont.resume(result)// <--- БУДИМ КОРУТИНУ
                }
                .addOnFailureListener { cont.resume(LocationResult.Error) }
            // Корутину отменили (ушли с экрана) — отменяем и запрос геолокации
            cont.invokeOnCancellation { cancellationToken.cancel() }
        }

    }
}