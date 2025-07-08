package plinko.test.task.plinko.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit

class GameRepository(
    private val sharedPreferences: SharedPreferences
) {

    fun saveHighScore(score: Int) {
        sharedPreferences.edit {
            putInt("high_score", score)
        }
    }

    fun getHighScore(): Int {
        return sharedPreferences.getInt("high_score", 0)
    }

    fun saveGameTime(time: Int) {
        sharedPreferences.edit {
            putInt("game_time", time)
        }
    }

    fun getGameTime(): Int {
        return sharedPreferences.getInt("game_time", 60)
    }

    fun saveScoreMultiplier(multiplier: Int) {
        sharedPreferences.edit {
            putInt("score_multiplier", multiplier)
        }
    }

    fun getScoreMultiplier(): Int {
        return sharedPreferences.getInt("score_multiplier", 1)
    }
}
