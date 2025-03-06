package fr.isen.nathangorga.isensmartcompanion.data


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("SELECT * FROM chat_history ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessage>> // Flow allows auto updates in UI

    @Query("DELETE FROM chat_history")
    suspend fun deleteAllMessages()

    @Delete
    suspend fun deleteMessage(message: ChatMessage)
}
