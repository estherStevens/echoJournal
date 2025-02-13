package stevens.software.echojournal.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [JournalEntry::class, Topic::class, EntryTopicsCrossRef::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class JournalEntriesDatabase : RoomDatabase() {
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun topicDao(): TopicDao

    companion object {
        @Volatile
        private var Instance: JournalEntriesDatabase? = null

        fun getDatabase(context: Context): JournalEntriesDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, JournalEntriesDatabase::class.java, "entries_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

}