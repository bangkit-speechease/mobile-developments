package com.example.speechease.ui.interactive

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.speechease.MainActivity
import com.example.speechease.R
import com.example.speechease.databinding.ActivityInteractiveFourBinding
import java.io.IOException

class InteractiveFourActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInteractiveFourBinding
    private var mediaPlayer: MediaPlayer? = null
    private var isReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInteractiveFourBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        binding.btnPlay.setOnClickListener {
            if (!isReady) {
                mediaPlayer?.prepareAsync()
            } else {
                if (mediaPlayer?.isPlaying == true) {
                    // None
                } else {
                    mediaPlayer?.start()
                    binding.btnPlay.setIconResource(R.drawable.baseline_stop_24)
                }
            }
        }

        mediaPlayer?.setOnCompletionListener {
            binding.btnPlay.setIconResource(R.drawable.baseline_play_arrow_24)
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, InteractiveFiveActivity::class.java)
            startActivity(intent)
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init() {
        mediaPlayer = MediaPlayer()
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        mediaPlayer?.setAudioAttributes(attribute)

        val afd = applicationContext.resources.openRawResourceFd(R.raw.interactive_four)
        try {
            mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mediaPlayer?.setOnPreparedListener {
            isReady = true
            mediaPlayer?.start()
            binding.btnPlay.setIconResource(R.drawable.baseline_stop_24)
        }
        mediaPlayer?.setOnErrorListener { _, what, extra ->
            Log.e("MediaPlayerError", "Error code: $what, Extra: $extra")
            false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}