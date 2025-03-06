package fr.isen.nathangorga.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class AgendaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AppNavigation(navController)
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "agenda") {
        composable("agenda") { AgendaScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(navController: NavHostController) {
    val courses = remember { getStudentCourses() }
    val events = remember { getSubscribedEvents() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agenda") }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)) {
            item { Text("Your Courses", style = MaterialTheme.typography.headlineMedium) }
            items(courses) { course ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(course.title, style = MaterialTheme.typography.titleMedium)
                        Text("Time: ${course.time}")
                    }
                }
            }
            item { Text("Subscribed Events", style = MaterialTheme.typography.headlineMedium) }
            items(events) { event ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(event.title, style = MaterialTheme.typography.titleMedium)
                        Text("Date: ${event.date}")
                    }
                }
            }
        }
    }
}

// Mock data functions (Replace with real data fetching logic)
fun getStudentCourses(): List<Course> {
    return listOf(
        Course("Mathematics", "08:00 - 10:00"),
        Course("Physics", "10:30 - 12:30"),
        Course("Computer Science", "14:00 - 16:00")
    )
}

fun getSubscribedEvents(): List<Event> {
    return listOf(
        Event(
            "Tech Conference", "2025-03-10",
            description = "A conference on new technologies.",
            date = "2025-03-10",
            location = "ISEN Campus",
            category = "Tech"
        ),
        Event(
            "AI Workshop", "2025-03-15",
            description = "A workshop on AI and Machine Learning.",
            date = "2025-03-15",
            location = "ISEN Lab",
            category = "Workshop"
        )
    )
}

// Data classes for courses and events
data class Course(val title: String, val time: String)
