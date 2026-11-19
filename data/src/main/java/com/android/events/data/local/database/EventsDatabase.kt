
package com.android.events.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.events.data.local.dao.EventDao
import com.android.events.data.local.entity.EventEntity

@Database(
    entities = [EventEntity::class],
    version = 1,
    exportSchema = true
)
abstract class EventsDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
}
