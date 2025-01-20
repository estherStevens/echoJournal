package stevens.software.echojournal.ui.create_journal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import stevens.software.echojournal.PlaybackState
import stevens.software.echojournal.R
import stevens.software.echojournal.Recording
import stevens.software.echojournal.interFontFamily
import stevens.software.echojournal.ui.common.RecordingTrack
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEntryScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateJournalEntryViewModel = koinViewModel()
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    CreateEntry(
        moods = uiState.value.moods,
        onNavigateBack = onNavigateBack,
        selectedMood = uiState.value.selectedMood,
        playbackState = uiState.value.playbackState,
        position = uiState.value.progressPosition,
        recording = uiState.value.recording,
        topics = uiState.value.topics,
        myTopics = uiState.value.entryTopics,
        currentPosition = uiState.value.currentPosition,
        saveButtonEnabled = uiState.value.saveButtonEnabled,
        onEntryTitleUpdated = {
            viewModel.updateEntryTitle(it)
        },
        onDescriptionUpdated = {
            viewModel.updateEntryDescription(it)
        },
        onMoodSelected = { mood ->
            viewModel.updateSelectedMood(mood)
        },
        onSaveEntry = {
            viewModel.saveEntry()
        },
        onPlayClicked = {
            viewModel.playFile()
        },
        onPauseClicked = {
            viewModel.pauseRecording()
        },
        onResumeClicked = {
            viewModel.resumeRecording()
        },
        onTopicChosen = {
            viewModel.updateEntryTopic(it)
        },
        onCreateTopic = {
            viewModel.saveTopic(it)
        },
        onRemoveTopic = {
            viewModel.removeEntryTopic(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEntry(
    moods: List<SelectableMood>,
    onNavigateBack: () -> Unit,
    saveButtonEnabled: Boolean,
    selectedMood: SelectableMood?,
    playbackState: PlaybackState,
    position: Float,
    topics: List<EntryTopic>,
    recording: Recording?,
    currentPosition: Long,
    myTopics: Set<EntryTopic>,
    onEntryTitleUpdated: (String) -> Unit,
    onDescriptionUpdated: (String) -> Unit,
    onMoodSelected: (SelectableMood?) -> Unit,
    onSaveEntry: () -> Unit,
    onPlayClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onResumeClicked: () -> Unit,
    onTopicChosen: (EntryTopic) -> Unit,
    onCreateTopic:(String) -> Unit,
    onRemoveTopic: (EntryTopic) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }

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
                            onNavigateBack()
                        }
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CancelButton(
                    onCancel = onNavigateBack
                )
                SaveEntryButton(
                    modifier = Modifier.weight(2f),
                    enabled = saveButtonEnabled,
                    onSaveEntry = {
                        onSaveEntry()
                        onNavigateBack() // todo error handling, only nav back if save is successful
                    }
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
                    val moodIcon = if(selectedMood == null) R.drawable.add_mood_icon else selectedMood.selectedMoodIcon
                    Icon(
                        painter = painterResource(moodIcon),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.clickable {
                            showBottomSheet = true
                        }
                    )

                    EntryTitle(
                        onEntryTitleUpdated = onEntryTitleUpdated
                    )

                }
                Spacer(Modifier.size(16.dp))

                RecordingTrack(
                    selectedMood = selectedMood?.id,
                    position = position,
                    currentPosition = currentPosition,
                    trackDuration = recording?.duration?.toLong() ?: 0L, //todo can prob get the duration from the media player rather than MediaStore
                    playbackState = playbackState,
                    onPlayClicked = onPlayClicked,
                    onPauseClicked = onPauseClicked,
                    onResumeClicked = onResumeClicked
                )

                Spacer(Modifier.size(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val chosenTopics by remember { mutableStateOf(myTopics) }

                    EntryTopic (
                        topics = topics,
                        myTopics = chosenTopics,
                        onCreateTopic = onCreateTopic,
                        onTopicChosen = onTopicChosen,
                        onRemoveTopic = onRemoveTopic
                    )
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

        if (showBottomSheet) {
            ChooseMoodBottomSheetDialog(
                moods = moods,
                onMoodSelected = {
                    onMoodSelected(it)
                    showBottomSheet = false
                },
                selectedMood = selectedMood,
                onDismissBottomSheet = {
                    showBottomSheet = false
                },
                onCancel = {
                    showBottomSheet = false
                })
        }
    }
}

@Composable
private fun EntryTitle(onEntryTitleUpdated: (String) -> Unit) {
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EntryTopic(
    topics: List<EntryTopic>,
    myTopics: Set<EntryTopic>,
    onTopicChosen: (EntryTopic) -> Unit,
    onRemoveTopic: (EntryTopic) -> Unit,
    onCreateTopic: (String) -> Unit
) {
    var topic by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(true) }
    val entryTopics = remember { mutableStateListOf<EntryTopic>()}
    val filteredTopics = topics.filter { it.topic.startsWith(topic) }

    val topicWithQuotes = "'$topic'" //todo find better way
    val text = String.format(LocalContext.current.getString(R.string.new_entry_create_topic), topicWithQuotes)
        FlowRow(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                painter = painterResource(R.drawable.topic_icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            entryTopics.forEach{ topic ->
                InputChip(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    selected = true,
                    onClick = {},
                    label = {
                        Text(
                            text = topic.topic,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = interFontFamily,
                            color = colorResource(R.color.dark_grey),
                        )
                    },
                    shape = CircleShape,
                    colors = InputChipDefaults.inputChipColors().copy(
                        selectedContainerColor = colorResource(R.color.pale_grey)
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.topic_icon),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.close_icon),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.clickable{
                                if(entryTopics.contains(topic)) {
                                    entryTopics.remove(topic)
                                }
                                onRemoveTopic(topic)
                            }
                        )
                    }
                )
            }

            TextField(
                value = topic,
                onValueChange = {
                    topic = it
                    expanded = it.isNotEmpty()
                },
                modifier = Modifier.defaultMinSize(minWidth = 10.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = stringResource(R.string.new_entry_add_topic),
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


    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        properties = PopupProperties(
            focusable = false,
            dismissOnClickOutside = false
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .requiredSizeIn(maxHeight = 180.dp) // todo - create my own Dropdown menu as this not good
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        filteredTopics.forEach { filteredTopic ->
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    topic = ""
                    if(!(entryTopics.contains(filteredTopic))) {
                        entryTopics.add(filteredTopic)
                    }
                    onTopicChosen(filteredTopic)
                },
                text = {
                    Text(
                        text = filteredTopic.topic,
                        fontWeight = FontWeight.Medium,
                        fontFamily = interFontFamily,
                        fontSize = 14.sp,
                        color = colorResource(R.color.denim_blue)
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.topic_icon),
                        tint = Color.Unspecified,
                        contentDescription = null
                    )
                }
            )
        }

        DropdownMenuItem(
            onClick = {
                expanded = false
                entryTopics.add(EntryTopic(topic))
                onCreateTopic(topic)
//                onTopicChosen(EntryTopic(topic))
                topic = ""
            },
            text = {
                Text(
                    text = text,
                    fontWeight = FontWeight.Medium,
                    fontFamily = interFontFamily,
                    fontSize = 14.sp,
                    color = colorResource(R.color.blue)
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.create_topic),
                    tint = Color.Unspecified,
                    contentDescription = null
                )
            }
        )

    }
}


