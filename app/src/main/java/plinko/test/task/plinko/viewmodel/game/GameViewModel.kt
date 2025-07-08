package plinko.test.task.plinko.viewmodel.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import plinko.test.task.plinko.data.model.GameItem
import plinko.test.task.plinko.data.repository.GameRepository
import plinko.test.task.plinko.data.model.MatchResult
import java.util.UUID
import kotlin.random.Random

class GameViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _gameGrid = MutableLiveData<List<GameItem>>()
    val gameGrid: LiveData<List<GameItem>> = _gameGrid

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> = _timeLeft

    private val _selectedPosition = MutableLiveData<Int?>()
    val selectedPosition: LiveData<Int?> = _selectedPosition

    private val _matchedItems = MutableLiveData<List<Int>>()
    val matchedItems: LiveData<List<Int>> = _matchedItems

    private val _newItems = MutableLiveData<List<Pair<Int, GameItem>>>()
    val newItems: LiveData<List<Pair<Int, GameItem>>> = _newItems

    private var currentGrid = mutableListOf<GameItem>()
    private var selectedPos: Int? = null
    private var currentScore = 0
    private var scoreMultiplier = 1
    private var comboCounter = 0

    fun initializeGame() {
        currentScore = 0
        comboCounter = 0
        _score.value = currentScore
        scoreMultiplier = gameRepository.getScoreMultiplier()
        _timeLeft.value = gameRepository.getGameTime().toLong()
        generateNewGrid()
    }

    private fun generateNewGrid() {
        currentGrid.clear()
        repeat(64) {
            val randomType = Random.nextInt(1, 7)
            currentGrid.add(GameItem(randomType, generateUniqueId()))
        }
        _gameGrid.value = currentGrid.toList()
    }

    private fun generateUniqueId(): String {
        return UUID.randomUUID().toString()
    }

    fun onItemClicked(position: Int) {
        if (selectedPos == null) {
            selectedPos = position
            _selectedPosition.value = position
        } else if (selectedPos == position) {
            selectedPos = null
            _selectedPosition.value = null
        } else {
            val currentlySelected = selectedPos!!
            selectedPos = null
            _selectedPosition.value = null

            viewModelScope.launch {
                swapItems(currentlySelected, position)
                delay(300)
                val matchResult = checkForMatches()

                if (matchResult.hasMatches) {
                    processMatches(matchResult)
                } else {
                    delay(200)
                    swapItems(currentlySelected, position)
                    comboCounter = 0
                }
            }
        }
    }

    private fun swapItems(pos1: Int, pos2: Int) {
        if (pos1 !in currentGrid.indices || pos2 !in currentGrid.indices) {
            return
        }
        val temp = currentGrid[pos1]
        currentGrid[pos1] = currentGrid[pos2]
        currentGrid[pos2] = temp
        _gameGrid.value = currentGrid.toList()
    }

    private fun checkForMatches(): MatchResult {
        val matchedPositions = findMatches()
        return MatchResult(matchedPositions.isNotEmpty(), matchedPositions)
    }

    private suspend fun processMatches(matchResult: MatchResult) {
        val matchedPositions = matchResult.matchedPositions
        _matchedItems.value = matchedPositions

        delay(300)

        comboCounter++
        val currentComboMultiplier = minOf(comboCounter, 5)

        val baseScore = matchedPositions.size * 10
        val finalScore = baseScore * scoreMultiplier * currentComboMultiplier
        currentScore += finalScore
        _score.value = currentScore


        removeAndRefill(matchedPositions)

        delay(400)
        val cascadeResult = checkForMatches()
        if (cascadeResult.hasMatches) {
            processMatches(cascadeResult)
        } else {
            comboCounter = 0
        }
    }

    private fun findMatches(): List<Int> {
        val matchedPositions = mutableListOf<Int>()

        for (row in 0 until 8) {
            var matchCount = 1
            var currentType = currentGrid[row * 8].type

            for (col in 1 until 8) {
                val pos = row * 8 + col
                if (currentGrid[pos].type == currentType) {
                    matchCount++
                } else {
                    if (matchCount >= 3) {
                        for (i in (col - matchCount) until col) {
                            matchedPositions.add(row * 8 + i)
                        }
                    }
                    matchCount = 1
                    currentType = currentGrid[pos].type
                }
            }
            if (matchCount >= 3) {
                for (i in (8 - matchCount) until 8) {
                    matchedPositions.add(row * 8 + i)
                }
            }
        }

        for (col in 0 until 8) {
            var matchCount = 1
            var currentType = currentGrid[col].type

            for (row in 1 until 8) {
                val pos = row * 8 + col
                if (currentGrid[pos].type == currentType) {
                    matchCount++
                } else {
                    if (matchCount >= 3) {
                        for (i in (row - matchCount) until row) {
                            matchedPositions.add(i * 8 + col)
                        }
                    }
                    matchCount = 1
                    currentType = currentGrid[pos].type
                }
            }
            if (matchCount >= 3) {
                for (i in (8 - matchCount) until 8) {
                    matchedPositions.add(i * 8 + col)
                }
            }
        }

        return matchedPositions.distinct()
    }

    private fun removeAndRefill(positions: List<Int>) {
        val newItemsList = mutableListOf<Pair<Int, GameItem>>()
        positions.forEach { pos ->
            val newItem = GameItem(Random.nextInt(1, 7), generateUniqueId())
            currentGrid[pos] = newItem
            newItemsList.add(pos to newItem)
        }
        _newItems.value = newItemsList
        _gameGrid.value = currentGrid.toList()
    }

    fun saveFinalScore() {
        if (currentScore > gameRepository.getHighScore()) {
            gameRepository.saveHighScore(currentScore)
        }
    }

    fun getHighScore(): Int {
        return gameRepository.getHighScore()
    }

    fun resetGame() {
        initializeGame()
    }
}