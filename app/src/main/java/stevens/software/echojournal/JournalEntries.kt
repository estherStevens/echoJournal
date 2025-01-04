package stevens.software.echojournal

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntries() {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val recordingPermissionState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                showBottomSheet = true
            }
        }
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onAddEntry = {
                    recordingPermissionState.launch(android.Manifest.permission.RECORD_AUDIO)
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

                    EmptyState(modifier = Modifier.weight(1f))
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
                                    SaveRecordingButton()
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
fun SaveRecordingButton() {
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
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
@Preview(showSystemUi = true)
fun Preview() {
    MaterialTheme {
        JournalEntries()
    }
}