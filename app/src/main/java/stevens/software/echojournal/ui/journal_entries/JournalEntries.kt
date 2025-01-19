package stevens.software.echojournal.ui.journal_entries

import android.Manifest
import android.os.Build
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import stevens.software.echojournal.R
import stevens.software.echojournal.interFontFamily
import stevens.software.echojournal.ui.common.RecordingTrack

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntriesScreen(
    viewModel: JournalEntriesViewModel = koinViewModel(),
    navigateToCreateEntry: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    JournalEntries(
        moods = uiState.value.moods,
        entries = uiState.value.entries,
        onStartRecording = { viewModel.startRecording() },
        onSaveRecording = {
            navigateToCreateEntry()
            viewModel.saveRecording()
        },
        onPlayClicked = {
            viewModel.playRecording(it)
        },
        onPauseClicked = {
            viewModel.pauseRecording()
        },
        onResumeClicked = {
            viewModel.resumeRecording()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JournalEntries(
    moods: List<EntryMood>,
    entries: List<EntryDateCategory>,
    onStartRecording: () -> Unit,
    onSaveRecording: () -> Unit,
    onPlayClicked: (Entry) -> Unit,
    onPauseClicked: (Entry) -> Unit,
    onResumeClicked: (Entry) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val recordingPermissionState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onStartRecording()
                showBottomSheet = true
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
                                moods = moods
                            )
                            Spacer(Modifier.size(6.dp))
                            TopicsFilterPill()
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
                                                    onPlayClicked = {
                                                        onPlayClicked(entry)
                                                    },
                                                    onPauseClicked = {
                                                        onPauseClicked(entry)
                                                    },
                                                    onResumeClicked = {
                                                        onResumeClicked(entry)
                                                    }
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

            if (showBottomSheet) {
                ModalBottomSheet(
                    containerColor = colorResource(R.color.bottom_sheet_dialog_bg),
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState,
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
fun TopicsFilterPill() {
    var selected by remember { mutableStateOf(false) }

    FilterChip(
        selected = selected,
        onClick = {
            selected = !selected
        },
        label = {
            Text(
                text = stringResource(R.string.entries_pill_all_topics),
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
            selected = selected,
            borderWidth = 1.dp,
            borderColor = colorResource(R.color.light_grey),
            selectedBorderColor = colorResource(R.color.dark_blue)
        ),
        shape = CircleShape
    )
}

@Composable
fun MoodsFilterPill(
    moods: List<EntryMood>
) {
    var selected by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf<EntryMood?>(null) }

    FilterChip(
        selected = selected,
        onClick = {
            expanded = true
            selected = !selected
        },
        label = {
            Text(
                text = stringResource(R.string.entries_pill_all_moods),
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
            selected = selected,
            borderWidth = 1.dp,
            borderColor = colorResource(R.color.light_grey),
            selectedBorderColor = colorResource(R.color.dark_blue)
        ),
        shape = CircleShape
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .background(
                color = Color.White, // Or your desired background color
                shape = RoundedCornerShape(8.dp)
            ),
        containerColor = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        moods.forEach { mood ->
            DropdownMenuItem(
                onClick = {
                    selectedOptionText = mood
                    expanded = true
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

//@Composable
//@Preview(showSystemUi = true)
//fun Preview() {
//    MaterialTheme {
//        JournalEntries(
//            moods = listOf(),
//            entries = listOf(EntryDateCategory(date = "Today", entries = listOf())),
//            {}, {},
//        )
//    }
//}

