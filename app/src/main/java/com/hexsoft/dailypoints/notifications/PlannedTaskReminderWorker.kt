package com.hexsoft.dailypoints.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hexsoft.dailypoints.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PlannedTaskReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        createChannel()
        val planId = inputData.getLong(KEY_PLAN_ID, -1L)
        val taskId = inputData.getLong(KEY_TASK_ID, -1L)
        val taskName = inputData.getString(KEY_TASK_NAME).orEmpty()
        if ((planId <= 0L && taskId <= 0L) || taskName.isBlank()) return Result.failure()
        val notificationId = if (planId > 0L) planId.toInt() else taskId.toInt()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Scheduled Task Reminder")
            .setContentText("It's time for: $taskName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)
        return Result.success()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Scheduled Task Reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "planned_task_reminder_channel"
        const val KEY_PLAN_ID = "plan_id"
        const val KEY_TASK_ID = "task_id"
        const val KEY_TASK_NAME = "task_name"
    }
}
