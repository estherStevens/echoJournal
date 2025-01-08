package stevens.software.echojournal.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val recordingFilePath: String,
    val description: String
)