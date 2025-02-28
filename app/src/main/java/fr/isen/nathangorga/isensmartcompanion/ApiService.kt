package fr.isen.nathangorga.isensmartcompanion


import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    // If the JSON response is an array of events:
    // @GET("events.json")
    // suspend fun getEvents(): Response<List<Event>>

    // If the JSON response is an object with keys and Event values:
    @GET("events.json")
    suspend fun getEvents(): Response<List<Event>>
}
