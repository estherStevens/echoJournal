package stevens.software.echojournal.ui.create_journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import stevens.software.echojournal.MediaPlayer
import stevens.software.echojournal.PlaybackState
import stevens.software.echojournal.R
import stevens.software.echojournal.VoiceRecorder
import stevens.software.echojournal.data.JournalEntry
import stevens.software.echojournal.data.repositories.JournalEntriesRepository
import java.time.OffsetDateTime

class CreateJournalEntryViewModel(
    val journalEntriesRepository: JournalEntriesRepository,
    val voiceRecorder: VoiceRecorder,
    val mediaPlayer: MediaPlayer
): ViewModel() {

    val entryTitle = MutableStateFlow("")
    val entryDescription = MutableStateFlow("")
    val allMoods = MutableStateFlow<List<SelectableMood>>(initialSetOfSelectableMoods())
    val selectedMood = MutableStateFlow<SelectableMood?>(null)
    val isSaveButtonEnabled = MutableStateFlow<Boolean>(false)

    val uiState = combine(
        entryTitle,
        entryDescription,
        allMoods,
        selectedMood,
        isSaveButtonEnabled,
        mediaPlayer.playingState)
    { entryTitle, entryDescription, moods, selectedMood, saveButtonEnabled, playingState ->
        CreateEntryUiState(
            entryTitle = entryTitle,
            entryDescription = entryDescription,
            moods = moods,
            selectedMood = selectedMood,
            saveButtonEnabled = saveButtonEnabled,
            file = voiceRecorder.filePath,
            isPlaying = playingState.isPlaying(),
            isPaused = playingState.isPaused()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        CreateEntryUiState(
            entryTitle = "",
            entryDescription = "",
            moods = listOf(),
            selectedMood = null,
            saveButtonEnabled = false,
            file = "",
            isPlaying = false,
            isPaused = true
        )
    )


    fun PlaybackState.isPlaying() : Boolean = this == PlaybackState.PLAYING
    fun PlaybackState.isPaused() : Boolean = this == PlaybackState.PAUSED

    fun updateEntryTitle(newEntryTitle: String){
        viewModelScope.launch{
            val saveButtonEnabled = newEntryTitle != "" && uiState.value.entryDescription != "" && uiState.value.selectedMood != null
            entryTitle.emit(newEntryTitle)
            isSaveButtonEnabled.emit(saveButtonEnabled)
        }
    }

    fun updateEntryDescription(newEntryDescription: String){
        val saveButtonEnabled = uiState.value.entryTitle != "" && newEntryDescription != "" && uiState.value.selectedMood != null

        viewModelScope.launch{
            entryDescription.emit(newEntryDescription)
            isSaveButtonEnabled.emit(saveButtonEnabled)
        }
    }

    fun updateSelectedMood(selectableMood: SelectableMood?) {
        val saveButtonEnabled = uiState.value.entryTitle != "" && uiState.value.entryDescription != "" && selectableMood != null

        viewModelScope.launch {
            selectedMood.emit(selectableMood)
            isSaveButtonEnabled.emit(saveButtonEnabled)
        }
    }

    fun saveEntry() {
        viewModelScope.launch{
            journalEntriesRepository.addJournalEntry(uiState.value.toJournalEntry()) //todo error handling
        }
    }

    fun playFile(){
        mediaPlayer.playFile(voiceRecorder.recordingUri)
    }

    fun pauseRecording(){
        mediaPlayer.pauseRecording()
    }


    fun resumeRecording(){
        mediaPlayer.resumeRecording()
    }


    fun CreateEntryUiState.toJournalEntry() =
        JournalEntry(
            title = this.entryTitle,
            recordingFilePath = "",
            description = this.entryDescription,
            timeOfEntry = OffsetDateTime.now(),
            mood = this.selectedMood?.id ?: Mood.NONE // todo - need to find solution for the null selected mood on start
        )



    private fun initialSetOfSelectableMoods() = listOf(
            SelectableMood(
                id = Mood.EXCITED,
                text = R.string.entries_mood_excited,
                moodIcon = R.drawable.unselected_excited_mood,
                selectedMoodIcon = R.drawable.selected_excited_mood,
                isMoodSelected = false
            ),
            SelectableMood(
                id = Mood.PEACEFUL,
                text = R.string.entries_mood_peaceful,
                moodIcon = R.drawable.unselected_peaceful_mood,
                selectedMoodIcon = R.drawable.selected_peaceful_mood,
                isMoodSelected = false
            ),
            SelectableMood(
                id = Mood.NEUTRAL,
                text = R.string.entries_mood_neutral,
                moodIcon = R.drawable.unselected_neutral_mood,
                selectedMoodIcon = R.drawable.selected_neutral_mood,
                isMoodSelected = false
            ),
            SelectableMood(
                id = Mood.SAD,
                text = R.string.entries_mood_sad,
                moodIcon = R.drawable.unselected_sad_mood,
                selectedMoodIcon = R.drawable.selected_sad_mood,
                isMoodSelected = false
            ),
            SelectableMood(
                id = Mood.STRESSED,
                text = R.string.entries_mood_stressed,
                moodIcon = R.drawable.unselected_stressed_mood,
                selectedMoodIcon = R.drawable.selected_stressed_mood,
                isMoodSelected = false
            ),
        )

    fun <T1, T2, T3, T4, T5, T6, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        transform: suspend (T1, T2, T3, T4, T5, T6) -> R
    ): Flow<R> = combine(
        combine(flow, flow2, flow3, ::Triple),
        combine(flow4, flow5, flow6, ::Triple)
    ) { t1, t2 ->
        transform(
            t1.first,
            t1.second,
            t1.third,
            t2.first,
            t2.second,
            t2.third
        )
    }

}



data class CreateEntryUiState(
    val entryTitle: String,
    val entryDescription: String,
    val moods: List<SelectableMood>,
    val selectedMood: SelectableMood?,
    val file: String,
    val saveButtonEnabled: Boolean,
    val isPlaying: Boolean,
    val isPaused: Boolean)
data class SelectableMood(val id: Mood, val text: Int, val moodIcon: Int, val selectedMoodIcon: Int, var isMoodSelected: Boolean)
enum class Mood {
    EXCITED,
    PEACEFUL,
    NEUTRAL,
    SAD,
    STRESSED,
    NONE
}