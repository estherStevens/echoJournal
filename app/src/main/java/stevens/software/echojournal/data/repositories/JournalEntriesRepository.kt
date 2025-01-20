package stevens.software.echojournal.data.repositories

import kotlinx.coroutines.flow.Flow
import stevens.software.echojournal.data.EntryTopicsCrossRef
import stevens.software.echojournal.data.EntryWithTopics
import stevens.software.echojournal.data.JournalEntry

interface JournalEntriesRepository {

    suspend fun addJournalEntry(entry: JournalEntry) : Long

    fun getAllJournalEntries(): Flow<List<JournalEntry>>

    suspend fun insertEntryWithTopics(entryWithTopics: EntryTopicsCrossRef)

    fun getAllEntriesWithTopics() : Flow<List<EntryWithTopics>>

}






