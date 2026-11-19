
package com.android.events.domain.usecase

import com.android.events.domain.repository.EventRepository
import javax.inject.Inject

class GetBookmarkedEventsUseCase @Inject constructor(
    private val repository: EventRepository
) {
    operator fun invoke() = repository.getBookmarkedEvents()
}
