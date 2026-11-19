
package com.android.events.data.di

import android.content.Context
import androidx.room.Room
import com.android.events.data.local.dao.EventDao
import com.android.events.data.local.database.EventsDatabase
import com.android.events.data.remote.datasource.LocalEventDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEventsDatabase(
        @ApplicationContext context: Context
    ): EventsDatabase {
        return Room.databaseBuilder(
            context,
            EventsDatabase::class.java,
            "events_database"
        ).build()
    }

    @Provides
    fun provideEventDao(
        database: EventsDatabase
    ): EventDao {
        return database.eventDao()
    }

    @Provides
    @Singleton
    fun provideLocalEventDataSource(
        @ApplicationContext context: Context
    ): LocalEventDataSource {
        return LocalEventDataSource(context)
    }
}
