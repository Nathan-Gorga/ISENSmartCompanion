package fr.isen.nathangorga.isensmartcompanion

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        val eventId = intent.getIntExtra("eventId", -1) // Get eventId from Intent
        setContent {
            val navController = rememberNavController() // Only if navigation is needed
            EventDetailScreen(eventId, navController)
        }
    }
}

@Composable
fun EventDetailScreen(eventId: Int, navController: NavHostController) {
    val event = getFakeEvents().find { it.id == eventId }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
    val isNotified =
        remember { mutableStateOf(sharedPreferences.getBoolean("event_$eventId", false)) }
    RequestNotificationPermission(LocalContext.current)
    if (!canScheduleExactAlarms(context) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        context.startActivity(intent)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        event?.let {
            Text(text = it.title, fontSize = 24.sp)
            Text(text = "Date: ${it.date}", fontSize = 16.sp)
            Text(text = "Point de RDV: ${it.location}", fontSize = 16.sp)
            Text(text = it.description, fontSize = 14.sp)

            // Notification Toggle
            Icon(painter = painterResource(
                if (isNotified.value) R.drawable.notif_on
                else R.drawable.notif_off
            ), contentDescription = "Notification Toggle", modifier = Modifier
                .size(46.dp)
                .padding(top = 16.dp)
                .clickable {
                    isNotified.value = !isNotified.value
                    sharedPreferences.edit().putBoolean("event_$eventId", isNotified.value)
                        .apply()

                    if (isNotified.value) {
                        if (canScheduleExactAlarms(context)) {
                            scheduleNotification(context, eventId, it.title)
                        } else {
                            Log.e(
                                "EventDetail",
                                "Permission SCHEDULE_EXACT_ALARM non accordée !"
                            )
                        }
                    }
                })
        } ?: Text("Event not found.")
    }
}

@Composable
fun RequestNotificationPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        val isGranted = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            val activity = context as Activity
            ActivityCompat.requestPermissions(activity, arrayOf(permission), 101)
        }
    }
}

