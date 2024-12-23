package com.alifmaulanarizqi.quizapp

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import android.media.MediaPlayer

class QuestionActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var tvQuestion: TextView
    private lateinit var ivQuestion: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgress: TextView
    private lateinit var tvOptionOne: TextView
    private lateinit var tvOptionTwo: TextView
    private lateinit var tvOptionThree: TextView
    private lateinit var tvOptionFour: TextView
    private lateinit var cvImage: CardView
    private lateinit var btnSubmit: Button
    private lateinit var timerText: TextView // Untuk menampilkan waktu

    private var mCurrentPosition: Int = 1
    private var mSelectedOptionPosition: Int = 0
    private var canSelectOption = true
    private var mCorrectAnswers: Int = 0
    private lateinit var mUserName: String
    private lateinit var mQuestionList: ArrayList<Question>
    private var countDownTimer: CountDownTimer? = null // Timer
    private val questionDuration: Long = 25000 // Durasi tiap soal: 25 detik

    private var mediaPlayer: MediaPlayer? = null // Musik latar belakang (BGM)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hilangkan Status Bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )

        // Hilangkan Action Bar
        supportActionBar?.hide()

        setContentView(R.layout.activity_question)
        setContentView(R.layout.activity_question)

        // Inisialisasi Background Music (BGM)
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.musichappy).apply {
                isLooping = true
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mUserName = intent.getStringExtra(Constants.USER_NAME_KEY).toString()

        tvQuestion = findViewById(R.id.tvQuestion)
        ivQuestion = findViewById(R.id.ivQuestion)
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tvProgress)
        tvOptionOne = findViewById(R.id.tvOptionOne)
        tvOptionTwo = findViewById(R.id.tvOptionTwo)
        tvOptionThree = findViewById(R.id.tvOptionThree)
        tvOptionFour = findViewById(R.id.tvOptionFour)
        cvImage = findViewById(R.id.cvImage)
        btnSubmit = findViewById(R.id.btnSubmit)
        timerText = findViewById(R.id.timerText)

        tvOptionOne.setOnClickListener(this)
        tvOptionTwo.setOnClickListener(this)
        tvOptionThree.setOnClickListener(this)
        tvOptionFour.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)

        mQuestionList = Constants.getQuestions()

        setQuestion()
    }

    private fun setQuestion() {
        // Reset tampilan opsi
        defaultOptionView()
        btnSubmit.visibility = View.GONE
        canSelectOption = true

        // Ambil pertanyaan saat ini
        val questionPackage: Question = mQuestionList[mCurrentPosition - 1]
        tvQuestion.text = questionPackage.question

        if (questionPackage.image == 0) {
            cvImage.background.setTint(ContextCompat.getColor(this, R.color.primary_color))
        } else {
            ivQuestion.setImageResource(questionPackage.image)
        }

        progressBar.progress = mCurrentPosition
        tvProgress.text = "$mCurrentPosition/${progressBar.max}"
        tvOptionOne.text = questionPackage.optionOne
        tvOptionTwo.text = questionPackage.optionTwo
        tvOptionThree.text = questionPackage.optionThree
        tvOptionFour.text = questionPackage.optionFour

        if (mCurrentPosition == mQuestionList.size) {
            btnSubmit.text = "Finish"
        } else {
            btnSubmit.text = "Submit"
        }

        startTimer() // Mulai timer untuk soal baru
    }

    private fun startTimer() {
        countDownTimer?.cancel() // Reset timer sebelumnya
        countDownTimer = object : CountDownTimer(questionDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = "Time left: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                timerText.text = "Time's up!"
                goToNextQuestion()
            }
        }.start()
    }

    private fun goToNextQuestion() {
        mCurrentPosition++
        if (mCurrentPosition <= mQuestionList.size) {
            setQuestion() // Atur soal berikutnya
        } else {
            // Jika sudah soal terakhir, pindah ke ResultActivity
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(Constants.USER_NAME_KEY, mUserName)
            intent.putExtra(Constants.SCORE_KEY, mCorrectAnswers)
            intent.putExtra(Constants.TOTAL_QUESTION_KEY, mQuestionList.size)
            startActivity(intent)
            finish()
        }
    }

    private fun defaultOptionView() {
        val options = ArrayList<TextView>()
        options.add(tvOptionOne)
        options.add(tvOptionTwo)
        options.add(tvOptionThree)
        options.add(tvOptionFour)

        for (option in options) {
            option.setTextColor(ContextCompat.getColor(this, R.color.white3))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this, R.drawable.default_option_border_bg)
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOption: Int) {
        defaultOptionView()
        mSelectedOptionPosition = selectedOption
        tv.setTextColor(ContextCompat.getColor(this, R.color.light_color))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)
    }

    private fun answerView(answer: Int, drawableView: Int) {
        when (answer) {
            1 -> tvOptionOne.background = ContextCompat.getDrawable(this, drawableView)
            2 -> tvOptionTwo.background = ContextCompat.getDrawable(this, drawableView)
            3 -> tvOptionThree.background = ContextCompat.getDrawable(this, drawableView)
            4 -> tvOptionFour.background = ContextCompat.getDrawable(this, drawableView)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvOptionOne -> {
                if (canSelectOption) {
                    selectedOptionView(tvOptionOne, 1)
                    btnSubmit.visibility = View.VISIBLE
                }
            }
            R.id.tvOptionTwo -> {
                if (canSelectOption) {
                    selectedOptionView(tvOptionTwo, 2)
                    btnSubmit.visibility = View.VISIBLE
                }
            }
            R.id.tvOptionThree -> {
                if (canSelectOption) {
                    selectedOptionView(tvOptionThree, 3)
                    btnSubmit.visibility = View.VISIBLE
                }
            }
            R.id.tvOptionFour -> {
                if (canSelectOption) {
                    selectedOptionView(tvOptionFour, 4)
                    btnSubmit.visibility = View.VISIBLE
                }
            }
            R.id.btnSubmit -> {
                countDownTimer?.cancel() // Hentikan timer saat Submit ditekan

                if (mSelectedOptionPosition == 0) {
                    // Jika tidak ada opsi yang dipilih, langsung ke soal berikutnya
                    goToNextQuestion()
                } else {
                    val question = mQuestionList[mCurrentPosition - 1]

                    // Jika jawaban salah
                    if (question.answer != mSelectedOptionPosition) {
                        answerView(mSelectedOptionPosition, R.drawable.wrong_option_bg)
                    } else {
                        // Jika jawaban benar
                        mCorrectAnswers++
                        answerView(question.answer, R.drawable.correct_option_bg)
                    }

                    // Cegah pilihan ulang
                    canSelectOption = false

                    // Jika soal terakhir
                    if (mCurrentPosition == mQuestionList.size) {
                        btnSubmit.text = "Finish"
                    } else {
                        btnSubmit.text = "Next"
                    }

                    // Reset pilihan
                    mSelectedOptionPosition = 0
                }
            }
        }
    }
    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause() // Musik dijeda saat aktivitas dijeda
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.start() // Musik dilanjutkan saat aktivitas dilanjutkan
    }
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
