package stevens.software.echojournal.ui.journal_entries

import android.Manifest
import android.os.Build
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import stevens.software.echojournal.PlaybackState
import stevens.software.echojournal.R
import stevens.software.echojournal.data.Topic
import stevens.software.echojournal.interFontFamily
import stevens.software.echojournal.ui.common.RecordingTrack
import stevens.software.echojournal.ui.create_journal.EntryTopic
import stevens.software.echojournal.ui.create_journal.Mood
import java.time.LocalDate
import kotlin.io.path.fileVisitor

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntriesScreen(
    viewModel: JournalEntriesViewModel = koinViewModel(),
    navigateToCreateEntry: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    JournalEntries(
        allMoods = uiState.value.allMoods,
        entries = uiState.value.entries,
        filteredMoods = uiState.value.filteredMoods,
        allTopics = uiState.value.allTopics,
        filteredTopics = uiState.value.filteredTopics,
        onStartRecording = { viewModel.startRecording() },
        onSaveRecording = {
            navigateToCreateEntry()
            viewModel.saveRecording()
        },
        onPlayClicked = { viewModel.playRecording(it) },
        onPauseClicked = { viewModel.pauseRecording() },
        onResumeClicked = { viewModel.resumeRecording() },
        onSelectedMoods = { viewModel.updateFilterMoods(it) },
        onSelectedTopics = { viewModel.updateFilterTopics(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JournalEntries(
    allMoods: List<EntryMood>,
    entries: List<EntryDateCategory>,
    filteredMoods: List<EntryMood>,
    allTopics: List<Topic>,
    filteredTopics: List<Topic>,
    onStartRecording: () -> Unit,
    onSaveRecording: () -> Unit,
    onPlayClicked: (Entry) -> Unit,
    onPauseClicked: (Entry) -> Unit,
    onResumeClicked: (Entry) -> Unit,
    onSelectedMoods: (List<EntryMood>) -> Unit,
    onSelectedTopics: (List<Topic>) -> Unit
) {
    val recordingBottomSheetState = rememberModalBottomSheetState()
    var showRecordingBottomSheet by remember { mutableStateOf(false) }

    val recordingPermissionState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onStartRecording()
                showRecordingBottomSheet = true
            }
        }
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onAddEntry = {
                    recordingPermissionState.launch(Manifest.permission.RECORD_AUDIO)
                }
            )
        },
        content = { contentPadding ->
            Box(
                modifier = Modifier
                    .background(backgroundColour())
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.entries_title),
                        fontFamily = interFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 26.sp,
                        color = colorResource(R.color.dark_black),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                    )

                    if (entries.isEmpty()) {
                        EmptyState(modifier = Modifier.weight(1f))
                    } else {
                        Spacer(Modifier.size(8.dp))
                        Row {
                            MoodsFilterPill(
                                allMoods = allMoods,
                                filteredMoods = filteredMoods,
                                onSelectedMoods = onSelectedMoods,
                            )
                            Spacer(Modifier.size(6.dp))
                            TopicsFilterPill(
                                allTopics = allTopics,
                                filteredTopics = filteredTopics,
                                onSelectedTopics = onSelectedTopics
                            )
                        }
                        Spacer(Modifier.size(8.dp))

                        LazyColumn {
                            entries.forEach{ entryDate ->
                                item {
                                    EntryDateCategory(entryDate.date)
                                }
                                items(entryDate.entries) { entry ->
                                    Row {
                                        Image(
                                            painterResource(entry.mood.moodIcon),
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 12.dp, top = 8.dp)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(color = Color.White)
                                        ) {

                                            Column(modifier = Modifier.padding(14.dp)) {
                                                Row {
                                                    Text(
                                                        text = entry.title,
                                                        fontWeight = FontWeight.Normal,
                                                        fontSize = 16.sp,
                                                        color = colorResource(R.color.dark_black),
                                                        fontFamily = interFontFamily,
                                                        modifier = Modifier.weight(2f)
                                                    )
                                                    Text(
                                                        text = entry.entryTime,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 14.sp,
                                                        color = colorResource(R.color.dark_grey),
                                                        fontFamily = interFontFamily
                                                    )
                                                }

                                                Spacer(Modifier.size(8.dp))

                                                RecordingTrack(
                                                    selectedMood = entry.mood.id,
                                                    position = entry.progressPosition,
                                                    currentPosition = entry.currentPosition,
                                                    trackDuration = entry.recording?.duration ?: 0L ,
                                                    playbackState = entry.playingState,
                                                    onPlayClicked = { onPlayClicked(entry) },
                                                    onPauseClicked = { onPauseClicked(entry) },
                                                    onResumeClicked = { onResumeClicked(entry) }
                                                )

                                                Spacer(Modifier.size(6.dp))

                                                Text(
                                                    text = entry.description,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 14.sp,
                                                    color = colorResource(R.color.dark_grey),
                                                    fontFamily = interFontFamily,
                                                    maxLines = 3, // todo show see more button
                                                    overflow = TextOverflow.Ellipsis,
                                                )

                                                Spacer(Modifier.size(6.dp))

                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)){
                                                    entry.topics.forEach{ topic ->
                                                        TopicPill(
                                                            topic = topic.topic
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer(Modifier.size(16.dp))
                                }

                            }

                        }

                    }
                }
            }

            if (showRecordingBottomSheet) {
                ModalBottomSheet(
                    containerColor = colorResource(R.color.bottom_sheet_dialog_bg),
                    onDismissRequest = {
                        showRecordingBottomSheet = false
                    },
                    sheetState = recordingBottomSheetState,
                    content = {
                        Box {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.add_entry_recording_title),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = interFontFamily,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                Spacer(Modifier.size(23.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(44.dp)
                                ) {
                                    CancelRecordingButton()
                                    SaveRecordingButton(
                                        onSaveRecording = onSaveRecording
                                    )
                                    PauseRecordingButton()
                                }
                            }
                        }
                    }
                )
            }
        }
    )
}

