package com.example.clientspace

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*

class VoiceRecorder(private val context: Context) {
    private var audioRecord: AudioRecord? = null
    private var _isRecording = false
    private lateinit var audioData: ByteArray

    private val sampleRate = 44100 // Частота дискретизации (стандарт для WAV)
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    val isRecording
        get() = _isRecording



    fun startRecording() {

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, sampleRate,
            channelConfig, audioFormat, bufferSize
        )

        audioData = ByteArray(bufferSize)
        _isRecording = true
        audioRecord?.startRecording()

        Thread {
            val outputStream = ByteArrayOutputStream()
            while (_isRecording) {
                val read = audioRecord?.read(audioData, 0, bufferSize) ?: 0
                if (read > 0) {
                    outputStream.write(audioData, 0, read)
                }
            }
            Log.d("voice recorder", "finished record")
            saveAsWav(outputStream.toByteArray()) // 📌 Добавляем WAV-заголовок
        }.start()
    }

    fun stopRecording() {
        _isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    private fun saveAsWav(pcmData: ByteArray) {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) // 📌 Получаем путь
        if (dir == null) {
            Log.e("voice recorder", "Каталог Music не найден!")
            return
        }
        else {
            Log.d("voice recorder", "Каталог найден")
        }

        val wavFile = File(dir, "recorded_audio.wav")
        try {
            FileOutputStream(wavFile).use { fos ->
                fos.write(createWavHeader(pcmData.size))
                fos.write(pcmData)
            }
            Log.d("VoiceRecorder", "Файл записан: ${wavFile.absolutePath}")
        } catch (e: Exception) {
            Log.e("VoiceRecorder", "Ошибка записи файла: ${e.message}")
        }
    }


    fun getRecordedBytes(): ByteArray {
        val wavFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "recorded_audio.wav")
        return wavFile.readBytes()
    }

    private fun createWavHeader(dataSize: Int): ByteArray {
        val totalSize = dataSize + 36
        val byteRate = sampleRate * 2

        return byteArrayOf(
            'R'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(), 'F'.code.toByte(),
            (totalSize and 0xff).toByte(),
            ((totalSize shr 8) and 0xff).toByte(),
            ((totalSize shr 16) and 0xff).toByte(),
            ((totalSize shr 24) and 0xff).toByte(),
            'W'.code.toByte(), 'A'.code.toByte(), 'V'.code.toByte(), 'E'.code.toByte(),
            'f'.code.toByte(), 'm'.code.toByte(), 't'.code.toByte(), ' '.code.toByte(),
            16, 0, 0, 0, 1, 0, 1, 0,
            (sampleRate and 0xff).toByte(),
            ((sampleRate shr 8) and 0xff).toByte(),
            ((sampleRate shr 16) and 0xff).toByte(),
            ((sampleRate shr 24) and 0xff).toByte(),
            (byteRate and 0xff).toByte(),
            ((byteRate shr 8) and 0xff).toByte(),
            ((byteRate shr 16) and 0xff).toByte(),
            ((byteRate shr 24) and 0xff).toByte(),
            2, 0, 16, 0,
            'd'.code.toByte(), 'a'.code.toByte(), 't'.code.toByte(), 'a'.code.toByte(),
            (dataSize and 0xff).toByte(),
            ((dataSize shr 8) and 0xff).toByte(),
            ((dataSize shr 16) and 0xff).toByte(),
            ((dataSize shr 24) and 0xff).toByte()
        )
    }
}
