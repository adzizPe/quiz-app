package com.alifmaulanarizqi.quizapp

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class CreditActivity : AppCompatActivity() {

    private lateinit var videoBackground: VideoView
    private var bgmPlayer: MediaPlayer? = null // MediaPlayer untuk BGM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit)

        // Hilangkan Action Bar dan Status Bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        supportActionBar?.hide()

        // Inisialisasi Video Background
        videoBackground = findViewById(R.id.videoBackground)
        setUpVideoBackground()

        // Inisialisasi Background Music (BGM)
        setUpBackgroundMusic()
    }

    private fun setUpVideoBackground() {
        val videoUri = Uri.parse("android.resource://${packageName}/raw/background_video")
        videoBackground.setVideoURI(videoUri)

        // Video looping dan otomatis dimulai
        videoBackground.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }

        // Mulai ulang video jika selesai
        videoBackground.setOnCompletionListener {
            videoBackground.start()
        }
    }

    private fun setUpBackgroundMusic() {
        // Inisialisasi BGM
        bgmPlayer = MediaPlayer.create(this, R.raw.musichappy) // Ganti dengan nama file musik di res/raw
        bgmPlayer?.isLooping = true // Musik akan diputar berulang
        bgmPlayer?.start() // Mulai memutar musik
    }

    override fun onPause() {
        super.onPause()
        videoBackground.pause() // Pause video saat aplikasi dijeda
        bgmPlayer?.pause() // Pause musik saat aplikasi dijeda
    }

    override fun onResume() {
        super.onResume()
        videoBackground.start() // Lanjutkan video saat aplikasi dilanjutkan
        bgmPlayer?.start() // Lanjutkan musik saat aplikasi dilanjutkan
    }

    override fun onDestroy() {
        super.onDestroy()
        videoBackground.stopPlayback() // Hentikan pemutaran video
        bgmPlayer?.release() // Bersihkan MediaPlayer untuk BGM
        bgmPlayer = null
    }
}
