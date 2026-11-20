package com.android.events

import android.app.Application
import com.android.events.workmanager.EventsRefreshScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EventsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        EventsRefreshScheduler(this).schedule()
    }
}