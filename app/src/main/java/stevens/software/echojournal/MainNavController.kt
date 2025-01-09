package stevens.software.echojournal

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import stevens.software.echojournal.ui.create_journal.CreateEntryScreen
import stevens.software.echojournal.ui.journal_entries.JournalEntriesScreen

@Serializable
object JournalEntries

@Serializable
object CreateJournalEntry

@SuppressLint("NewApi")
@Composable
fun MainNavController() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = JournalEntries) {
        composable<JournalEntries> {
            JournalEntriesScreen(
                navigateToCreateEntry = {
                    navController.navigate(CreateJournalEntry)
                }
            )
        }
        composable<CreateJournalEntry> {
            CreateEntryScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }
    }
}