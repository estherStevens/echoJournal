package stevens.software.echojournal.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Insert
    suspend fun insert(topic: Topic) : Long

    @Delete
    suspend fun delete(topic: Topic)

    @Update
    suspend fun update(topic: Topic)

    @Query("Select * from topics")
    fun getAllTopics() : Flow<List<Topic>>

    @Query("Select * from topics where topic = :topic")
    suspend fun getTopic(topic: String) : Topic

}