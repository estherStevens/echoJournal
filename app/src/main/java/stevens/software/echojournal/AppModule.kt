package stevens.software.echojournal

import android.os.Build
import androidx.annotation.RequiresApi
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import stevens.software.echojournal.data.repositories.JournalEntriesRepository
import stevens.software.echojournal.data.repositories.JournalEntriesRepositoryImplementation
import stevens.software.echojournal.data.repositories.MoodsRepository
import stevens.software.echojournal.ui.create_journal.CreateJournalEntryViewModel
import stevens.software.echojournal.ui.journal_entries.JournalEntriesViewModel

@RequiresApi(Build.VERSION_CODES.S)
val appModule = module {
    viewModelOf(::JournalEntriesViewModel)
    viewModelOf(::CreateJournalEntryViewModel)
    singleOf(::VoiceRecorder)
    factoryOf(::MediaPlayer)
    singleOf(::JournalEntriesRepositoryImplementation) { bind<JournalEntriesRepository>() }
    factoryOf(::MoodsRepository)

}