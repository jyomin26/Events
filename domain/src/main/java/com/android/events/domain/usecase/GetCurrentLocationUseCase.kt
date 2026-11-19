package com.android.events.domain.usecase

import com.android.events.domain.model.UserLocation
import com.android.events.domain.repository.LocationRepository
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): UserLocation? {
        return locationRepository.getCurrentLocation()
    }
}