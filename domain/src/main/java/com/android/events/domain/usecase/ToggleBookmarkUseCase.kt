
package com.android.events.domain.usecase

import com.android.events.domain.repository.EventRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor (
    private val repository: EventRepository
) {
    suspend operator fun invoke(eventId: String) {
        repository.toggleBookmark(eventId)
    }
}
