
package com.android.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.events.data.local.database.EventsDatabase
import com.android.events.data.local.entity.EventEntity
import com.android.events.data.remote.datasource.LocalEventDataSource
import com.android.events.data.repository.EventRepositoryImpl
import com.android.events.domain.model.Event
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class EventRepositoryImplTest {

    private lateinit var database: EventsDatabase
    private lateinit var repository: EventRepositoryImpl
    private lateinit var localEventDataSource: FakeLocalEventDataSource

    private val eventDao
        get() = database.eventDao()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            EventsDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localEventDataSource = FakeLocalEventDataSource(context)

        repository = EventRepositoryImpl(
            eventDao = database.eventDao(),
            database = database,
            localEventDataSource = localEventDataSource
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getEvents_returnsAllEvents() = runTest {
        // Arrange
        val entities = listOf(
            createEventEntity("1", "Concert"),
            createEventEntity("2", "Music Festival")
        )

        eventDao.insertEvents(entities)

        // Act
        val result = repository.getEvents().first()

        // Assert
        assertEquals(2, result.size)
        assertEquals("Concert", result[0].title)
        assertEquals("Music Festival", result[1].title)
    }

    @Test
    fun getBookmarkedEvents_returnsOnlyBookmarkedEvents() = runTest {
        // Arrange
        eventDao.insertEvents(
            listOf(
                createEventEntity(
                    id = "1",
                    title = "Concert",
                    isBookmarked = true
                ),
                createEventEntity(
                    id = "2",
                    title = "Festival",
                    isBookmarked = false
                )
            )
        )

        // Act
        val result = repository.getBookmarkedEvents().first()

        // Assert
        assertEquals(1, result.size)
        assertEquals("Concert", result.first().title)
        assertTrue(result.first().isBookmarked)
    }

    @Test
    fun getEventById_returnsMatchingEvent() = runTest {
        // Arrange
        eventDao.insertEvents(
            listOf(createEventEntity("1", "Concert"))
        )

        // Act
        val result = repository.getEventById("1")

        // Assert
        assertNotNull(result)
        assertEquals("Concert", result?.title)
    }

    @Test
    fun getEventById_returnsNullWhenEventDoesNotExist() = runTest {
        // Act
        val result = repository.getEventById("unknown")

        // Assert
        assertNull(result)
    }

    @Test
    fun toggleBookmark_changesBookmarkStatus() = runTest {
        // Arrange
        eventDao.insertEvents(
            listOf(
                createEventEntity(
                    id = "1",
                    title = "Concert",
                    isBookmarked = false
                )
            )
        )

        // Act
        repository.toggleBookmark("1")

        // Assert
        val result = repository.getEventById("1")

        assertTrue(result?.isBookmarked == true)
    }

    @Test
    fun refreshEvents_preservesExistingBookmarkStatus() = runTest {
        // Arrange
        eventDao.insertEvents(
            listOf(
                createEventEntity(
                    id = "1",
                    title = "Old Concert",
                    isBookmarked = true
                ),
                createEventEntity(
                    id = "2",
                    title = "Old Festival",
                    isBookmarked = false
                )
            )
        )

        val updatedEvents = listOf(
            createEvent(
                id = "1",
                title = "Updated Concert"
            ),
            createEvent(
                id = "3",
                title = "New Festival"
            )
        )

        localEventDataSource.setEvents(updatedEvents)

        // Act
        repository.refreshEvents()

        // Assert
        val result = eventDao.getAllEventsOnce()

        assertEquals(2, result.size)

        val updatedConcert = result.first { it.id == "1" }

        assertEquals("Updated Concert", updatedConcert.title)
        assertTrue(updatedConcert.isBookmarked)

        // Newly added events should not be bookmarked.
        val newFestival = result.first { it.id == "3" }

        assertFalse(newFestival.isBookmarked)

        // Removed events should no longer exist.
        assertFalse(result.any { it.id == "2" })
    }

    private fun createEventEntity(
        id: String,
        title: String,
        isBookmarked: Boolean = false
    ) = EventEntity(
        id = id,
        title = title,
        location = "Kitchener",
        time = "2026-11-21T18:00:00",
        imageUrl = "https://example.com/event.jpg",
        latitude = 43.4516,
        longitude = -80.4925,
        isBookmarked = isBookmarked
    )

    private fun createEvent(
        id: String,
        title: String
    ) = Event(
        id = id,
        title = title,
        location = "Kitchener",
        time = "2026-11-21T18:00:00",
        imageUrl = "https://example.com/event.jpg",
        latitude = 43.4516,
        longitude = -80.4925,
        isBookmarked = false
    )
}
