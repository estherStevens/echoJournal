package stevens.software.echojournal

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MediaPlayer(
    val context: Context,
) {
    private var player: MediaPlayer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val playingState = MutableStateFlow(PlaybackState.STOPPED)

    @SuppressLint("NewApi")
    fun playFile(uri: Uri?){
        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(context, uri ?: Uri.EMPTY)
        }

        player?.prepare()
        player?.start()

        if(player?.isPlaying == true) {
            coroutineScope.launch{
                playingState.emit(PlaybackState.PLAYING)
            }
        }
    }

    fun pauseRecording() {
        player?.pause()
        if(player?.isPlaying == false) {
            coroutineScope.launch{
                playingState.emit(PlaybackState.PAUSED)
            }
        }
    }

    fun resumeRecording() {
        player?.start()
        if(player?.isPlaying == true) {
            coroutineScope.launch{
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
