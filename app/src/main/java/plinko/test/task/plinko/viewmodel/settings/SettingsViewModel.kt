package plinko.test.task.plinko.viewmodel.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import plinko.test.task.plinko.data.repository.GameRepository

class SettingsViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _gameTime = MutableLiveData<Int>()
    val gameTime: LiveData<Int> = _gameTime

    private val _scoreMultiplier = MutableLiveData<Int>()
    val scoreMultiplier: LiveData<Int> = _scoreMultiplier

    init {
        _gameTime.value = gameRepository.getGameTime()
        _scoreMultiplier.value = gameRepository.getScoreMultiplier()
    }

    fun setGameTime(time: Int) {
        gameRepository.saveGameTime(time)
        _gameTime.value = time
    }

    fun setScoreMultiplier(multiplier: Int) {
        gameRepository.saveScoreMultiplier(multiplier)
        _scoreMultiplier.value = multiplier
    }
}