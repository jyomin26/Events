package com.android.data

import android.content.Context
import com.android.events.data.remote.datasource.LocalEventDataSource
import com.android.events.domain.model.Event

class FakeLocalEventDataSource(context: Context) : LocalEventDataSource(context) {

    private var events = emptyList<Event>()

    fun setEvents(events: List<Event>) {
        this.events = events
    }

    override fun getEvents(): List<Event> {
        return events
    }
}