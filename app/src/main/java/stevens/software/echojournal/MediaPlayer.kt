package stevens.software.echojournal

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import stevens.software.echojournal.ui.journal_entries.PlayingTrack

class MediaPlayer(
    val context: Context,
) {
    private var player: MediaPlayer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val playingState = MutableStateFlow(PlaybackState.STOPPED)
    val playingTrack: MutableStateFlow<PlayingTrack?> = MutableStateFlow(null) //todo can i update to a flow so i dont have to set an initial value
    val position = MutableStateFlow(0f)

    val trackMetadata = MutableStateFlow(null)

//    fun getRecording(uri: Uri?){
//
//        val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
//        val projection = arrayOf(
//            MediaStore.Video.Media._ID,
//            MediaStore.Video.Media.DISPLAY_NAME,
//            MediaStore.Video.Media.DURATION,
//            MediaStore.Video.Media.SIZE
//        )
//        val selection = "${MediaStore.Video.Media.DISPLAY_NAME} >= ?"
//        val selectionArgs = arrayOf(
//            TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
//        )
//
//    }

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

        if(player?.isPlaying == true) {
            coroutineScope.launch{
                playingTrack.emit(PlayingTrack(file = fileName, playbackState = PlaybackState.PLAYING))
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
                    position.emit(progress)
                }

                delay(1000L)
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
