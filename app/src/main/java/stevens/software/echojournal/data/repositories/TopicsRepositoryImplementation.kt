package stevens.software.echojournal.data.repositories

import android.content.Context
import kotlinx.coroutines.flow.Flow
import stevens.software.echojournal.data.JournalEntriesDatabase
import stevens.software.echojournal.data.Topic

class TopicsRepositoryImplementation(
    private val context: Context
): TopicsRepository {
    private val topicsDao = JournalEntriesDatabase.getDatabase(context).topicDao()

    override suspend fun addTopic(topic: Topic) {
       topicsDao.insert(topic)
    }

    override fun getAllTopics(): Flow<List<Topic>> {
       return topicsDao.getAllTopics()
    }
}