package com.example.clientspace

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import java.io.File
import java.io.FileOutputStream
import com.example.clientspace.ui.File as LocalFile

// class responsible for playing audio
class AudioPlayerView @JvmOverloads constructor(
    context: Context,
    attrs : AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private val btnPlayStop : ImageView
        get() = findViewById(R.id.imagePlayStop)

    private val audioSeekBar : SeekBar
        get() = findViewById(R.id.audioSeekBar)

    private val tvAudioTime : TextView
        get() = findViewById(R.id.tvAudioTime)

    private val imageCloseAudio : ImageView
        get() = findViewById(R.id.imageCloseAudio)

    private var mediaPlayer : MediaPlayer? = null
    private var handler = Handler(Looper.getMainLooper())
    private var isSetAudio = false
    private var audioFile : LocalFile? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.audio_player_view, this, true)

        btnPlayStop.setOnClickListener{
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()

                    btnPlayStop.setImageResource(R.drawable.ic_play)
                } else {
                    btnPlayStop.setImageResource(R.drawable.ic_pause)
                    startAudio()
                }
            }
        }

        audioSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer?.pause()
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                startAudio()
            }

        })

        imageCloseAudio.setOnClickListener{
            destroy()
        }
    }


    fun setAudioFile(file : LocalFile) {
        if (!file.isAudio) {
            //Log.e("not audio file", file.type)
            return
        }

        if (isSetAudio) {
            destroy()
        }

        // default this view is gone
        visibility = View.VISIBLE

        val tempFile = File.createTempFile("temp", "." + file.type.substring(6), context.cacheDir)
        tempFile.deleteOnExit()

        FileOutputStream(tempFile).use { it.write(file.bytes) }

        val uri : Uri = Uri.fromFile(tempFile)

        if (tempFile.exists()) {

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, uri)
                prepare()


                audioSeekBar.max = duration

                setOnCompletionListener {
                    destroy()
                }
            }

            startAudio()

            isSetAudio = true
            audioFile = file
        }
    }


    // destroy the view and stop the audio
    fun destroy() {
        visibility = View.INVISIBLE
        // isSetAudio = false

        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer?.stop()

            mediaPlayer?.seekTo(0)
            audioSeekBar.progress = 0

        }

        if (isSetAudio) {
            mediaPlayer?.reset()

        }

        mediaPlayer?.release()

        isSetAudio = false
        mediaPlayer = null

        audioFile = null
    }

    private fun startAudio() {
        if (mediaPlayer == null) {
            return
        }

        mediaPlayer!!.start()
        btnPlayStop.setImageResource(R.drawable.ic_pause)
        handler.postDelayed(updateSeekBar, 1000)
    }

    private val updateSeekBar = object : Runnable {
        override fun run() {
            if (mediaPlayer != null) {
                audioSeekBar.progress = mediaPlayer!!.currentPosition
                tvAudioTime.text = formatTime(mediaPlayer!!.currentPosition)
                handler.postDelayed(this, 1000)
            }
            else {
                audioSeekBar.progress = 0
                tvAudioTime.text = "00:00"
            }
        }
    }

    private fun formatTime(millis : Int) : String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60

        return String.format("%02d:%02d", minutes, seconds)
    }
}