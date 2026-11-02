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
import com.hexsoft.dailypoints.domain.usecase.goals.GetGoalsUseCase
import com.hexsoft.dailypoints.domain.usecase.points.GetDailyPointsUseCase
import com.hexsoft.dailypoints.utils.DateUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getDailyPointsUseCase: GetDailyPointsUseCase,
    private val getGoalsUseCase: GetGoalsUseCase,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        createChannel()
        val dailyPoints = getDailyPointsUseCase(DateUtils.today())
        val dailyGoal = getGoalsUseCase().first().dailyGoal
        val remaining = (dailyGoal - dailyPoints).coerceAtLeast(0)

        val content = if (remaining > 0) {
            "Reminder: Enter today's points. You need $remaining more to reach today's goal."
        } else {
            "Reminder: Enter today's points."
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
        return Result.success()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "daily_points_reminder_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
