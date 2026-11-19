
package com.android.events.data.repository

import androidx.room.withTransaction
import com.android.events.data.local.dao.EventDao
import com.android.events.data.local.database.EventsDatabase
import com.android.events.data.mapper.toDomain
import com.android.events.data.mapper.toEntity
import com.android.events.data.remote.datasource.LocalEventDataSource
import com.android.events.domain.model.Event
import com.android.events.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val database: EventsDatabase,
    private val localEventDataSource: LocalEventDataSource
) : EventRepository {

    override fun getEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBookmarkedEvents(): Flow<List<Event>> {
        return eventDao.getBookmarkedEvents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun toggleBookmark(eventId: String) {
        eventDao.toggleBookmark(eventId)
    }

    override suspend fun getEventById(eventId: String): Event? {
        return eventDao.getEventById(eventId)?.toDomain()
    }

    override suspend fun refreshEvents() {
        val events = localEventDataSource.getEvents()

        database.withTransaction {
            val existingEvents = eventDao.getAllEventsOnce()
                .associateBy { it.id }

            val updatedEntities = events.map { event ->
                event.toEntity(
                    isBookmarked = existingEvents[event.id]?.isBookmarked ?: false
                )
            }

            eventDao.deleteAllEvents()
            eventDao.insertEvents(updatedEntities)
        }
    }
}
