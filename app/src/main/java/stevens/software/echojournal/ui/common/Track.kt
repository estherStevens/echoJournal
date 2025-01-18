package stevens.software.echojournal.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import stevens.software.echojournal.PlaybackState
import stevens.software.echojournal.R
import stevens.software.echojournal.Recording
import stevens.software.echojournal.ui.create_journal.Mood
import stevens.software.echojournal.ui.create_journal.SelectableMood
import stevens.software.echojournal.ui.create_journal.TrackControlButton
import stevens.software.echojournal.ui.journal_entries.Entry

@Composable
fun RecordingTrack(
    selectedMood: Mood?,
    trackDuration: Float,
    position: Float,
    playbackState: PlaybackState,
    onPauseClicked: () -> Unit,
    onResumeClicked: () -> Unit,
    onPlayClicked: () -> Unit
){
    val i = playbackState
    val iconColor = when(selectedMood) {
        Mood.EXCITED -> colorResource(R.color.excited_mood)
        Mood.PEACEFUL -> colorResource(R.color.peaceful_mood)
        Mood.NEUTRAL -> colorResource(R.color.neutral_mood)
        Mood.SAD -> colorResource(R.color.sad_mood)
        Mood.STRESSED -> colorResource(R.color.stressed_mood)
        Mood.NONE -> colorResource(R.color.blue)
        null -> colorResource(R.color.blue)
    }

    val trackBackground = when(selectedMood) {
        Mood.EXCITED -> colorResource(R.color.excited_mood_track_background)
        Mood.PEACEFUL -> colorResource(R.color.peaceful_mood_track_background)
        Mood.NEUTRAL -> colorResource(R.color.neutral_mood_track_background)
        Mood.SAD -> colorResource(R.color.sad_mood_track_background)
        Mood.STRESSED -> colorResource(R.color.stressed_mood_track_background)
        Mood.NONE -> colorResource(R.color.very_light_blue_gradient2)
        null -> colorResource(R.color.very_light_blue_gradient2)
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(trackBackground)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            when(playbackState) {
                PlaybackState.PLAYING -> {
                    TrackControlButton(
                        icon = R.drawable.pause_recording_icon,
                        color = iconColor,
                        onClick = onPauseClicked
                    )
                }
                PlaybackState.PAUSED -> {
                    TrackControlButton(
                        icon = R.drawable.play_recording_icon,
                        color = iconColor,
                        onClick = onResumeClicked
                    )
                }
                PlaybackState.STOPPED -> {
                    TrackControlButton(
                        icon = R.drawable.play_recording_icon,
                        color = iconColor,
                        onClick = onPlayClicked

                    )
                }
            }
//            SeekBar(
//                trackDuration = trackDuration,
//                trackPosition = position)

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekBar(
    trackDuration: Float,
    trackPosition: Float
){
//    var position by remember { mutableStateOf(trackPosition) }
    Slider(
        value = trackPosition,
        onValueChange = { },
        modifier = Modifier,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        valueRange = 0f..trackDuration
    )
}
