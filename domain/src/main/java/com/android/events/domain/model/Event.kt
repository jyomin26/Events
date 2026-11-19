
package com.android.events.domain.model

data class Event(
    val id: String,
    val title: String,
    val location: String,
    val time: String,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val isBookmarked: Boolean = false
)
