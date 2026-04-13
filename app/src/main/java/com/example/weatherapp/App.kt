package com.example.weatherapp

import android.app.Application
import com.android.volley.toolbox.Volley
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {
    val requestQueue by lazy{
        Volley.newRequestQueue(this)
    }
}