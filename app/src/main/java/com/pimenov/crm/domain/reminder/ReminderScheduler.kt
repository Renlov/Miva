package com.pimenov.crm.domain.reminder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) {

    fun needsPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    }

    fun schedule(taskId: Long, title: String, triggerAtMillis: Long) {
        val delay = triggerAtMillis - System.currentTimeMillis()
        if (delay <= 0) return

        val data = Data.Builder()
            .putLong(ReminderWorker.KEY_TASK_ID, taskId)
            .putString(ReminderWorker.KEY_TASK_TITLE, title)
            .build()

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(TAG_PREFIX + taskId)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                TAG_PREFIX + taskId,
                ExistingWorkPolicy.REPLACE,
                request
            )
    }

    fun cancel(taskId: Long) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(TAG_PREFIX + taskId)
    }

    companion object {
        private const val TAG_PREFIX = "reminder_task_"
    }
}
