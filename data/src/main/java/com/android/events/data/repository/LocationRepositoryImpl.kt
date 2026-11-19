package com.android.events.data.repository

import com.android.events.data.location.LocationDataSource
import com.android.events.domain.model.UserLocation
import com.android.events.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource
) : LocationRepository {

    override suspend fun getCurrentLocation(): UserLocation? {
        return locationDataSource.getCurrentLocation()?.let { location ->
            UserLocation(
                latitude = location.latitude,
                longitude = location.longitude
            )
        }
    }
}