package fr.isen.nathangorga.isensmartcompanion

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.isen.nathangorga.isensmartcompanion.data.ChatMessage
import fr.isen.nathangorga.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISENSmartCompanionTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavigationGraph(navController, Modifier.padding(innerPadding))

                }


            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }


}

@Composable
fun TitleAndLogo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "ISEN Smart Companion",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium
        )


    }
}

//Navigation
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("home", "events", "agenda", "history") // Added "agenda"
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                label = { Text(screen.replaceFirstChar { it.uppercase() }) },
                selected = navController.currentBackStackEntryAsState().value?.destination?.route == screen,
                onClick = { navController.navigate(screen) },
                icon = { /* Add icons if needed */ }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "home", modifier = modifier) {
        composable("home") { MainScreen() }
        composable("events") { EventsScreen(navController) }
        composable("agenda") { AgendaScreen() }
        composable("history") { HistoryScreen() }

        composable("event_detail/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            eventId?.let { EventDetailScreen(eventId) }
        }
    }
}

// Screens
@Composable
fun MainScreen() {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        TitleAndLogo()
        Spacer(modifier = Modifier.weight(1f))
        UserInput()


    }
}

@Composable
fun EventsScreen(navController: NavHostController) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        RetrofitClient.apiService.getEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    events = response.body() ?: emptyList()
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                isLoading = false
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Événements ISEN", fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))

        if (isLoading) {
            Text("Chargement...")
        } else {
            LazyColumn {
                items(events) { event ->
                    EventItem(event, onClick = {
                        navController.navigate("event_detail/${event.id}")
                    })
                }
            }
        }
    }
}



@Composable
fun HistoryScreen(viewModel: MainViewModel = viewModel()) {
    val history by viewModel.chatHistory.collectAsState(initial = emptyList())

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Chat History", fontSize = MaterialTheme.typography.headlineMedium.fontSize)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(history) { message ->
                ChatHistoryItem(message, onDelete = { viewModel.deleteMessage(it) })
            }
        }

        Button(onClick = { viewModel.clearHistory() }, modifier = Modifier.fillMaxWidth()) {
            Text("Clear History")
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun ChatHistoryItem(message: ChatMessage, onDelete: (ChatMessage) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "User: ${message.userMessage}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "AI: ${message.aiResponse}", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Date: ${
                    java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(message.timestamp)
                }",
                style = MaterialTheme.typography.labelSmall
            )

            TextButton(onClick = { onDelete(message) }) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, fontSize = 20.sp, color = Color.Black)
            Text(text = event.date, fontSize = 14.sp, color = Color.DarkGray)
            Text(text = event.location, fontSize = 14.sp, color = Color.Gray)
        }
    }
}


// TODO : delete getFakeEvents once API has been added
fun getFakeEvents(): List<Event> {
    return listOf(
        Event(
            1.toString(),
            "BDE Soirée",
            "Une soirée étudiante incroyable !",
            "2025-03-10",
            "Campus ISEN",
            "Soirée"
        ),
        Event(
            2.toString(),
            "Gala ISEN",
            "Le grand gala annuel de l'ISEN.",
            "2025-04-15",
            "Salle Prestige",
            "Gala"
        ),
        Event(
            3.toString(),
            "Journée Cohésion",
            "Une journée pour mieux se connaître.",
            "2025-02-20",
            "Parc ISEN",
            "Cohésion"
        )
    )
}

//Preview
@Preview(showBackground = true)
@Composable
fun PreviewEventsScreen() {
    ISENSmartCompanionTheme {
        EventsScreen(navController = rememberNavController())
    }
}


@Composable
fun UserInput(viewModel: MainViewModel = viewModel()) {
    var userInput by remember { mutableStateOf("") }
    val chatHistory by viewModel.chatHistory.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display chat history
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(chatHistory) { chatMessage ->
                Card(modifier = Modifier.padding(8.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Vous: ${chatMessage.userMessage}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "ISEN_BOT: ${chatMessage.aiResponse}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Input field and send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Entrez votre message") }
            )
            Button(
                onClick = {
                    if (userInput.isNotBlank()) {
                        viewModel.sendMessageToGemini(userInput) { }
                        userInput = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Envoyer")
            }
        }
    }
}
