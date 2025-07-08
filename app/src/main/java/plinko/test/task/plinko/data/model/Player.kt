package plinko.test.task.plinko.data.model

data class Player(
    val name: String,
    val score: Int,
    val time: Int,
    val isCurrentPlayer: Boolean = false
)