package com.example.reflexio.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.reflexio.R
import com.example.reflexio.databinding.ActivityHomeBinding
import com.example.reflexio.utils.StorageManager

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var storageManager: StorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageManager = StorageManager(this)
        
        setupUI()
        startAnimations()
    }

    private fun setupUI() {
        val highScore = storageManager.getHighScore()
        binding.tvHighScore.text = getString(R.string.high_score_label, highScore)

        binding.btnStart.setOnClickListener {
            // Launch Instructions first
            val intent = Intent(this, InstructionsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun startAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.tvTitle.startAnimation(fadeIn)
        binding.tvSubtitle.startAnimation(fadeIn)
        binding.btnStart.startAnimation(fadeIn)
        binding.tvHighScore.startAnimation(fadeIn)
    }

    override fun onResume() {
        super.onResume()
        val highScore = storageManager.getHighScore()
        binding.tvHighScore.text = getString(R.string.high_score_label, highScore)
    }
}