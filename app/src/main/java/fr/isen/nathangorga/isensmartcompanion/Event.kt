package fr.isen.nathangorga.isensmartcompanion

import java.io.Serializable


data class Event(
    val category: String = "",
    val date: String = "",
    val description: String = "",
    val id: String = "",
    val location: String = "",
    val title: String = ""
) : Serializable

