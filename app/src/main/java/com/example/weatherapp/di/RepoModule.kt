package com.example.weatherapp.di

import com.example.weatherapp.data.repository.RepositoryImplement
import com.example.weatherapp.domain.Repo.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn( SingletonComponent::class)
abstract class RepoModule {
    @Binds
    abstract fun bindRepository(repositoryImplement: RepositoryImplement): Repository
}