package com.example.reflexio.activities

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.reflexio.R
import com.example.reflexio.databinding.ActivityGameBinding
import com.example.reflexio.utils.Constants
import com.example.reflexio.utils.StorageManager
import java.util.Locale
import kotlin.random.Random

class GameActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityGameBinding
    private lateinit var storageManager: StorageManager
    
    private var score = 0
    private var highScore = 0
    private var currentColorResId: Int = 0
    private var roundTime = 3000L 
    private var countDownTimer: CountDownTimer? = null
    
    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageManager = StorageManager(this)
        highScore = storageManager.getHighScore()
        
        // Initialize TTS early to eliminate delay
        tts = TextToSpeech(this, this)
        
        setupUI()
        setupBackgroundMusic()
        startGame()
    }

    private fun setupUI() {
        binding.tvHighScore.text = getString(R.string.high_score_label, highScore)
        binding.tvScore.text = getString(R.string.score_label, score)
        
        binding.colorGrid.removeAllViews()
        Constants.GAME_COLORS.forEach { colorRes ->
            val button = ImageButton(this).apply {
                val size = 260
                layoutParams = GridLayout.LayoutParams().apply {
                    width = size
                    height = size
                    setMargins(20, 20, 20, 20)
                }
                setBackgroundResource(R.drawable.bg_circle_button)
                backgroundTintList = ContextCompat.getColorStateList(context, colorRes)
                elevation = 15f
                setOnClickListener { onColorClicked(it, colorRes) }
            }
            binding.colorGrid.addView(button)
        }
    }

    private fun setupBackgroundMusic() {
        try {
            val resId = resources.getIdentifier("bg_music", "raw", packageName)
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(this, resId)
                mediaPlayer?.isLooping = true
                mediaPlayer?.setVolume(0.3f, 0.3f)
                mediaPlayer?.start()
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun startGame() {
        score = 0
        roundTime = 3000L
        generateNewRound()
    }

    private fun generateNewRound() {
        countDownTimer?.cancel()
        
        currentColorResId = Constants.GAME_COLORS[Random.nextInt(Constants.GAME_COLORS.size)]
        
        var distractorColorResId: Int
        do {
            distractorColorResId = Constants.GAME_COLORS[Random.nextInt(Constants.GAME_COLORS.size)]
        } while (distractorColorResId == currentColorResId)
        
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val randomLetter = letters[Random.nextInt(letters.length)].toString()
        
        binding.tvColorLetter.text = randomLetter
        binding.tvColorLetter.setTextColor(ContextCompat.getColor(this, currentColorResId))
        
        binding.colorArea.setBackgroundColor(ContextCompat.getColor(this, distractorColorResId))
        binding.colorArea.alpha = 0.8f 

        binding.tvScore.text = getString(R.string.score_label, score)
        
        binding.cardColor.alpha = 0f
        binding.cardColor.animate().alpha(1f).setDuration(150).start()
        
        startTimer()
    }

    private fun startTimer() {
        binding.progressBar.max = 100
        countDownTimer = object : CountDownTimer(roundTime, 20) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = (millisUntilFinished.toFloat() / roundTime * 100).toInt()
                binding.progressBar.progress = progress
            }
            override fun onFinish() { endGame() }
        }.start()
    }

    private fun onColorClicked(view: View, colorResId: Int) {
        if (colorResId == currentColorResId) {
            score++
            
            val glowAnim = ObjectAnimator.ofObject(
                binding.cardColor, "cardBackgroundColor", ArgbEvaluator(), 
                Color.parseColor("#1E1E1E"), Color.WHITE, Color.parseColor("#1E1E1E")
            )
            glowAnim.duration = 250
            glowAnim.start()

            view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }.start()

            adjustDifficulty()
            generateNewRound()
        } else {
            endGame()
        }
    }

    private fun adjustDifficulty() {
        if (score % 4 == 0) {
            if (roundTime > 800L) {
                roundTime -= 100L
            }
        }
    }

    private fun endGame() {
        countDownTimer?.cancel()
        mediaPlayer?.stop()
        
        // Speak IMMEDIATELY on game over with modified tone
        if (isTtsReady) {
            tts?.setPitch(0.8f)      // Lower pitch for a "deeper" more serious game-over tone
            tts?.setSpeechRate(0.9f) // Slightly slower for dramatic effect
            tts?.speak("Game Over! Final Score: $score", TextToSpeech.QUEUE_FLUSH, null, "GameOverID")
        }
        
        storageManager.saveHighScore(score)
        val intent = Intent(this, GameOverActivity::class.java).apply {
            putExtra(Constants.EXTRA_SCORE, score)
        }
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isTtsReady = true
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (mediaPlayer?.isPlaying == false) mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        mediaPlayer?.release()
        tts?.stop()
        tts?.shutdown()
    }
}
