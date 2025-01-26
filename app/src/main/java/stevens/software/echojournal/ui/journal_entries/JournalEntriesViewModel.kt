package stevens.software.echojournal.ui.journal_entries

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
import stevens.software.echojournal.data.repositories.TopicsRepository
import stevens.software.echojournal.ui.create_journal.EntryTopic
import stevens.software.echojournal.ui.create_journal.Mood
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.filter
import kotlin.collections.map

@RequiresApi(Build.VERSION_CODES.S)
class JournalEntriesViewModel(
    private val voiceRecorder: VoiceRecorder,
    private val journalEntriesRepository: JournalEntriesRepository,
    private val moodsRepository: MoodsRepository,
    private val mediaPlayer: MediaPlayer,
    private val topicsRepository: TopicsRepository
) : ViewModel() {

    private val allTopics: StateFlow<List<Topic>> = topicsRepository.getAllTopics()
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    private val _filteredMoods = MutableStateFlow<MutableList<EntryMood>>(mutableListOf<EntryMood>())
    val filteredMoods: StateFlow<List<EntryMood>> = _filteredMoods.asStateFlow()

    private val _filteredTopics = MutableStateFlow<MutableList<Topic>>(mutableListOf<Topic>())
    val filteredTopics: StateFlow<List<Topic>> = _filteredTopics.asStateFlow()

    private val journalEntries : StateFlow<List<EntryWithTopics>> = journalEntriesRepository.getAllEntriesWithTopics()
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    private val filteredJournalEntries = combine(filteredMoods, journalEntries) { moods, entries ->
        filterEntriesByMood(moods, entries)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    val uiState = combine(
        filteredJournalEntries,
        filteredMoods,
        filteredTopics,
        allTopics,
        mediaPlayer.playingTrack,
    ) { entriesWithTopics, filteredMoods, filteredTopics, allTopics, playingState ->
        JournalEntriesUiState(
            allMoods = moodsRepository.getAllMoods(),
            allTopics = allTopics,
            filteredMoods = filteredMoods,
            filteredTopics = filteredTopics,
            entries = groupEntriesByDate(
                entries = entriesWithTopics.map { it.toEntry(playingState) },
            )
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        JournalEntriesUiState(
            allMoods = listOf(),
            filteredMoods = listOf(),
            filteredTopics = listOf(),
            allTopics = listOf(),
            entries = listOf(),
        )
    )

    fun filterEntriesByMood(moods: List<EntryMood>, entries: List<EntryWithTopics>) : List<EntryWithTopics>{
        if (moods.isEmpty()) return entries
        val entries = moods.flatMap { mood -> entries.filter { mood.id == it.entry.mood } }
        return entries.toList()
    }

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

    fun updateFilterTopics(topics: List<Topic>) {
        viewModelScope.launch{
            _filteredTopics.update {
                _filteredTopics.value.toMutableList().apply {
                    this.clear()
                    this.addAll(topics)
                }
            }
        }
    }

    fun updateFilterMoods(moods: List<EntryMood>) {
        viewModelScope.launch{
            _filteredMoods.update {
                _filteredMoods.value.toMutableList().apply {
                    this.clear()
                    this.addAll(moods)
                }
            }
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
    val allTopics: List<Topic>,
    val filteredTopics: List<Topic>,
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