@Composable
fun EntryDateCategory(date: String){
    Text(
        text = date,
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = colorResource(R.color.dark_grey),
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun TopicsFilterPill(
    allTopics: List<Topic>,
    filteredTopics: List<Topic>,
    onSelectedTopics: (List<Topic>) -> Unit
) {
    var chipSelected by remember { mutableStateOf(false) }
    var topicsDropDownExpanded by remember { mutableStateOf(false) }
    var selectedTopics = remember { mutableStateListOf<Topic>() }

    val text = if(filteredTopics.isEmpty()) {
        stringResource(R.string.entries_pill_all_topics)
    } else {
        val stringBuilder = StringBuilder()
        stringBuilder.append(filteredTopics.take(2).toTopicText().joinToString(", "))
        if(filteredTopics.size >= 2){
            stringBuilder.append(" +${filteredTopics.size - 2}")
        }
        stringBuilder.toString()
    }

    FilterChip(
        selected = chipSelected,
        onClick = {
            topicsDropDownExpanded = true
            chipSelected = !chipSelected
        },
        label = {
            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                fontFamily = interFontFamily,
                fontSize = 16.sp
            )
        },
        colors = FilterChipDefaults.filterChipColors().copy(
            containerColor = Color.Transparent,
            selectedContainerColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = chipSelected,
            borderWidth = 1.dp,
            borderColor = colorResource(R.color.light_grey),
            selectedBorderColor = colorResource(R.color.dark_blue)
        ),
        shape = CircleShape
    )

    DropdownMenu(
        expanded = topicsDropDownExpanded,
        onDismissRequest = {
            topicsDropDownExpanded = false
            onSelectedTopics(selectedTopics) },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .requiredSizeIn(maxHeight = 400.dp) // todo - create my own Dropdown menu as this not ideal
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            ),
        containerColor = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        allTopics.forEach { topic ->
            val backgroundColour = if(selectedTopics.contains(topic)) colorResource(R.color.filter_list_bg).copy(alpha = 0.05f) else Color.Transparent

            Box(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
                DropdownMenuItem(
                    onClick = {
                        topicsDropDownExpanded = true
                        if(selectedTopics.contains(topic)) {
                            selectedTopics.remove(topic)
                        } else {
                            selectedTopics.add(topic)
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    modifier = Modifier.background(color = backgroundColour, shape = RoundedCornerShape(8.dp)),
                    trailingIcon = {
                        if(selectedTopics.contains(topic)) {
                            Icon(
                                painter = painterResource(R.drawable.tick),
                                contentDescription = null
                            )
                        }
                    },
                    text = {
                        Text(
                            text = topic.topic,
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
        }

    }
}

@Composable
fun MoodsFilterPill(
    allMoods: List<EntryMood>,
    filteredMoods: List<EntryMood>,
    onSelectedMoods: (List<EntryMood>) -> Unit
) {
    var chipSelected by remember { mutableStateOf(false) }
    var moodsDropDownExpanded by remember { mutableStateOf(false) }
    var selectedMoods = remember { mutableStateListOf<EntryMood>() }


    val text = if(filteredMoods.isEmpty()) {
        stringResource(R.string.entries_pill_all_moods)
    } else {
        val stringBuilder = StringBuilder()
        stringBuilder.append(filteredMoods.take(2).toMoodText().joinToString(", "))
        if(filteredMoods.size >= 2){
            stringBuilder.append(" +${filteredMoods.size - 2}")
        }
        stringBuilder.toString()
    }

    FilterChip(
        selected = chipSelected,
        onClick = {
            moodsDropDownExpanded = true
            chipSelected = !chipSelected
        },
        label = {
            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                fontFamily = interFontFamily,
                fontSize = 16.sp
            )
        },
        leadingIcon = {
            if(filteredMoods.isNotEmpty()) {
                Row {
                    filteredMoods.forEach {
                        Icon(
                            painterResource(it.moodIcon),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        },
        colors = FilterChipDefaults.filterChipColors().copy(
            containerColor = Color.Transparent,
            selectedContainerColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = chipSelected,
            borderWidth = 1.dp,
            borderColor = colorResource(R.color.light_grey),
            selectedBorderColor = colorResource(R.color.dark_blue)
        ),
        shape = CircleShape
    )

    DropdownMenu(
        expanded = moodsDropDownExpanded,
        onDismissRequest = {
            moodsDropDownExpanded = false
            onSelectedMoods(selectedMoods) },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            ),
        containerColor = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        allMoods.forEach { mood ->
            val backgroundColour = if(selectedMoods.contains(mood)) colorResource(R.color.filter_list_bg).copy(alpha = 0.05f) else Color.Transparent

            Box(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
                DropdownMenuItem(
                    onClick = {
                        moodsDropDownExpanded = true
                        if(selectedMoods.contains(mood)) {
                            selectedMoods.remove(mood)
                        } else {
                            selectedMoods.add(mood)
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    modifier = Modifier.background(color = backgroundColour, shape = RoundedCornerShape(8.dp)),
                    trailingIcon = {
                        if(selectedMoods.contains(mood)) {
                            Icon(
                                painter = painterResource(R.drawable.tick),
                                contentDescription = null
                            )
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(mood.text),
                            fontWeight = FontWeight.Medium,
                            fontFamily = interFontFamily,
                            fontSize = 14.sp,
                            color = colorResource(R.color.denim_blue)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(mood.moodIcon),
                            tint = Color.Unspecified,
                            contentDescription = null
                        )
                    }
                )
            }
        }

    }
}


@Composable
fun backgroundColour() = Brush.verticalGradient(
    listOf(
        colorResource(R.color.very_light_blue_gradient2),
        colorResource(R.color.very_light_blue_gradient1)
    )
)

@Composable
fun CancelRecordingButton() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(colorResource(R.color.light_red))
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(R.drawable.cancel_recording),
            tint = Color.Unspecified,
            contentDescription = ""
        )
    }
}

@Composable
fun SaveRecordingButton(
    onSaveRecording: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
            .clickable {
                onSaveRecording()
            }
            .background(colorResource(R.color.light_blue_save_button_2)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(95.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.light_blue_save_button_1)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.dark_blue))
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(R.drawable.save_recording),
                    tint = Color.Unspecified,
                    contentDescription = ""
                )
            }
        }
    }
}

@Composable
fun PauseRecordingButton() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(colorResource(R.color.light_purple))
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(R.drawable.pause_recording),
            tint = Color.Unspecified,
            contentDescription = ""
        )
    }
}

