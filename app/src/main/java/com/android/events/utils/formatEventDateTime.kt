package com.android.events.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun formatEventDateTime(dateTime: String): String {
    return try {
        val parsedDateTime = LocalDateTime.parse(dateTime)

        val formatter = DateTimeFormatter.ofPattern(
            "EEEE, MMMM dd, yyyy 'at' h:mm a",
            Locale.getDefault()
        )

        parsedDateTime.format(formatter)
    } catch (e: Exception) {
        dateTime
    }
}