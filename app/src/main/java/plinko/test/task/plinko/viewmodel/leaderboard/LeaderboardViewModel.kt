package plinko.test.task.plinko.viewmodel.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import plinko.test.task.plinko.data.model.Player
import plinko.test.task.plinko.data.repository.LeaderboardRepository

class LeaderboardViewModel(
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    private val _leaderboard = MutableLiveData<List<Player>>()
    val leaderboard: LiveData<List<Player>> = _leaderboard

    fun loadLeaderboard() {
        val players = leaderboardRepository.getLeaderboard()
        _leaderboard.value = players
    }
}