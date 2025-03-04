package fr.isen.nathangorga.isensmartcompanion

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import fr.isen.nathangorga.isensmartcompanion.data.ChatDatabase
import fr.isen.nathangorga.isensmartcompanion.data.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val apiKey = BuildConfig.AI_API_KEY // API Key from BuildConfig
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    // Get the ChatDao instance from the database
    private val chatDao = ChatDatabase.getDatabase(application).chatDao()

    // Flow to observe chat history
    val chatHistory: Flow<List<ChatMessage>> = chatDao.getAllMessages()

    /**
     * Send user message to Gemini AI, store response in Room database.
     */
    fun sendMessageToGemini(userInput: String, onResult: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = model.generateContent(userInput)
                val reply = response.text ?: "Aucune réponse trouvée."

                // Save message to Room database
                val chatMessage = ChatMessage(userMessage = userInput, aiResponse = reply)
                chatDao.insertMessage(chatMessage)

                onResult(reply)
            } catch (e: Exception) {
                Log.e("AI_ERROR", "Erreur lors de l'appel à Gemini", e)
                onResult("Erreur : ${e.message}")
            }
        }
    }

    fun deleteMessage(chatMessage: ChatMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            chatDao.deleteMessage(chatMessage)
        }
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            chatDao.deleteAllMessages()
        }
    }
}


//package fr.isen.nathangorga.isensmartcompanion
//
//import android.app.Application
//import android.util.Log
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.ai.client.generativeai.GenerativeModel
//import fr.isen.nathangorga.isensmartcompanion.data.ChatDao
//import fr.isen.nathangorga.isensmartcompanion.data.ChatDatabase
//import fr.isen.nathangorga.isensmartcompanion.data.ChatMessage
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//
//class MainViewModel(application: Application) : AndroidViewModel(application){
//
//    private val apiKey = BuildConfig.AI_API_KEY // Clé API définie dans BuildConfig
//    private val model = GenerativeModel(
//        modelName = "gemini-1.5-flash",
//        apiKey = apiKey
//    )
//    private val chatDao = ChatDatabase.getDatabase(application).chatDao()
//    // Utilisation d'un StateFlow pour stocker les réponses
//
//    val chatHistory: Flow<List<ChatMessage>> = chatDao.getAllMessages()
//
//    private val _responses = MutableStateFlow<List<String>>(emptyList())
//    val responses: StateFlow<List<String>> = _responses
//
//    /**
//     * Envoie le message de l'utilisateur à Gemini et ajoute la réponse à la liste.
//     * Le paramètre onResult permet éventuellement de traiter la réponse dans l'UI.
//     */
//    fun sendMessageToGemini(userInput: String, onResult: (String) -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val response = model.generateContent(userInput)
//                val reply = response.text ?: "Aucune réponse trouvée."
//                // On ajoute le message utilisateur et la réponse de Gemini à la liste
//                _responses.value = _responses.value + "Vous : $userInput" + "\nISEN_BOT : $reply"
//                ChatDao.insertMessage(_responses.value)
//
//                onResult(reply)
//            } catch (e: Exception) {
//                Log.e("AI_ERROR", "Erreur lors de l'appel à Gemini", e)
//                _responses.value += "Erreur : ${e.message}"
//                onResult("Erreur : ${e.message}")
//            }
//        }
//    }
//    fun deleteMessage(chatMessage: ChatMessage) {
//        viewModelScope.launch(Dispatchers.IO) {
//            ChatDao.deleteMessage(chatMessage)
//        }
//    }
//
//    fun clearHistory() {
//        viewModelScope.launch(Dispatchers.IO) {
//            ChatDao.deleteAllMessages()
//        }
//    }
//}
