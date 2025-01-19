package stevens.software.echojournal.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import stevens.software.echojournal.ui.create_journal.Mood
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mood: Mood,
    val title: String,
    val recordingFilePath: String,
    val description: String,
    val timeOfEntry: OffsetDateTime
)


data class EntryWithTopics(
    @Embedded val entry: JournalEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "topicId",
        associateBy = Junction(EntryTopicsCrossRef::class)
    )
    val topics: List<Topic>
)

class Converters {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }
}