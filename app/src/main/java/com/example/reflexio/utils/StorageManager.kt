package com.example.reflexio.utils

import android.content.Context
import android.content.SharedPreferences

class StorageManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    fun getHighScore(): Int {
        return sharedPreferences.getInt(Constants.KEY_HIGH_SCORE, 0)
    }

    fun saveHighScore(score: Int) {
        val currentHighScore = getHighScore()
        if (score > currentHighScore) {
            sharedPreferences.edit().putInt(Constants.KEY_HIGH_SCORE, score).apply()
        }
    }
}