package com.alifmaulanarizqi.quizapp

import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var videoBackground: VideoView
    private var isBgmPlaying = true
    private var isSfxEnabled = true // SFX default aktif
    private var sfxPlayer: MediaPlayer? = null // MediaPlayer untuk SFX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hilangkan Status Bar dan Action Bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        supportActionBar?.hide()

        setContentView(R.layout.activity_main_menu)

        // Inisialisasi Video Background
        videoBackground = findViewById(R.id.videoBackground)
        setUpVideoBackground()

        // Inisialisasi Background Music
        setUpBackgroundMusic()

        // Inisialisasi Tombol
        val btnStart: Button = findViewById(R.id.btnStart)
        val btnSettings: Button = findViewById(R.id.btnSettings)
        val btnCredit: Button = findViewById(R.id.btnCredit)
        val btnExit: Button = findViewById(R.id.btnExit)

        // Efek Animasi pada Tombol
        applyButtonAnimation(btnStart)
        applyButtonAnimation(btnSettings)
        applyButtonAnimation(btnCredit)
        applyButtonAnimation(btnExit)

        // Tombol Mulai
        btnStart.setOnClickListener {
            playSFX() // Mainkan SFX
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Tombol Pengaturan
        btnSettings.setOnClickListener {
            playSFX() // Mainkan SFX
            showSettingsDialog()
        }

        // Tombol Credit
        btnCredit.setOnClickListener {
            playSFX() // Mainkan SFX
            val intent = Intent(this, CreditActivity::class.java)
            startActivity(intent)
        }

        // Tombol Exit
        btnExit.setOnClickListener {
            playSFX() // Mainkan SFX
            showExitDialog()
        }
    }

    private fun setUpVideoBackground() {
        val videoUri = Uri.parse("android.resource://${packageName}/raw/background_video") // File video di res/raw
        videoBackground.setVideoURI(videoUri)

        videoBackground.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = false // Matikan looping bawaan
            mediaPlayer.setOnSeekCompleteListener {
                videoBackground.start() // Mulai ulang tanpa jeda
            }

            // Gunakan listener untuk mendeteksi saat hampir selesai
            Thread {
                while (true) {
                    try {
                        if (mediaPlayer.isPlaying && mediaPlayer.currentPosition >= mediaPlayer.duration - 100) {
                            videoBackground.seekTo(0) // Kembali ke awal sebelum selesai
                        }
                        Thread.sleep(50) // Periksa setiap 50ms
                    } catch (e: Exception) {
                        break
                    }
                }
            }.start()
        }

        videoBackground.start()
    }

    private fun setUpBackgroundMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.musichappy).apply {
            isLooping = true // Musik akan diulang
            start() // Mulai memutar musik
        }
    }

    private fun applyButtonAnimation(button: Button) {
        val fadeInOut = AlphaAnimation(0.5f, 1.0f)
        fadeInOut.duration = 1000
        fadeInOut.repeatCount = AlphaAnimation.INFINITE
        fadeInOut.repeatMode = AlphaAnimation.REVERSE
        button.startAnimation(fadeInOut)
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pengaturan")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        // Kontrol Volume BGM
        val volumeSeekBar = SeekBar(this)
        volumeSeekBar.max = 100
        volumeSeekBar.progress = 50
        mediaPlayer?.setVolume(0.5f, 0.5f)

        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                mediaPlayer?.setVolume(volume, volume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        layout.addView(TextView(this).apply { text = "Kontrol Volume BGM"; textSize = 16f })
        layout.addView(volumeSeekBar)

        // Toggle SFX
        val sfxToggle = CheckBox(this).apply {
            text = "Aktifkan SFX"
            isChecked = isSfxEnabled
            setOnCheckedChangeListener { _, isChecked ->
                isSfxEnabled = isChecked
            }
        }

        layout.addView(sfxToggle)

        builder.setView(layout)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Keluar Aplikasi")
        builder.setMessage("Apakah Anda yakin ingin keluar?")
        builder.setPositiveButton("Ya") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            finishAffinity() // Menutup seluruh aplikasi
        }
        builder.setNegativeButton("Tidak") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun playSFX() {
        if (isSfxEnabled) { // Periksa apakah SFX diaktifkan
            try {
                sfxPlayer?.release() // Lepaskan SFX sebelumnya jika ada
                sfxPlayer = MediaPlayer.create(this, R.raw.bruh).apply {
                    start()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error playing SFX: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        videoBackground.pause() // Jeda video saat aplikasi dijeda
        mediaPlayer?.pause() // Jeda musik saat aplikasi dijeda
    }

    override fun onResume() {
        super.onResume()
        videoBackground.start() // Lanjutkan video saat aplikasi dilanjutkan
        mediaPlayer?.start() // Lanjutkan musik saat aplikasi dilanjutkan
    }

    override fun onDestroy() {
        super.onDestroy()
        videoBackground.stopPlayback() // Hentikan pemutaran video
        mediaPlayer?.release() // Bersihkan MediaPlayer
        mediaPlayer = null
        sfxPlayer?.release() // Bersihkan SFX Player
        sfxPlayer = null
    }
}
