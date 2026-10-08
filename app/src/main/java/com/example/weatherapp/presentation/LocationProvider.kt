package com.example.weatherapp.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationProvider(context: Context) {
    private val appContext = context.applicationContext
    private val fLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    fun isLocationEnabled(): Boolean {
        val locationManager =
            appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun isGranted(permission: String) =
        ContextCompat.checkSelfPermission(
            appContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    // На Android 12+ пользователь может выдать только приблизительную (COARSE) геолокацию.
    // Для погоды этого достаточно, поэтому хватает любого из двух разрешений.
    fun hasLocationPermission(): Boolean =
        isGranted(Manifest.permission.ACCESS_FINE_LOCATION) ||
                isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)

    @SuppressLint("MissingPermission") // разрешение проверяется в hasLocationPermission()
    fun getCurrentCoordinates(onResult: (String) -> Unit, onError: () -> Unit) {
        if (!hasLocationPermission()) {
            onError()
            return
        }
        val priority = if
                (isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Priority.PRIORITY_HIGH_ACCURACY//5-20m точность через gps
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY//500m точность если не дали файн
        }


        val ct = CancellationTokenSource()//объект для отмены запроса локации
        // addOnSuccessListener вместо task.result: task.result бросает исключение,
        // если получить локацию не удалось
        fLocationClient.getCurrentLocation(priority, ct.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    onResult("${location.latitude},${location.longitude}")
                } else {
                    onError()
                }
            }
            .addOnFailureListener { onError() }
    }
}