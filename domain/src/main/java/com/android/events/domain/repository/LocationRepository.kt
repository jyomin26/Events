package com.android.events.domain.repository

import com.android.events.domain.model.UserLocation

interface LocationRepository {
    suspend fun getCurrentLocation(): UserLocation?
}