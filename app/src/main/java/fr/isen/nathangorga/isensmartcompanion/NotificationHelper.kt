package fr.isen.nathangorga.isensmartcompanion

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

fun canScheduleExactAlarms(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}


@SuppressLint("ScheduleExactAlarm")
fun scheduleNotification(context: Context, eventId: String, eventTitle: String) {
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("eventTitle", eventTitle)
    }

    // Use hashCode() to generate a unique integer from the eventId string
    val requestCode = eventId.hashCode()

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val triggerTime = System.currentTimeMillis() + 10 * 1000 // 10 seconds later
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
}



fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "event_channel",
            "Event Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for subscribed events"
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    Log.d("NotificationHelper", "Notification channel created!")
}