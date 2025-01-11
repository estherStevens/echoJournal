package stevens.software.echojournal.ui.create_journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import stevens.software.echojournal.R

class CreateJournalEntryViewModel: ViewModel() {

//    private val _uiState = MutableStateFlow(
//        CreateEntryUiState(
//            entryTitle = "",
//            entryDescription = "",
//            moods = allMoods()
//        )
//    )
//
    val allMoods = MutableStateFlow<List<SelectableMood>>(initialSetOfSelectableMoods())
    val selectedMood = MutableStateFlow<SelectableMood?>(null)

    val uiState = combine(allMoods, selectedMood) { moods, selectedMood ->
        CreateEntryUiState(
            entryTitle = "",
            entryDescription = "",
            moods = moods,
            selectedMood = null
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        CreateEntryUiState(
            entryTitle = "",
            entryDescription = "",
            moods = listOf(),
            selectedMood = null
        )
    )

//    var uiState : StateFlow<CreateEntryUiState> = _uiState.asStateFlow()

    fun updateEntryTitle(newEntryTitle: String){
        viewModelScope.launch{
            uiState.value.copy(
                entryTitle = newEntryTitle
            )
        }
    }

    fun updateEntryDescription(newEntryDescription: String){
        viewModelScope.launch{
            uiState.value.copy(
                entryDescription = newEntryDescription
            )
        }
    }

    fun updateSelectedMood(selectableMood: SelectableMood?) {
        viewModelScope.launch {
            selectedMood.emit(selectableMood)
        }
    }



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

}


data class CreateEntryUiState(val entryTitle: String, val entryDescription: String, val moods: List<SelectableMood>, val selectedMood: SelectableMood?)
data class SelectableMood(val id: Mood, val text: Int, val moodIcon: Int, val selectedMoodIcon: Int, var isMoodSelected: Boolean)
enum class Mood {
    EXCITED,
    PEACEFUL,
    NEUTRAL,
    SAD,
    STRESSED
}