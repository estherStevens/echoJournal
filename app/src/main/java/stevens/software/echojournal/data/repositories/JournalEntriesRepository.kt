package stevens.software.echojournal.data.repositories

import kotlinx.coroutines.flow.Flow
import stevens.software.echojournal.data.JournalEntry

interface JournalEntriesRepository {

    suspend fun addJournalEntry(entry: JournalEntry)

    fun getAllJournalEntries(): Flow<List<JournalEntry>>



}






