package com.example.weatherapp.di

import com.example.weatherapp.data.location.FusedLocationTracker
import com.example.weatherapp.domain.location.LocationTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {
    @Binds
    abstract fun bindLocationTracker(tracker: FusedLocationTracker): LocationTracker
}