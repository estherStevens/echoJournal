package stevens.software.echojournal.ui.create_journal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import stevens.software.echojournal.R
import stevens.software.echojournal.interFontFamily
import stevens.software.echojournal.ui.journal_entries.JournalEntriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEntryScreen(
    onBackClicked: () -> Unit,
    viewModel: CreateJournalEntryViewModel = koinViewModel()
) {
    CreateEntry(
        onBackClicked = onBackClicked,
        onEntryTitleUpdated = {
            viewModel.updateEntryTitle(it)
        },
        onDescriptionUpdated = {
            viewModel.updateEntryDescription(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEntry(
    onBackClicked: () -> Unit,
    onEntryTitleUpdated: (String) -> Unit,
    onDescriptionUpdated: (String) -> Unit
){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.new_entry_title),
                        fontFamily = interFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        color = colorResource(R.color.dark_black),
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.back_icon),
                        tint = Color.Unspecified,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            onBackClicked()
                        }
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }, 
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CancelEntryButton(
                    onCancelEntry = {}
                )
                SaveEntryButton(
                    modifier = Modifier.weight(2f),
                    onSaveEntry = {}
                )
            }

        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_entry_icon),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.clickable {

                        }
                    )

                    EntryTitle(
                        onEntryTitleUpdated = onEntryTitleUpdated
                    )

                }
                Spacer(Modifier.size(16.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colorResource(R.color.light_purple))
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.play_recording_icon),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }

                Spacer(Modifier.size(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.pencil_icon),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                    EntryDescription(
                        onDescriptionUpdated = onDescriptionUpdated
                    )

                }


            }
        }
    }
}


@Composable
private fun EntryTitle(onEntryTitleUpdated: (String) -> Unit){
    var entryTitle by remember { mutableStateOf("") }
    TextField(
        value = entryTitle,
        onValueChange = {
            onEntryTitleUpdated(it)
            entryTitle = it
        },
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(
            fontFamily = interFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 26.sp,
            color = colorResource(R.color.dark_black)
        ),
        placeholder = {
            Text(
                text = stringResource(R.string.new_entry_add_title),
                fontFamily = interFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 26.sp,
                color = colorResource(R.color.light_grey)
            )
        },
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun EntryDescription(
    onDescriptionUpdated: (String) -> Unit
){
    var entryDescription by remember { mutableStateOf("") }

    TextField(
        value = entryDescription,
        onValueChange = {
            entryDescription = it
            onDescriptionUpdated(it)
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(R.string.new_entry_add_description),
                fontFamily = interFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = colorResource(R.color.light_grey)
            )
        },
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun CancelEntryButton(
    onCancelEntry: () -> Unit
){
    Button(
        onClick = onCancelEntry,
        colors =  ButtonDefaults.buttonColors().copy(
            containerColor = colorResource(R.color.light_purple)
        )){
        Text(
            text = stringResource(R.string.new_entry_cancel),
            color = colorResource(R.color.blue),
            fontFamily = interFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun SaveEntryButton(
    modifier: Modifier,
    onSaveEntry: () -> Unit){
    Button(
        onClick = onSaveEntry,
        modifier = modifier,
        colors =  ButtonDefaults.buttonColors().copy(
            containerColor = colorResource(R.color.disabled_gred)
        )){
        Text(
            text = stringResource(R.string.new_entry_save),
            color = colorResource(R.color.grey),
            fontFamily = interFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun Preview() {
    MaterialTheme {
        CreateEntry (
            {}, {}, {}
        )
    }
}