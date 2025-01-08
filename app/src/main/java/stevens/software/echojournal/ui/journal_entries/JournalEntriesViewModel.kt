package stevens.software.echojournal.ui.journal_entries

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import stevens.software.echojournal.VoiceRecorder
import stevens.software.echojournal.data.JournalEntry
import stevens.software.echojournal.data.repositories.JournalEntriesRepository
import java.io.File
import stevens.software.echojournal.R

@RequiresApi(Build.VERSION_CODES.S)
class JournalEntriesViewModel(
    val context: Context,
    private val voiceRecorder: VoiceRecorder,
    private val journalEntriesRepository: JournalEntriesRepository
) : ViewModel() {

    private val isLoading = MutableStateFlow<Boolean>(true)


    val uiState = combine(
        journalEntriesRepository.getAllJournalEntries(),
        isLoading,
    ) { entries, isLoading  ->
        JournalEntriesUiState(
            moods = allMoods(),
            entries = entries.map { it.toEntry() },
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        JournalEntriesUiState(
            moods = listOf(),
            entries = listOf(),
        )
    )


    fun startRecording(){
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS), "newwwwhhh.mp4")

        voiceRecorder.startRecording(file)

    }

    fun allMoods(): List<Mood> {
       return listOf(
            Mood(
                text = R.string.entries_mood_excited,
                moodIcon = R.drawable.excited_mood
            ),
            Mood(
                text = R.string.entries_mood_peaceful,
                moodIcon = R.drawable.peaceful_mood
            ),
            Mood(
                text = R.string.entries_mood_neutral,
                moodIcon = R.drawable.neutral_mood
            ),
            Mood(
                text = R.string.entries_mood_sad,
                moodIcon = R.drawable.sad_mood
            ),
            Mood(
                text = R.string.entries_mood_stressed,
                moodIcon = R.drawable.stressed_mood
            ),
        )
    }

    fun JournalEntry.toEntry() = Entry(
        title = this.title,
        recordingFileName = this.recordingFilePath,
        description = this.description
    )

    fun stopRecording(){
        voiceRecorder.stopRecording()


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
    val moods: List<Mood>,
    val entries: List<Entry>
)
data class Entry(val title: String, val recordingFileName: String, val description: String)
data class Mood(val text: Int, val moodIcon: Int)