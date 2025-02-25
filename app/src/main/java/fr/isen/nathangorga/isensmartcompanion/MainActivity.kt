package fr.isen.nathangorga.isensmartcompanion

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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

//

//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            ISENSmartCompanionTheme {
//                val navController = rememberNavController()
//                Scaffold(
//                    bottomBar = { BottomNavigationBar(navController)}
//                ) { innerPadding ->
//                    Column(modifier = Modifier.padding(innerPadding)) {
//                        TitleAndLogo()
//                        Spacer(modifier = Modifier.weight(1f))
//                        UserInput()
//
//
//                    }
//                }
//
//
//            }
//        }
//    }
//}
//
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Composable
//fun TitleAndLogo() {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.logo), // Load the image from resources
//            contentDescription = "App Logo",
//            modifier = Modifier.size(100.dp) // Set the image size
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(
//            text = "ISEN Smart Companion",
//            fontSize = 24.sp,
//            style = MaterialTheme.typography.headlineMedium
//        )
//
//
//    }
//}
//
//
//@Composable
//fun BottomNavigationBar(navController: NavHostController){
//    val items =listOf("home", "events", "history")
//    NavigationBar {
//        items.forEach{screen ->
//            NavigationBarItem(
//                label = { Text(screen.replaceFirstChar { it.uppercase()})},
//                selected= navController.currentBackStackEntryAsState().value?.destination?.route == screen,
//                onClick = { navController.navigate(screen) },
//                icon = { /* Add icons later if needed */ }
//            )
//        }
//    }
//}
//
//@Composable
//fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
//
//    NavHost(navController, startDestination = "home", modifier = modifier) {
//        composable("home") { MainScreen() }
//        composable("events") { EventsScreen(navController) } // Pass navController for navigation
//        composable("history") { HistoryScreen() }
//    }
//}
//
//@Composable
//fun MainScreen(){
//    Text("Welcome to Home Screen", modifier = Modifier.padding(16.dp))
//}
//
//fun getFakeEvents(): List<Event> {
//    return listOf(
//        Event(1, "BDE Party", "A fun night organized by the BDE.", "March 10, 2025", "ISEN Hall", "Party"),
//        Event(2, "Gala Night", "An elegant evening with music and dinner.", "April 15, 2025", "Grand Hotel", "Gala"),
//        Event(3, "Cohesion Day", "A day for students to bond and play games.", "May 5, 2025", "ISEN Campus", "Team Building")
//    )
//}
//
//@Composable
//fun EventsScreen(navController: NavHostController) {
//    val events = getFakeEvents()
//
//    LazyColumn {
//        items(events) { event ->
//            EventItem(event, onClick = { navController.navigate("event_detail") })
//        }
//    }
//
//
//}
//
//@Composable
//fun HistoryScreen() {
//    Text("History of events", modifier = Modifier.padding(16.dp))
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    ISENSmartCompanionTheme {
//        Greeting("Android")
//    }
//}
//
//@Composable
//fun EventItem(event: Event, onClick: () -> Unit) {
//    Card(
//        onClick = onClick,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(event.title, style = MaterialTheme.typography.headlineSmall)
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(event.date, style = MaterialTheme.typography.bodyMedium)
//            Text(event.location, style = MaterialTheme.typography.bodyMedium)
//        }
//    }
//    onClick = {
//        val intent = Intent(context, EventDetailActivity::class.java)
//        intent.putExtra("event_title", event.title)
//        context.startActivity(intent)
//    }
//
//}
//
//
//}
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            ISENSmartCompanionTheme {
//                val navController = rememberNavController()
//                Scaffold(
//                    bottomBar = { BottomNavigationBar(navController) }
//                ) { innerPadding ->
//                    NavigationGraph(navController, Modifier.padding(innerPadding))
//                }
//            }
//        }
//    }
//}


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
            painter = painterResource(id = R.drawable.logo), // Load the image from resources
            contentDescription = "App Logo",
            modifier = Modifier.size(100.dp) // Set the image size
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "ISEN Smart Companion",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium
        )


    }
}

// --- Navigation ---
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("home", "events", "history")
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
        composable("history") { HistoryScreen() }


    }
}

// --- Screens ---
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
    val events = getFakeEvents()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Événements ISEN", fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn {
            items(events) { event ->
                EventItem(event, onClick = {
                    navController.navigate("event_detail/${event.id}")
                })
            }
        }
    }
}

@Composable
fun HistoryScreen() {
    Text("Historique des événements", modifier = Modifier.padding(16.dp))
}

// --- Event Data Model & Fake Data ---
//data class Event(
//    val id: Int,
//    val title: String,
//    val description: String,
//    val date: String,
//    val location: String,
//    val category: String
//)
// --- Event Item ---
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


fun getFakeEvents(): List<Event> {
    return listOf(
        Event(
            1,
            "BDE Soirée",
            "Une soirée étudiante incroyable !",
            "2025-03-10",
            "Campus ISEN",
            "Soirée"
        ),
        Event(
            2,
            "Gala ISEN",
            "Le grand gala annuel de l'ISEN.",
            "2025-04-15",
            "Salle Prestige",
            "Gala"
        ),
        Event(
            3,
            "Journée Cohésion",
            "Une journée pour mieux se connaître.",
            "2025-02-20",
            "Parc ISEN",
            "Cohésion"
        )
    )
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun PreviewEventsScreen() {
    ISENSmartCompanionTheme {
        EventsScreen(navController = rememberNavController())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInput() {
    var userInput by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") } // Holds the AI's response
    val context = LocalContext.current // Get context for Toast

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //  AI Response is now displayed above the input field
        if (responseText.isNotEmpty()) {
            Text(
                text = responseText,
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp) // Space before input field
            )
        }

        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Ask your question...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp), // Space between input and button
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )

        )

        Button(
            onClick = {
                // Show a Toast message
                Toast.makeText(context, "Question Submitted", Toast.LENGTH_SHORT).show()

                // Simulate a fake AI response
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