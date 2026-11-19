package com.android.events.data.di

import com.android.events.data.repository.LocationRepositoryImpl
import com.android.events.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        implementation: LocationRepositoryImpl
    ): LocationRepository
}