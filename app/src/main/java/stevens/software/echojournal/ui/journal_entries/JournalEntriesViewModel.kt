package stevens.software.echojournal.ui.journal_entries

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import stevens.software.echojournal.MediaPlayer
import stevens.software.echojournal.PlaybackState
import stevens.software.echojournal.R
import stevens.software.echojournal.Recording
import stevens.software.echojournal.VoiceRecorder
import stevens.software.echojournal.data.EntryWithTopics
import stevens.software.echojournal.data.JournalEntry
import stevens.software.echojournal.data.Topic
import stevens.software.echojournal.data.repositories.JournalEntriesRepository
import stevens.software.echojournal.data.repositories.MoodsRepository
import stevens.software.echojournal.ui.create_journal.EntryTopic
import stevens.software.echojournal.ui.create_journal.Mood
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.map

@RequiresApi(Build.VERSION_CODES.S)
class JournalEntriesViewModel(
    private val voiceRecorder: VoiceRecorder,
    private val journalEntriesRepository: JournalEntriesRepository,
    private val moodsRepository: MoodsRepository,
    private val mediaPlayer: MediaPlayer
) : ViewModel() {

    private val isLoading: MutableStateFlow<Boolean> = MutableStateFlow<Boolean>(true)
    private val filteredMoods: MutableStateFlow<List<EntryMood>> = MutableStateFlow<List<EntryMood>>(listOf())
    private val journalEntries: StateFlow<List<EntryWithTopics>> = journalEntriesRepository.getAllEntriesWithTopics().stateIn(
        viewModelScope, SharingStarted.Eagerly, listOf() //dunno if this needs to be a stateflow or flow
    )

    val uiState = combine(
        journalEntries,
        filteredMoods,
        isLoading,
        mediaPlayer.playingTrack,
    ) { entriesWithTopics, filteredMoods, isLoading, playingState ->
        JournalEntriesUiState(
            allMoods = moodsRepository.getAllMoods(),
            filteredMoods = filteredMoods,
            entries = groupEntriesByDate(
                entries = entriesWithTopics.map { it.toEntry(playingState) },
            ),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        JournalEntriesUiState(
            allMoods = listOf(),
            filteredMoods = listOf(),
            entries = listOf(),
        )
    )

    fun groupEntriesByDate(entries: List<Entry>) : List<EntryDateCategory>{
        return entries
            .sortedByDescending { it.entryDate }
            .sortedByDescending { it.entryTime }
            .groupBy { it.entryDate }.map {
            EntryDateCategory(
                date = getDate(it.key),
                entries = it.value
            )
        }
    }

    fun startRecording() = voiceRecorder.startRecording()

    fun EntryWithTopics.toEntry(playbackState: PlayingTrack?) : Entry {
        var recording = voiceRecorder.getRecording(this.entry.recordingFilePath)
        var playingState = PlaybackState.STOPPED
        var progressPosition = 0f
        var currentPosition = 0L
        if(playbackState?.file == this.entry.recordingFilePath) {
            playingState = playbackState.playbackState
            progressPosition = playbackState.progressPosition
            currentPosition = playbackState.currentPosition
        }

        return Entry(
            title = this.entry.title,
            recordingFileName = this.entry.recordingFilePath,
            description = this.entry.description,
            entryTime = getTime(this.entry.timeOfEntry),
            entryDate = this.entry.timeOfEntry.toLocalDate(),
            mood = moodsRepository.toEntryMood(this.entry.mood),
            recording = recording,
            playingState = playingState,
            progressPosition = progressPosition,
            currentPosition = currentPosition,
            topics = this.topics.map { it.toEntryTopic() }
        )
    }

    fun updateFilterMoods(mood: List<EntryMood>) {
        viewModelScope.launch{
            filteredMoods.emit(mood)
        }
    }

    fun playRecording(entry: Entry){
        mediaPlayer.playFile(entry.recording?.contentUri, entry.recording?.name.toString())
    }

    fun pauseRecording(){
        mediaPlayer.pauseRecording()
    }

    fun resumeRecording(){
        mediaPlayer.resumeRecording()
    }

    fun saveRecording(){
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
}

fun Topic.toEntryTopic() = EntryTopic(
    topic = this.topic
)

data class JournalEntriesUiState(
    val allMoods: List<EntryMood>,
    val filteredMoods: List<EntryMood>,
    val entries: List<EntryDateCategory>
)

data class Entry(
    val mood: EntryMood,
    val title: String,
    val recordingFileName: String,
    val description: String,
    val entryTime: String,
    val entryDate: LocalDate,
    val recording: Recording?,
    val playingState: PlaybackState,
    val progressPosition: Float,
    val currentPosition: Long,
    val topics: List<EntryTopic>
)
data class EntryMood(val id: Mood, val text: Int, val moodIcon: Int, var selected: Boolean? = false)
data class EntryDateCategory(val date: String, val entries : List<Entry>)
data class PlayingTrack(val file: String, val playbackState: PlaybackState, val progressPosition: Float, val currentPosition: Long)
