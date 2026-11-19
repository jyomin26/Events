
package com.android.events.data.mapper

import com.android.events.data.local.entity.EventEntity
import com.android.events.domain.model.Event

fun EventEntity.toDomain(): Event {
    return Event(
        id = id,
        title = title,
        location = location,
        time = time,
        imageUrl = imageUrl,
        latitude = latitude,
        longitude = longitude,
        isBookmarked = isBookmarked
    )
}

fun Event.toEntity(
    isBookmarked: Boolean = this.isBookmarked
): EventEntity {
    return EventEntity(
        id = id,
        title = title,
        location = location,
        time = time,
        imageUrl = imageUrl,
        latitude = latitude,
        longitude = longitude,
        isBookmarked = isBookmarked
    )
}