@Composable
private fun CustomDropDownMenu(){
//    if(expanded) {
//        Box(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp))) {
//            LazyColumn {
//                items(topics) { topic ->
//                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
//                        Icon(
//                            painter = painterResource(R.drawable.topic_icon),
//                            contentDescription = null,
//                            tint = Color.Unspecified)
//                        Spacer(Modifier.size(6.dp))
//                        Text(
//                            text = topic.topic,
//                            fontWeight = FontWeight.Medium,
//                            fontFamily = interFontFamily,
//                            fontSize = 14.sp,
//                            color = colorResource(R.color.denim_blue)
//                        )
//                    }
//
//                }
//            }
//        }
//    }

}

@Composable
private fun EntryDescription(
    onDescriptionUpdated: (String) -> Unit
) {
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
private fun CancelButton(
    onCancel: () -> Unit
) {
    Button(
        onClick = onCancel,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = colorResource(R.color.light_purple)
        )
    ) {
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
    enabled: Boolean,
    onSaveEntry: () -> Unit,
) {
    val textColor = if(enabled) Color.White else colorResource(R.color.grey)
    Button(
        onClick = onSaveEntry,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = colorResource(R.color.dark_blue),
            disabledContainerColor = colorResource(R.color.disabled_gred)
        )
    ) {
        Text(
            text = stringResource(R.string.new_entry_save),
            color = textColor,
            fontFamily = interFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun ConfirmMoodButton(
    enabled: Boolean,
    modifier: Modifier,
    onConfirmMood: () -> Unit
) {
    val iconTint = if(enabled) Color.White else colorResource(R.color.grey)
    val textColour = if(enabled) Color.White else colorResource(R.color.grey)

    Button(
        onClick = onConfirmMood,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = colorResource(R.color.dark_blue),
            disabledContainerColor = colorResource(R.color.disabled_gred)
        )
    ) {
        Row{
            Icon(
                painterResource(R.drawable.confirm),
                contentDescription = null,
                tint = iconTint
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = stringResource(R.string.choose_mood_bottom_sheet_confirm),
                color = textColour,
                fontFamily = interFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChooseMoodBottomSheetDialog(
    moods: List<SelectableMood>,
    selectedMood: SelectableMood?,
    onMoodSelected: (SelectableMood?) -> Unit,
    onDismissBottomSheet: () -> Unit,
    onCancel: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismissBottomSheet,
        content = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.choose_mood_bottom_sheet_title),
                        fontSize = 22.sp,
                        fontFamily = interFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(R.color.dark_black)
                    )
                    Spacer(Modifier.size(32.dp))
                    SelectMood(
                        options = moods,
                        selectedMood = selectedMood,
                        updateSelectedMood = onMoodSelected,
                        onCancel = onCancel
                    )

                }
            }
        },
        sheetState = sheetState,
        containerColor = Color.White,
    )
}

