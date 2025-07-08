package plinko.test.task.plinko.data.model

import java.util.UUID

data class GameItem(
    val type: Int,
    val id: String = UUID.randomUUID().toString()
)