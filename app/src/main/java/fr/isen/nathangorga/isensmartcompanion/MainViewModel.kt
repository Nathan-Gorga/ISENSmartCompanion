package fr.isen.nathangorga.isensmartcompanion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val apiKey = BuildConfig.AI_API_KEY // Clé API définie dans BuildConfig
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    // Utilisation d'un StateFlow pour stocker les réponses
    private val _responses = MutableStateFlow<List<String>>(emptyList())
    val responses: StateFlow<List<String>> = _responses

    /**
     * Envoie le message de l'utilisateur à Gemini et ajoute la réponse à la liste.
     * Le paramètre onResult permet éventuellement de traiter la réponse dans l'UI.
     */
    fun sendMessageToGemini(userInput: String, onResult: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = model.generateContent(userInput)
                val reply = response.text ?: "Aucune réponse trouvée."
                // On ajoute le message utilisateur et la réponse de Gemini à la liste
                _responses.value = _responses.value + "Vous : $userInput" + "\nISEN_BOT : $reply"
                onResult(reply)
            } catch (e: Exception) {
                Log.e("AI_ERROR", "Erreur lors de l'appel à Gemini", e)
                _responses.value += "Erreur : ${e.message}"
                onResult("Erreur : ${e.message}")
            }
        }
    }
}