@Composable
fun EmptyState(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.empty_state),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
            )
            Spacer(Modifier.size(34.dp))
            Text(
                text = stringResource(R.string.entries_empty_state_title),
                fontSize = 22.sp,
                color = colorResource(R.color.dark_black),
                fontFamily = interFontFamily,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = stringResource(R.string.entries_empty_state_subtitle),
                fontSize = 14.sp,
                color = colorResource(R.color.dark_grey),
                fontFamily = interFontFamily,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun FloatingActionButton(
    onAddEntry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 9.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        val brush = Brush.verticalGradient(
            listOf(
                colorResource(R.color.light_blue),
                colorResource(R.color.dark_blue)
            )
        )
        IconButton(
            onClick = onAddEntry,
            modifier = Modifier
                .clip(CircleShape)
                .background(brush)
        ) {
            Icon(
                painter = painterResource(R.drawable.add_icon),
                tint = Color.White,
                contentDescription = null
            )
        }
    }
}

@Composable
fun TopicPill(topic: String){
    Box(
        modifier = Modifier.background(color = colorResource(R.color.pale_grey), shape = CircleShape)
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically){
            Icon(
                painter = painterResource(R.drawable.topic_icon),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Text(
                text = topic,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = interFontFamily,
                color = colorResource(R.color.dark_grey),
                modifier = Modifier
            )
        }
    }
}

