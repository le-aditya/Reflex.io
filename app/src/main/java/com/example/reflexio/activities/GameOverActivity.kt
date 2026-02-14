package com.example.reflexio.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.reflexio.R
import com.example.reflexio.databinding.ActivityGameOverBinding
import com.example.reflexio.utils.Constants
import com.example.reflexio.utils.StorageManager

class GameOverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameOverBinding
    private lateinit var storageManager: StorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageManager = StorageManager(this)
        
        val score = intent.getIntExtra(Constants.EXTRA_SCORE, 0)
        val highScore = storageManager.getHighScore()

        setupUI(score, highScore)
        startAnimations()
    }

    private fun setupUI(score: Int, highScore: Int) {
        binding.tvFinalScore.text = getString(R.string.score_label, score)
        binding.tvHighScore.text = getString(R.string.high_score_label, highScore)

        binding.btnPlayAgain.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun startAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.tvGameOver.startAnimation(fadeIn)
        binding.tvFinalScore.startAnimation(fadeIn)
        binding.tvHighScore.startAnimation(fadeIn)
        binding.btnPlayAgain.startAnimation(fadeIn)
        binding.btnHome.startAnimation(fadeIn)
    }
}