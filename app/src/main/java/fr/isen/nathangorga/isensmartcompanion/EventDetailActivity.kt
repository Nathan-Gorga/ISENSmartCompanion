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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)

        val eventId = intent.getStringExtra("eventId") ?: ""
        setContent {
            EventDetailScreen(eventId)
        }
    }
}

@Composable
fun EventDetailScreen(eventId: String) {

    var event by remember { mutableStateOf<Event?>(null) }
    var isLoading by remember { mutableStateOf(true) }


    LaunchedEffect(eventId) {
        RetrofitClient.apiService.getEvents().enqueue(object : retrofit2.Callback<List<Event>> {
            override fun onResponse(
                call: retrofit2.Call<List<Event>>,
                response: retrofit2.Response<List<Event>>
            ) {
                if (response.isSuccessful) {

                    event = response.body()?.find { it.id == eventId }
                }
                isLoading = false
            }

            override fun onFailure(call: retrofit2.Call<List<Event>>, t: Throwable) {
                isLoading = false

            }
        })
    }

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
        if (isLoading) {
            Text("Loading event details...", fontSize = 18.sp)
        } else {
            event?.let {
                Text(text = it.title, fontSize = 24.sp)
                Text(text = "Date: ${it.date}", fontSize = 16.sp)
                Text(text = "Point de RDV: ${it.location}", fontSize = 16.sp)
                Text(text = it.description, fontSize = 14.sp)

                // Notification Toggle
                Icon(
                    painter = painterResource(
                        if (isNotified.value) R.drawable.notif_on else R.drawable.notif_off
                    ),
                    contentDescription = "Notification Toggle",
                    modifier = Modifier
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
                        }
                )
            } ?: Text("Event not found.", fontSize = 18.sp)
        }
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

