
package com.android.events.data.repository

import androidx.room.withTransaction
import com.android.events.data.local.dao.CacheMetadataDao
import com.android.events.data.local.dao.EventDao
import com.android.events.data.local.database.EventsDatabase
import com.android.events.data.local.entity.CacheMetadataEntity
import com.android.events.data.mapper.toDomain
import com.android.events.data.mapper.toEntity
import com.android.events.data.remote.datasource.LocalEventDataSource
import com.android.events.domain.model.Event
import com.android.events.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val EVENTS_CACHE_KEY = "events"
private const val CACHE_TTL = 15 * 60 * 1000L

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val database: EventsDatabase,
    private val localEventDataSource: LocalEventDataSource,
    private val cacheMetadataDao: CacheMetadataDao
) : EventRepository {


    override fun getEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private suspend fun isCacheExpired(): Boolean {
        val metadata = cacheMetadataDao.getMetadata(EVENTS_CACHE_KEY)
            ?: return true

        val currentTime = System.currentTimeMillis()

        return currentTime - metadata.lastFetchedAt > CACHE_TTL
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

        // Don't refresh if cache is still valid
        if (!isCacheExpired()) return

        // Fetch events
        val events = localEventDataSource.getEvents()

        // Update database transactionally
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

            // Update timestamp only after successful refresh
            cacheMetadataDao.insertMetadata(
                CacheMetadataEntity(
                    cacheKey = EVENTS_CACHE_KEY,
                    lastFetchedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
