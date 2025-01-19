package stevens.software.echojournal.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "topics")
class Topic (
    @PrimaryKey(autoGenerate = true)
    val topicId: Int = 0,
    val topic: String
)

@Entity(primaryKeys = ["id", "topicId"])
data class EntryTopicsCrossRef(
    val id: Int,
    val topicId: Int
)

//data class EntryWithTopics(
//    @Embedded val journalEntry: JournalEntry,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "topicId"
//    )
//    val topics: List<Topic>
//)