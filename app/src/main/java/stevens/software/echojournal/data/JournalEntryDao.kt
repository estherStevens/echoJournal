package stevens.software.echojournal.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {
    @Insert
    suspend fun insert(entry: JournalEntry) : Long

    @Delete
    suspend fun delete(entry: JournalEntry)

    @Update
    suspend fun update(entry: JournalEntry)

    @Query("Select * from entries")
    fun getAllEntries() : Flow<List<JournalEntry>>

//    @Transaction
//    @Query("SELECT * FROM entries")
//    fun getEntriesWithTopics():  Flow<List<EntryWithTopics>>

    @Transaction
    @Query("SELECT * FROM entries")
    fun getEntriesWithTopics(): Flow<List<EntryWithTopics>>

    @Insert
    suspend fun insertEntryWithTopics(entry: EntryTopicsCrossRef) : Long

}