@Composable
fun List<Topic>.toTopicText() : MutableList<String> {
    val topics = mutableListOf<String>()
    this.forEach {
        topics.add(it.topic)
    }
    return topics
}


@Composable
fun List<EntryMood>.toMoodText() : MutableList<String> {
    val context = LocalContext.current
    val moods = mutableListOf<String>()
    this.forEach {
        moods.add(context.getString(it.text))
    }
    return moods
}


fun EntryMood.toText() =
    this.text

@Composable
@Preview(showSystemUi = true)
fun Preview() {
    MaterialTheme {
        JournalEntries(
            allMoods = listOf(),
            allTopics = listOf(),
            entries = listOf(EntryDateCategory(date = "Today", entries = listOf(
                Entry(
                    mood = EntryMood(id = Mood.EXCITED, text = R.string.entries_mood_excited, moodIcon = 0),
                    title = "New Entry",
                    recordingFileName = "",
                    description = "",
                    entryTime = "",
                    entryDate = LocalDate.now(),
                    recording = null,
                    playingState = PlaybackState.STOPPED,
                    progressPosition = 0f,
                    currentPosition = 0,
                    topics = listOf(
                        EntryTopic(
                            topic = "New Topic"
                        )
                    )
                )
            ))),
            onStartRecording = {},
            onSaveRecording = {},
            onPlayClicked = {},
            onPauseClicked = {},
            onResumeClicked = {},
            onSelectedMoods = {},
            filteredMoods = listOf(),
            filteredTopics = listOf(),
            onSelectedTopics = {}
        )
    }
}

