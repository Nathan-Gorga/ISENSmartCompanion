package fr.isen.nathangorga.isensmartcompanion

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.isen.nathangorga.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

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
fun HistoryScreen() { //TODO : add log of events
    Text("Historique des événements", modifier = Modifier.padding(16.dp))
}


@Composable
fun EventItem(event: Event, onClick: () -> Unit) { //TODO : add event details
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
    // Utilisation de collectAsState pour observer les réponses du StateFlow
    val responses by viewModel.responses.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Affichage de l'historique du chat
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(responses) { response ->
                Card(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = response,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        // Zone de saisie et bouton d'envoi
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
                        // Ici, nous passons un lambda vide pour onResult, car on met à jour le StateFlow directement dans le ViewModel
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