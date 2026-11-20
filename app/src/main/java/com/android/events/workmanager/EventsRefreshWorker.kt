package com.android.events.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.android.events.domain.usecase.RefreshEventsUseCase
import java.io.IOException

class EventsRefreshWorker(
    context: Context,
    params: WorkerParameters,
    private val refreshEventsUseCase: RefreshEventsUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            refreshEventsUseCase()
            Result.success()
        } catch (exception: IOException) {
            Result.retry()
        } catch (exception: Exception) {
            Result.failure()
        }
    }
}