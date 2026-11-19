
package com.android.events.data.remote.datasource

import android.content.Context
import com.android.events.domain.model.Event
import org.json.JSONArray

open class LocalEventDataSource(
    private val context: Context
) {

    open fun getEvents(): List<Event> {

        val json = context.assets
            .open("Events.json")
            .bufferedReader()
            .use { it.readText() }

        val jsonArray = JSONArray(json)

        return buildList {

            for (index in 0 until jsonArray.length()) {

                val item = jsonArray.getJSONObject(index)

                add(
                    Event(
                        id = item.getString("id"),
                        title = item.getString("title"),
                        location = item.getString("location"),
                        time = item.getString("time"),
                        imageUrl = item.getString("imageUrl"),
                        latitude = item.getDouble("latitude"),
                        longitude = item.getDouble("longitude")
                    )
                )
            }
        }
    }
}
