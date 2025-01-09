package stevens.software.echojournal.ui.create_journal

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateJournalEntryViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(
        CreateEntryUiState(
            entryTitle = "",
            entryDescription = ""
        )
    )

    var uiState : StateFlow<CreateEntryUiState> = _uiState.asStateFlow()

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

    data class CreateEntryUiState(val entryTitle: String, val entryDescription: String)
}