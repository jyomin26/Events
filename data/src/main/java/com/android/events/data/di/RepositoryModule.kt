
package com.android.events.data.di

import com.android.events.data.local.dao.EventDao
import com.android.events.data.local.database.EventsDatabase
import com.android.events.data.remote.datasource.LocalEventDataSource
import com.android.events.data.repository.EventRepositoryImpl
import com.android.events.domain.repository.EventRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        implementation: EventRepositoryImpl
    ): EventRepository
}
