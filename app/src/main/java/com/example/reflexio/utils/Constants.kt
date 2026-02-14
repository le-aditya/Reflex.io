package com.example.reflexio.utils

import com.example.reflexio.R

object Constants {
    val GAME_COLORS = listOf(
        R.color.game_red,
        R.color.game_blue,
        R.color.game_green,
        R.color.game_yellow,
        R.color.game_purple,
        R.color.game_orange,
        R.color.game_cyan,
        R.color.game_pink
    )
    
    const val PREFS_NAME = "quick_reflex_prefs"
    const val KEY_HIGH_SCORE = "high_score"
    const val EXTRA_SCORE = "extra_score"
    
    // Faster gameplay
    const val INITIAL_TIME = 2000L 
    const val MIN_TIME = 600L
    const val TIME_DECREMENT = 150L
    const val DIFFICULTY_INTERVAL = 3 // Increase speed faster
}