@Composable
fun SelectMood(
    selectedMood: SelectableMood?,
    options: List<SelectableMood>,
    updateSelectedMood: (SelectableMood?) -> Unit,
//    onConfirmMood: (SelectableMood) -> Unit,
    onCancel: () -> Unit
) {

    var selectedOption by remember { mutableStateOf(selectedMood) }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        options.forEach { option ->
            MoodButton(
                selected = option == selectedOption,
                onClick = {
                    selectedOption = option
                },
                icon = option.moodIcon,
                text = option.text,
                selectedIcon = selectedOption?.selectedMoodIcon // Or any other selected icon
            )

        }
    }
    Spacer(Modifier.size(24.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CancelButton(
            onCancel = onCancel
        )
        ConfirmMoodButton(
            enabled = selectedOption != null,
            modifier = Modifier.weight(2f),
            onConfirmMood = {
                updateSelectedMood(selectedOption) //todo remove nullable type and just Mood.None instead
            }

        )
    }
}

@Composable
fun MoodButton(
    selected: Boolean,
    onClick: () -> Unit,
    icon: Int,
    text: Int,
    selectedIcon: Int?,
) {
    val textColor = if(selected) R.color.dark_black else R.color.grey
    val textWeight = if(selected) FontWeight.Medium else FontWeight.Normal
    val imageHeight = 34.dp

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            if (selected && selectedIcon != null) {
                Image(
                    painter = painterResource(selectedIcon),
                    contentDescription = null,
                    modifier = Modifier.height(imageHeight)
                )
            } else {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.height(imageHeight)
                )
            }

            Spacer(Modifier.size(10.dp))
            Text(
                text = stringResource(text),
                fontFamily = interFontFamily,
                color = colorResource(textColor),
                fontSize = 12.sp,
                fontWeight = textWeight
            )
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun Preview() {
    MaterialTheme {
        Column {
            Spacer(Modifier.size(40.dp))
            val i = EntryTopic(topic = "hey")
            EntryTopic(
                topics = listOf<EntryTopic>(),
                myTopics = setOf(EntryTopic(topic = "hey"), EntryTopic(topic = "yoo"), EntryTopic(topic = "topic")),
                {},
                {},
                {}
            )
        }

//        CreateEntry(
//            moods = listOf(),
//            onNavigateBack = {},
//            saveButtonEnabled = false,
//            selectedMood = SelectableMood(Mood.EXCITED, 0, 0, 0),
//            playbackState = PlaybackState.PLAYING,
//            currentPosition = 0L,
//            recording =  null,
//            topics = listOf(),
//            myTopics = setOf(),
//            onEntryTitleUpdated = {},
//            onDescriptionUpdated = {},
//            onMoodSelected = { },
//            position = 0f,
//            onSaveEntry = {},
//            onPlayClicked = {},
//            onPauseClicked = {},
//            onResumeClicked = {},
//            onTopicChosen = {},
//            onCreateTopic = {},
//            onRemoveTopic = {}
//        )
    }
}