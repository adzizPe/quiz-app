package com.alifmaulanarizqi.quizapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var videoBackground: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set layout
        setContentView(R.layout.activity_main)

        // Hilangkan Action Bar dan Status Bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        supportActionBar?.hide()

        // Inisialisasi elemen UI
        val btnStart: Button = findViewById(R.id.btnStart)
        val etName: EditText = findViewById(R.id.etName)
        val ivLogo: ImageView = findViewById(R.id.ivLogo)
        val tvTitle: TextView = findViewById(R.id.tvAppName)
        videoBackground = findViewById(R.id.videoBackground)

        // Inisialisasi Video Background dan Musik
        setUpVideoBackground()
        setUpBackgroundMusic()

        // Efek Fade-in untuk Logo dan Judul
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        ivLogo.startAnimation(fadeInAnimation)
        tvTitle.startAnimation(fadeInAnimation)

        // Tombol Mulai
        btnStart.setOnClickListener {
            val name = etName.text.toString().trim()
            hideKeyboard(etName)

            if (name.isEmpty()) {
                // Efek Goyang pada input jika nama kosong
                val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake)
                etName.startAnimation(shakeAnimation)
                Toast.makeText(this, "Enter your name first!", Toast.LENGTH_LONG).show()
            } else {
                animateButton(btnStart) {
                    val intent = Intent(this, QuestionActivity::class.java)
                    intent.putExtra("USER_NAME_KEY", name)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun setUpVideoBackground() {
        val videoUri = Uri.parse("android.resource://${packageName}/raw/planet_bg") // File video di res/raw
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
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.musichappy).apply {
                isLooping = true
                start()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error playing music: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk menutup keyboard
    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Fungsi untuk animasi tombol
    private fun animateButton(view: View, onEnd: () -> Unit) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = 200
            addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onEnd() // Eksekusi setelah animasi selesai
                }

                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            start()
        }
    }

    // Hentikan musik dan video saat aktivitas dihentikan sementara
    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause() // Musik dijeda
        videoBackground.pause() // Video dijeda
    }

    // Lanjutkan musik dan video saat aktivitas dilanjutkan
    override fun onResume() {
        super.onResume()
        mediaPlayer?.start() // Musik dilanjutkan
        videoBackground.start() // Video dilanjutkan
    }

    // Hentikan dan lepaskan MediaPlayer saat aktivitas dihancurkan
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
        videoBackground.stopPlayback()
    }
}
