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

    private val apiKey = BuildConfig.AI_API_KEY
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )


    private val chatDao = ChatDatabase.getDatabase(application).chatDao()


    val chatHistory: Flow<List<ChatMessage>> = chatDao.getAllMessages()


    fun sendMessageToGemini(userInput: String, onResult: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = model.generateContent(userInput)
                val reply = response.text ?: "Aucune réponse trouvée."


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


