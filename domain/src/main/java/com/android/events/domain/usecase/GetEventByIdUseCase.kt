package com.android.events.domain.usecase

import com.android.events.domain.model.Event
import com.android.events.domain.repository.EventRepository
import javax.inject.Inject

class GetEventByIdUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(eventId: String): Event? {
        return repository.getEventById(eventId)
    }
}