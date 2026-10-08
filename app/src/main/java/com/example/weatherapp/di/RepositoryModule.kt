package com.example.weatherapp.di

import com.example.weatherapp.data.repository.RepositoryImplement
import com.example.weatherapp.domain.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn( SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(impl: RepositoryImplement): Repository
}