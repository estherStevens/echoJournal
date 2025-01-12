package stevens.software.echojournal.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import stevens.software.echojournal.ui.create_journal.Mood

@Entity(tableName = "entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mood: Mood,
    val title: String,
    val recordingFilePath: String,
    val description: String
)