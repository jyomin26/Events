
package com.android.events.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.events.details.UiState
import com.android.events.domain.model.Event
import com.android.events.domain.usecase.GetBookmarkedEventsUseCase
import com.android.events.domain.usecase.GetEventsUseCase
import com.android.events.domain.usecase.RefreshEventsUseCase
import com.android.events.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val getBookmarkedEventsUseCase: GetBookmarkedEventsUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val refreshEventsUseCase: RefreshEventsUseCase
) : ViewModel() {

    private val _events =
        MutableStateFlow<UiState<List<Event>>>(UiState.Loading)

    val events = _events.asStateFlow()

    private val _bookmarkedEvents =
        MutableStateFlow<UiState<List<Event>>>(UiState.Loading)

    val bookmarkedEvents = _bookmarkedEvents.asStateFlow()

    init {
        observeEvents()
        observeBookmarkedEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            try {
                val currentEvents = getEventsUseCase().first()

                if (currentEvents.isEmpty()) {
                    refreshEventsUseCase()
                }

                getEventsUseCase().collect { eventList ->
                    _events.value = UiState.Success(eventList)
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.value = UiState.Error(
                    e.message ?: "Failed to load events."
                )
            }
        }
    }

    private fun observeBookmarkedEvents() {
        viewModelScope.launch {
            try {
                getBookmarkedEventsUseCase().collect { eventList ->
                    _bookmarkedEvents.value =
                        UiState.Success(eventList)
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _bookmarkedEvents.value = UiState.Error(
                    e.message ?: "Failed to load saved events."
                )
            }
        }
    }

    fun toggleBookmark(eventId: String) {
        viewModelScope.launch {
            try {
                toggleBookmarkUseCase(eventId)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
            }
        }
    }

    fun retryLoadingEvents() {
        viewModelScope.launch {
            _events.value = UiState.Loading

            try {
                refreshEventsUseCase()

                getEventsUseCase().collect { eventList ->
                    _events.value = UiState.Success(eventList)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.value = UiState.Error(
                    e.message ?: "Failed to load events."
                )
            }
        }
    }
}
