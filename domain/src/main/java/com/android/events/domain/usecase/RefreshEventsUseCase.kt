package com.android.events.domain.usecase

import com.android.events.domain.repository.EventRepository
import javax.inject.Inject

class RefreshEventsUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke() {
        repository.refreshEvents()
    }
}