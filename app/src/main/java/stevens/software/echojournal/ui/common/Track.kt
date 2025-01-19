package stevens.software.echojournal.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import stevens.software.echojournal.PlaybackState
import stevens.software.echojournal.R
import stevens.software.echojournal.interFontFamily
import stevens.software.echojournal.ui.create_journal.Mood
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun RecordingTrack(
    selectedMood: Mood?,
    position: Float,
    currentPosition: Long,
    trackDuration: Long,
    playbackState: PlaybackState,
    onPauseClicked: () -> Unit,
    onResumeClicked: () -> Unit,
    onPlayClicked: () -> Unit
){

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(selectedMood.toRecordingTrackBackgroundColour())
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when(playbackState) {
                PlaybackState.PLAYING -> {
                    TrackControlButton(
                        icon = R.drawable.pause_recording_icon,
                        color = selectedMood.toColour(),
                        onClick = onPauseClicked,
                    )
                }
                PlaybackState.PAUSED -> {
                    TrackControlButton(
                        icon = R.drawable.play_recording_icon,
                        color =  selectedMood.toColour(),
                        onClick = onResumeClicked
                    )
                }
                PlaybackState.STOPPED -> {
                    TrackControlButton(
                        icon = R.drawable.play_recording_icon,
                        color =  selectedMood.toColour(),
                        onClick = onPlayClicked

                    )
                }
            }
            SeekBar(
                modifier = Modifier.weight(2f),
                activeSliderColour = selectedMood.toColour(),
                inactiveSliderColour = selectedMood.toTrackInactiveColour(),
                trackPosition = position)

            val rounded : Long = 1000 * ((trackDuration + 500) / 1000)


            val duration = rounded.toDuration(DurationUnit.MILLISECONDS)
            val mins = duration.inWholeMinutes
            val secs = duration.minus(mins.toDuration(DurationUnit.MINUTES)).inWholeSeconds

            val minsText = String.format("%d", mins)
            val secsText = String.format("%02d", secs)

            println("heyyy " + secsText + " " + secs + " " + duration)


            val new_number : Long = 1000 * ((currentPosition + 500) / 1000)

            val currentPositionDuration = new_number.toDuration(DurationUnit.MILLISECONDS)
            val minsListenedTo = currentPositionDuration.inWholeMinutes
            val secsListenedTo = currentPositionDuration.minus(mins.toDuration(DurationUnit.MINUTES)).inWholeSeconds

            val minsListenedToText = String.format("%d", minsListenedTo)
            val secsListenedToText = String.format("%02d", secsListenedTo)


            Text(
                text = "$minsListenedToText:$secsListenedToText / $minsText:$secsText",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = interFontFamily,
                color = colorResource(R.color.dark_grey)
            )


        }


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekBar(
    modifier: Modifier,
    activeSliderColour: Color,
    inactiveSliderColour: Color,
    trackPosition: Float
){
    Slider(
        value = trackPosition,
        onValueChange = { },
        modifier = modifier.padding(horizontal = 6.dp),
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        valueRange = 0f..100f,
        thumb = {},
        track = { sliderState ->
            SliderDefaults.Track(
                modifier = Modifier.height(4.dp),
                sliderState = sliderState,
                drawStopIndicator = null,
                thumbTrackGapSize = 0.dp,
                colors = SliderDefaults.colors().copy(
                    activeTrackColor = activeSliderColour,
                    inactiveTrackColor = inactiveSliderColour
                )
            )
        }
    )
}

@Composable
fun TrackControlButton(icon: Int, color: Color, onClick: () -> Unit){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White)
            .size(32.dp)
            .padding(4.dp)

    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.clickable {
                onClick()
            }
        )
    }
}


@Preview
@Composable
fun Seekbar(){
    RecordingTrack(
        selectedMood = Mood.SAD,
        position = 10f,
        trackDuration = 400L,
        currentPosition = 100L,
        playbackState = PlaybackState.PLAYING,
        onPlayClicked = {},
        onPauseClicked = {},
        onResumeClicked = {}
    )
}

@Composable
fun Mood?.toColour() = when(this) {
    Mood.EXCITED -> colorResource(R.color.excited_mood)
    Mood.PEACEFUL -> colorResource(R.color.peaceful_mood)
    Mood.NEUTRAL -> colorResource(R.color.neutral_mood)
    Mood.SAD -> colorResource(R.color.sad_mood)
    Mood.STRESSED -> colorResource(R.color.stressed_mood)
    Mood.NONE -> colorResource(R.color.blue)
    null -> colorResource(R.color.blue)
}

@Composable
fun Mood?.toTrackInactiveColour() = when(this) {
    Mood.EXCITED -> colorResource(R.color.excited_mood_seekbar_inactive)
    Mood.PEACEFUL -> colorResource(R.color.peaceful_mood_seekbar_inactive)
    Mood.NEUTRAL -> colorResource(R.color.neutral_mood_seekbar_inactive)
    Mood.SAD -> colorResource(R.color.sad_mood_seekbar_inactive)
    Mood.STRESSED -> colorResource(R.color.stressed_mood_seekbar_inactive)
    Mood.NONE -> colorResource(R.color.seekbar_inactive)
    null -> colorResource(R.color.seekbar_inactive)
}

@Composable
fun Mood?.toRecordingTrackBackgroundColour() = when(this) {
    Mood.EXCITED -> colorResource(R.color.excited_mood_track_background)
    Mood.PEACEFUL -> colorResource(R.color.peaceful_mood_track_background)
    Mood.NEUTRAL -> colorResource(R.color.neutral_mood_track_background)
    Mood.SAD -> colorResource(R.color.sad_mood_track_background)
    Mood.STRESSED -> colorResource(R.color.stressed_mood_track_background)
    Mood.NONE -> colorResource(R.color.very_light_blue_gradient2)
    null -> colorResource(R.color.very_light_blue_gradient2)
}