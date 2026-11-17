
package com.example.pawpal

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val title = inputData.getString("title")
                ?: return Result.failure()

            val petName = inputData.getString("petName")
                ?: return Result.failure()

            val taskId = inputData.getInt("taskId", -1)

            if (taskId == -1) {
                return Result.failure()
            }

            createNotificationChannel()

            if (
                Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(
                    "PawPalNotifications",
                    "Notification permission denied"
                )
                return Result.success()
            }

            val notification = NotificationCompat.Builder(
                applicationContext,
                CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("PawPal Reminder")
                .setContentText("$title for $petName")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(
                applicationContext
            ).notify(taskId, notification)

            Log.d(
                "PawPalNotifications",
                "Notification sent for task $taskId"
            )

            Result.success()

        } catch (e: Exception) {
            Log.e(
                "PawPalNotifications",
                "Notification failed",
                e
            )
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pet Care Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for pet care tasks"
            }

            val manager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "pawpal_reminders"
    }
}
