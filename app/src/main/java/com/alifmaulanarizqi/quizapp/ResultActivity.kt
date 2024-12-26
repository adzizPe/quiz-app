package com.alifmaulanarizqi.quizapp

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.view.View
import android.widget.TextView

class ResultActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null // Tambahkan MediaPlayer untuk BGM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Hilangkan Status Bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )

        // Hilangkan Action Bar
        supportActionBar?.hide()

        setContentView(R.layout.activity_result)


        // Inisialisasi Background Music (BGM)
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.kemenangan).apply {
                isLooping = false // Musik diputar berulang
                start() // Mulai memutar musik
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val tvName: TextView = findViewById(R.id.tvName)
        val tvScore: TextView = findViewById(R.id.tvScore)
        val btnFinish: Button = findViewById(R.id.btnFinish)

        val username = intent.getStringExtra(Constants.USER_NAME_KEY)
        tvName.text = username

        val totalQuestions = intent.getIntExtra(Constants.TOTAL_QUESTION_KEY, 0)
        val totalScores = intent.getIntExtra(Constants.SCORE_KEY, 0)

        tvScore.text = "Your score is $totalScores out of $totalQuestions"


        btnFinish.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Tutup aktivitas saat kembali ke MainActivity
        }


    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause() // Jeda musik saat aktivitas dijeda
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.start() // Lanjutkan musik saat aktivitas dilanjutkan
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Lepaskan MediaPlayer untuk menghindari kebocoran memori
        mediaPlayer = null
    }
}
