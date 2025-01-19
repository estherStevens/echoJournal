package stevens.software.echojournal

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaTimestamp
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import stevens.software.echojournal.ui.journal_entries.PlayingTrack
import kotlin.math.roundToInt

class MediaPlayer(
    val context: Context,
)  {
    private var player: MediaPlayer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val playingState = MutableStateFlow(PlaybackState.STOPPED)
    val playingTrack: MutableStateFlow<PlayingTrack?> = MutableStateFlow(null) //todo can i update to a flow so i dont have to set an initial value?


    @SuppressLint("NewApi")
    fun playFile(recordingUri: Uri?, fileName: String = ""){
        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(context, recordingUri ?: Uri.EMPTY)
        }

        player?.prepare()
        player?.start()

        player?.setOnMediaTimeDiscontinuityListener(object : MediaPlayer.OnMediaTimeDiscontinuityListener { //todo make this class implement Player
            override fun onMediaTimeDiscontinuity(
                mp: MediaPlayer,
                mts: MediaTimestamp
            ) {
                if(!(mp.isPlaying)) {
                    playingTrack.update { previousState ->
                        previousState?.copy(
                            playbackState = PlaybackState.PAUSED
                        )
                    }
                }
            }
        })

        if(player?.isPlaying == true) {
            coroutineScope.launch{
                playingTrack.emit(PlayingTrack(file = fileName, playbackState = PlaybackState.PLAYING, progressPosition = 0f, currentPosition = player?.currentPosition?.toLong() ?: 0L))
                playingState.emit(PlaybackState.PLAYING)
                seekbarUpdateObserver()
            }
        }
    }


    private suspend fun seekbarUpdateObserver() {
        withContext(Dispatchers.IO) {
            while (true) {
                if (player != null && player!!.isPlaying) {
                    val pos = player!!.currentPosition
                    val progress = (pos.toFloat() / player!!.duration) * 100f

                    playingTrack.update {
                        it?.copy(
                            progressPosition = progress,
                            currentPosition = pos.toLong()
                        )
                    }
                }

                delay(100L) //Because i am testing on seconds long tracks, having 1000L is to noticeable
            }
        }
    }

    fun pauseRecording() {
        player?.pause()
        if(player?.isPlaying == false) {
            coroutineScope.launch{
                playingTrack.update { previousState ->
                    previousState?.copy(
                        playbackState = PlaybackState.PAUSED
                    )
                }
                playingState.emit(PlaybackState.PAUSED)
            }
        }
    }

    fun resumeRecording() {
        player?.start()
        if(player?.isPlaying == true) {
            coroutineScope.launch{
                playingTrack.update { previousState ->
                    previousState?.copy(
                        playbackState = PlaybackState.PLAYING
                    )
                }
                playingState.emit(PlaybackState.PLAYING)
            }
        }
    }

    fun stopPlaying(){
        player?.stop()
        player?.release()

        player = null
    }

}

enum class PlaybackState{
    PLAYING,
    PAUSED,
    STOPPED
}
