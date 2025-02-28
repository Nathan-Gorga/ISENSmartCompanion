package fr.isen.nathangorga.isensmartcompanion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.isen.nathangorga.isensmartcompanion.ui.theme.ISENSmartCompanionTheme


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
    val items = listOf("home", "events", "history")
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                label = { Text(screen.replaceFirstChar { it.uppercase() }) },
                selected = navController.currentBackStackEntryAsState().value?.destination?.route == screen,
                onClick = { navController.navigate(screen) },
                icon = {}
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "home", modifier = modifier) {
        composable("home") { MainScreen() }
        composable("events") { EventsScreen(navController) }
        composable("history") { HistoryScreen() }


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
    val context = LocalContext.current
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }  // Track loading state
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Retrieve events from the web service
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.apiService.getEvents()
            if (response.isSuccessful && response.body() != null) {
                // Convert the response (Map<String, Event>) to List<Event>
                events = response.body() ?: emptyList()
                isLoading = false
            } else {
                errorMessage = "Error: ${response.code()} ${response.message()}"
                isLoading = false
            }
        } catch (e: Exception) {
            errorMessage = "Exception: ${e.message}"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Événements ISEN",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isLoading) {
            Text("Loading...", modifier = Modifier.padding(16.dp))
        } else if (errorMessage != null) {
            Text(
                "Failed to load events: $errorMessage",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn {
                items(events) { event ->
                    EventItem(event) { selectedEvent ->
                        val intent = Intent(context, EventDetailActivity::class.java).apply {
                            putExtra("event", selectedEvent)
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}





@Composable
fun HistoryScreen() { //TODO : add log of events
    Text("Historique des événements", modifier = Modifier.padding(16.dp))
}


@Composable
fun EventItem(event: Event, onClick: (Event) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(event) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, fontSize = 20.sp, color = Color.Black)
            Text(text = event.date, fontSize = 14.sp, color = Color.DarkGray)
            Text(text = event.location, fontSize = 14.sp, color = Color.Gray)
        }
    }
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
fun UserInput() { //TODO : log user inputs in history page
    var userInput by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (responseText.isNotEmpty()) {
            Text(
                text = responseText,
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Ask your question...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )

        )

        Button(
            onClick = {

                Toast.makeText(context, "Question Submitted", Toast.LENGTH_SHORT).show()

                // TODO : add API key to have real AI interaction
                responseText = "AI says: '${userInput.trim()}' is a great question!"
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.send),
                contentDescription = "Send",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}