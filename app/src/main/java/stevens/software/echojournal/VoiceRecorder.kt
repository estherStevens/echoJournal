package stevens.software.echojournal

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream

class VoiceRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null

    @RequiresApi(Build.VERSION_CODES.S)
    fun startRecording(file: File){
        recorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
    }

    fun stopRecording(){
        recorder?.release()
        recorder = null
    }
}