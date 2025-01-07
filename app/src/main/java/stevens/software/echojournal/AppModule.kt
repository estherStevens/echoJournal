package stevens.software.echojournal

import android.os.Build
import androidx.annotation.RequiresApi
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import stevens.software.echojournal.ui.journal_entries.JournalEntriesViewModel

@RequiresApi(Build.VERSION_CODES.S)
val appModule = module {
    viewModelOf(::JournalEntriesViewModel)
    factoryOf(::VoiceRecorder)
}