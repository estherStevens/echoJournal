package stevens.software.echojournal.ui.journal_entries

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import stevens.software.echojournal.VoiceRecorder
import stevens.software.echojournal.data.JournalEntry
import stevens.software.echojournal.data.repositories.JournalEntriesRepository
import java.io.File
import stevens.software.echojournal.R
import stevens.software.echojournal.data.repositories.MoodsRepository
import stevens.software.echojournal.ui.create_journal.Mood
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
class JournalEntriesViewModel(
    val context: Context,
    private val voiceRecorder: VoiceRecorder,
    private val journalEntriesRepository: JournalEntriesRepository,
    private val moodsRepository: MoodsRepository
) : ViewModel() {

    private val isLoading = MutableStateFlow<Boolean>(true)

    val uiState = combine(
        journalEntriesRepository.getAllJournalEntries(),
        isLoading,
    ) { entries, isLoading  ->
        JournalEntriesUiState(
            moods = moodsRepository.getFilterMoods(),
            entries = groupEntriesByDate(entries.map { it.toEntry() })
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        JournalEntriesUiState(
            moods = listOf(),
            entries = listOf(),
        )
    )


    fun groupEntriesByDate(entries: List<Entry>) : List<EntryDateCategory>{
        return entries.sortedByDescending { it.entryDate }.groupBy {
            it.entryDate
        }.map {
            EntryDateCategory(
                date = getDate(it.key),
                entries = it.value
            )
        }

    }

    fun startRecording(){
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS), "newwwwhhh.mp4")
        voiceRecorder.startRecording(file)
    }


    fun JournalEntry.toEntry() = Entry(
        title = this.title,
        recordingFileName = this.recordingFilePath,
        description = this.description,
        entryTime = getTime(this.timeOfEntry),
        entryDate = this.timeOfEntry.toLocalDate(),
        mood = moodsRepository.toEntryMood(this.mood)
    )


    fun stopRecording(){
        voiceRecorder.stopRecording()

    }

    fun getTime(time: OffsetDateTime): String {
        val time = time.toLocalTime()
        return time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun getDate(time: LocalDate): String {
        val yesterday = LocalDate.now().minusDays(1)


       return  when{
            time == LocalDate.now() -> "TODAY" //todo - remove from viewmodel
            time == yesterday -> "YESTERDAY"
            else -> {
                val dayOfWeek = time.dayOfWeek
                val month = time.month
                val date = time.dayOfYear

                "$dayOfWeek, $month $date"
            }
        }
    }


    /* fun getMyRecordingEntries() : Flow<List<Entry>>{
         val path = context.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)
         val files = path?.walkTopDown()?.toList()?.filter {
             it.isFile
         } ?: emptyList()
         val entries = mutableListOf<Entry>()
         for(file in files) {
             entries.add(Entry(file.nameWithoutExtension))
         }
         return flowOf(entries)
     }*/

}

data class JournalEntriesUiState(
    val moods: List<EntryMood>,
    val entries: List<EntryDateCategory>
)


data class Entry(val mood: EntryMood, val title: String, val recordingFileName: String, val description: String, val entryTime: String, val entryDate: LocalDate)
data class EntryMood(val text: Int, val moodIcon: Int)
data class EntryDateCategory(val date: String, val entries : List<Entry>)

