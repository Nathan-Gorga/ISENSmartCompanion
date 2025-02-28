package fr.isen.nathangorga.isensmartcompanion


import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("events.json")
    suspend fun getEvents(): Response<List<Event>>
}
