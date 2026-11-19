package com.android.events.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.events.domain.model.Event
import com.android.events.domain.model.UserLocation
import com.android.events.domain.usecase.GetCurrentLocationUseCase
import com.android.events.domain.usecase.GetEventByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {


    private val _event =
        MutableStateFlow<UiState<Event>>(UiState.Loading)

    val event = _event.asStateFlow()

    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation = _userLocation.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _event.value = UiState.Loading

            try {
                val event = getEventByIdUseCase(eventId)

                _event.value = if (event != null) {
                    UiState.Success(event)
                } else {
                    UiState.Error("Event not found.")
                }

            } catch (e: Exception) {
                _event.value = UiState.Error(
                    e.message ?: "Something went wrong. Please try again."
                )
            }
        }
    }

    fun loadCurrentLocation() {
        viewModelScope.launch {
            _userLocation.value = getCurrentLocationUseCase()
        }
    }
}