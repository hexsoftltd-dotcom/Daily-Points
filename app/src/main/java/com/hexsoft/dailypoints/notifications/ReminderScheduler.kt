package com.hexsoft.dailypoints.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.workDataOf
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager,
) {
    fun scheduleDailyReminders(reminderTime: LocalTime) {
        cancelDailyReminders()
        scheduleExactDailyAlarm(nextTriggerTime(reminderTime), reminderTime)
    }

    fun cancelDailyReminders() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(reminderPendingIntent())
        workManager.cancelUniqueWork(UNIQUE_ONE_TIME_WORK_NAME)
        workManager.cancelUniqueWork(UNIQUE_PERIODIC_WORK_NAME)
    }

    fun schedulePlannedTaskReminder(planId: Long, taskName: String, triggerAtMillis: Long) {
        val delayMillis = triggerAtMillis - System.currentTimeMillis()
        require(delayMillis > 0) { "Reminder time must be in the future." }
        val request = OneTimeWorkRequestBuilder<PlannedTaskReminderWorker>()
            .setInitialDelay(Duration.ofMillis(delayMillis))
            .setInputData(
                workDataOf(
                    PlannedTaskReminderWorker.KEY_PLAN_ID to planId,
                    PlannedTaskReminderWorker.KEY_TASK_NAME to taskName,
                ),
            )
            .build()
        workManager.enqueueUniqueWork(
            plannedTaskWorkName(planId),
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    fun cancelPlannedTaskReminder(planId: Long) {
        workManager.cancelUniqueWork(plannedTaskWorkName(planId))
    }

    fun scheduleDailyPlannedTaskReminder(taskId: Long, taskName: String, reminderTime: LocalTime) {
        require(taskId > 0L) { "Task id is required for daily reminder." }
        val initialDelayMillis = Duration.between(
            LocalDateTime.now(),
            nextRunDateTime(reminderTime),
        ).toMillis().coerceAtLeast(1L)
        val request = PeriodicWorkRequestBuilder<PlannedTaskReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    PlannedTaskReminderWorker.KEY_PLAN_ID to -1L,
                    PlannedTaskReminderWorker.KEY_TASK_ID to taskId,
                    PlannedTaskReminderWorker.KEY_TASK_NAME to taskName,
                ),
            )
            .build()
        workManager.enqueueUniquePeriodicWork(
            plannedTaskDailyWorkName(taskId),
            androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    fun cancelDailyPlannedTaskReminder(taskId: Long) {
        workManager.cancelUniqueWork(plannedTaskDailyWorkName(taskId))
    }

    private fun nextTriggerTime(reminderTime: LocalTime): Long {
        return nextRunDateTime(reminderTime)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun nextRunDateTime(reminderTime: LocalTime): LocalDateTime {
        val now = LocalDateTime.now()
        val nowMinutes = now.hour * 60 + now.minute
        val reminderMinutes = reminderTime.hour * 60 + reminderTime.minute
        val nextDate = if (nowMinutes <= reminderMinutes) now.toLocalDate() else now.toLocalDate().plusDays(1)
        return nextDate.atTime(reminderTime)
    }

    private fun scheduleExactDailyAlarm(triggerAtMillis: Long, reminderTime: LocalTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = reminderPendingIntent(reminderTime)
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        }
    }

    private fun reminderPendingIntent(reminderTime: LocalTime? = null): PendingIntent {
        val intent = Intent(context, DailyReminderReceiver::class.java).apply {
            reminderTime?.let {
                putExtra(EXTRA_REMINDER_HOUR, it.hour)
                putExtra(EXTRA_REMINDER_MINUTE, it.minute)
            }
        }
        return PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private companion object {
        const val UNIQUE_ONE_TIME_WORK_NAME = "daily_points_reminder_once"
        const val UNIQUE_PERIODIC_WORK_NAME = "daily_points_reminder_periodic"
        const val DAILY_REMINDER_REQUEST_CODE = 4001
        const val EXTRA_REMINDER_HOUR = "extra_reminder_hour"
        const val EXTRA_REMINDER_MINUTE = "extra_reminder_minute"

        fun plannedTaskWorkName(planId: Long): String = "planned_task_reminder_$planId"
        fun plannedTaskDailyWorkName(taskId: Long): String = "planned_task_daily_reminder_$taskId"
    }
}
