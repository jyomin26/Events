
package com.android.events.domain.repository

import com.android.events.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {

    fun getEvents(): Flow<List<Event>>

    fun getBookmarkedEvents(): Flow<List<Event>>

    suspend fun toggleBookmark(eventId: String)

    suspend fun refreshEvents()

    suspend fun getEventById(eventId: String): Event?
}
