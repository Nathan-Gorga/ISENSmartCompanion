package fr.isen.nathangorga.isensmartcompanion

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("events.json")
    fun getEvents(): Call<List<Event>>
}
