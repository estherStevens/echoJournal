package stevens.software.echojournal.data.repositories

import android.content.Context
import kotlinx.coroutines.flow.Flow
import stevens.software.echojournal.data.JournalEntriesDatabase
import stevens.software.echojournal.data.JournalEntry
import kotlin.collections.sortedBy

class JournalEntriesRepositoryImplementation(
    private val context: Context
) : JournalEntriesRepository {
        private val journalEntriesDao = JournalEntriesDatabase.getDatabase(context).journalEntryDao()

    override suspend fun addJournalEntry(entry: JournalEntry) {
        val i = journalEntriesDao.insert(entry)
        val o = i
    }

    override fun getAllJournalEntries(): Flow<List<JournalEntry>> {
       return journalEntriesDao.getAllEntries()
    }

}