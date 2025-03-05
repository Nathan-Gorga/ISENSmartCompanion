package fr.isen.nathangorga.isensmartcompanion


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        createNotificationChannel(context)
        val eventTitle = intent?.getStringExtra("eventTitle") ?: "Event Reminder"

        val notificationManager = NotificationManagerCompat.from(context)

        val notification =
            NotificationCompat.Builder(context, "event_channel") // Utilise bien "event_channel"
                .setSmallIcon(R.drawable.ic_event)
                .setContentTitle("Reminder")
                .setContentText("Upcoming Event: $eventTitle")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notificationManager.notify(eventTitle.hashCode(), notification)
    }
}
