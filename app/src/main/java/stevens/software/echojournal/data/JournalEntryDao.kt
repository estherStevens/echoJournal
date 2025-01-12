package stevens.software.echojournal.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
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
}