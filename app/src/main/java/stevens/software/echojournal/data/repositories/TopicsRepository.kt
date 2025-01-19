package stevens.software.echojournal.data.repositories

import kotlinx.coroutines.flow.Flow
import stevens.software.echojournal.data.JournalEntry
import stevens.software.echojournal.data.Topic

interface TopicsRepository {

    suspend fun addTopic(topic: Topic)

    fun getAllTopics(): Flow<List<Topic>>
}