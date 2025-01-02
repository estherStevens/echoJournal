package stevens.software.echojournal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun JournalEntries() {
    Box(
        modifier = Modifier.safeContentPadding()
    ) {
        Text(text = stringResource(R.string.entries_title))
    }

}

@Composable
@Preview(showSystemUi = true)
fun Preview(){
    MaterialTheme {
        JournalEntries()
    }
}