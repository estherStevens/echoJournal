package stevens.software.echojournal

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalDateTime

class VoiceRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null


    var filePath = ""

    var recordingUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.S)
    fun startRecording(){
        val time = LocalDateTime.now()
        val fileName = "Entry_$time.m4a"

        val values = ContentValues()
        values.put(MediaStore.Audio.Media.TITLE, fileName)
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
        values.put(MediaStore.Audio.Media.IS_PENDING, true)

        recordingUri = context.contentResolver.insert(MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values)

        context.contentResolver.openFileDescriptor(recordingUri!!, "w")?.let { fileDescriptor ->
            MediaRecorder(context).apply {
                recorder = this
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(64000)
                setAudioChannels(2)
                setMaxDuration(-1)
                setOutputFile(fileDescriptor.fileDescriptor)
                prepare()
                start()
            }
        }
    }

    fun stopRecording(){
        recorder?.stop()
        recorder?.release()
        recorder = null

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Files.FileColumns.IS_PENDING, false)
        context.contentResolver.update(recordingUri!!, contentValues, null, null)
    }


}