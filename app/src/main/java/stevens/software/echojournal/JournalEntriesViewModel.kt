package stevens.software.echojournal

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.io.File

@RequiresApi(Build.VERSION_CODES.S)
class JournalEntriesViewModel(
    val context: Context,
    private val voiceRecorder: VoiceRecorder
) : ViewModel() {

    private val isLoading = MutableStateFlow<Boolean>(true)

    val uiState = combine(
        getMyRecordingEntries(),
        isLoading,
    ) { recordings, isLoading ->
        JournalEntriesUiState(
            entries = listOf()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        JournalEntriesUiState(
            entries = listOf(),
        )
    )


    fun startRecording(){
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS), "newwwwhhh.mp4")
        println("heyy " + file.absolutePath)

        voiceRecorder.startRecording(file)
    }

    fun stopRecording(){
        voiceRecorder.stopRecording()


    }

    fun getMyRecordingEntries() : Flow<List<Entry>>{
        val path = context.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)
        val files = path?.walkTopDown()?.toList()?.filter {
            it.isFile
        } ?: emptyList()
        val entries = mutableListOf<Entry>()
        for(file in files) {
            entries.add(Entry(file.nameWithoutExtension))
        }
        return flowOf(entries)
    }

}

data class JournalEntriesUiState(
    val entries: List<Entry>
)
data class Entry(val fileName: String)