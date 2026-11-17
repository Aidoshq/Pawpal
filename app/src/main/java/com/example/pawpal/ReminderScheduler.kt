
package com.example.pawpal

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleReminder(
        context: Context,
        task: CareTask,
        petName: String
    ) {
        try {
            val formatter = SimpleDateFormat(
                "HH:mm",
                Locale.US
            ).apply {
                isLenient = false
            }

            val parsedTime = formatter.parse(task.time)
                ?: throw IllegalArgumentException(
                    "Invalid reminder time"
                )

            val selectedTime = Calendar.getInstance().apply {
                val parsedCalendar = Calendar.getInstance().apply {
                    time = parsedTime
                }

                set(
                    Calendar.HOUR_OF_DAY,
                    parsedCalendar.get(Calendar.HOUR_OF_DAY)
                )
                set(
                    Calendar.MINUTE,
                    parsedCalendar.get(Calendar.MINUTE)
                )
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // If the selected time has passed,
            // schedule for tomorrow.
            if (
                selectedTime.timeInMillis <=
                System.currentTimeMillis()
            ) {
                selectedTime.add(Calendar.DAY_OF_YEAR, 1)
            }

            val delay = selectedTime.timeInMillis -
                    System.currentTimeMillis()

            val inputData = Data.Builder()
                .putString("title", task.title)
                .putString("petName", petName)
                .putInt("taskId", task.id)
                .build()

            val reminderRequest =
                OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(
                        delay.coerceAtLeast(0L),
                        TimeUnit.MILLISECONDS
                    )
                    .setInputData(inputData)
                    .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "pawpal_task_${task.id}",
                    ExistingWorkPolicy.REPLACE,
                    reminderRequest
                )

            Log.d(
                "PawPalNotifications",
                "Scheduled task ${task.id} " +
                        "for ${selectedTime.time}"
            )

        } catch (e: Exception) {
            Log.e(
                "PawPalNotifications",
                "Failed to schedule task ${task.id}",
                e
            )
        }
    }

    fun cancelReminder(
        context: Context,
        taskId: Int
    ) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("pawpal_task_$taskId")

        Log.d(
            "PawPalNotifications",
            "Cancelled reminder for task $taskId"
        )
    }
}
