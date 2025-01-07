package stevens.software.echojournal.data.repositories

import android.content.Context
import kotlinx.coroutines.flow.Flow
import stevens.software.echojournal.data.JournalEntriesDatabase
import stevens.software.echojournal.data.JournalEntry

class JournalEntriesRepositoryImplementation(
    private val context: Context
) : JournalEntriesRepository {
        private val journalEntriesDao = JournalEntriesDatabase.getDatabase(context).journalEntryDao()

    override suspend fun addJournalEntry(entry: JournalEntry) {
        journalEntriesDao.insert(entry)
    }

    override fun getAllJournalEntries(): Flow<List<JournalEntry>> {
       return journalEntriesDao.getAllEntries()
    }

}