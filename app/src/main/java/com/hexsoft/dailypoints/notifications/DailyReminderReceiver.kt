package com.hexsoft.dailypoints.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hexsoft.dailypoints.R
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class DailyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        createChannel(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("Reminder: Enter today's points.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)

        val hour = intent.getIntExtra(EXTRA_REMINDER_HOUR, DEFAULT_HOUR)
        val minute = intent.getIntExtra(EXTRA_REMINDER_MINUTE, DEFAULT_MINUTE)
        scheduleNextDay(context, LocalTime.of(hour, minute))
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNextDay(context: Context, reminderTime: LocalTime) {
        val triggerAtMillis = LocalDateTime.now()
            .toLocalDate()
            .plusDays(1)
            .atTime(reminderTime)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val intent = Intent(context, DailyReminderReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_HOUR, reminderTime.hour)
            putExtra(EXTRA_REMINDER_MINUTE, reminderTime.minute)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private companion object {
        const val CHANNEL_ID = "daily_points_reminder_channel"
        const val NOTIFICATION_ID = 1001
        const val REQUEST_CODE = 4001
        const val EXTRA_REMINDER_HOUR = "extra_reminder_hour"
        const val EXTRA_REMINDER_MINUTE = "extra_reminder_minute"
        const val DEFAULT_HOUR = 20
        const val DEFAULT_MINUTE = 0
    }
}
