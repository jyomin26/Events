
package com.android.events.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.events.data.local.dao.CacheMetadataDao
import com.android.events.data.local.dao.EventDao
import com.android.events.data.local.entity.CacheMetadataEntity
import com.android.events.data.local.entity.EventEntity

@Database(
    entities = [EventEntity::class, CacheMetadataEntity::class],
    version = 1,
    exportSchema = true
)
abstract class EventsDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun cacheMetadataDao(): CacheMetadataDao
}
