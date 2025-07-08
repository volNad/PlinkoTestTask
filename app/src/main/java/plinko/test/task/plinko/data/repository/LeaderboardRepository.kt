package plinko.test.task.plinko.data.repository

import plinko.test.task.plinko.data.model.Player
import kotlin.random.Random

class LeaderboardRepository(
    private val gameRepository: GameRepository
) {

    private val plinkoPlayers = listOf(
        "Ball Master", "Plinko Pro", "Bounce King", "Drop Champion",
        "Gravity Guru", "Ping Pong Pete", "Bouncy Bob", "Sphere Sage",
        "Orb Oracle", "Ball Wizard", "Plinko Pilot", "Drop Doctor",
        "Bounce Baron", "Gravity Ghost", "Ping Master", "Ball Bender",
        "Plinko Phantom", "Drop Dynamo", "Bounce Buddy", "Sphere Spirit",
        "Orb Overlord", "Ball Bandit", "Plinko Prince", "Drop Daredevil"
    )

    private val timeOptions = listOf(30, 60, 90, 120)

    fun getLeaderboard(): List<Player> {
        val players = mutableListOf<Player>()

        repeat(24) { index ->
            players.add(
                Player(
                    name = plinkoPlayers[index],
                    score = Random.nextInt(1000, 10000),
                    time = timeOptions.random()
                )
            )
        }

        players.add(
            Player(
                name = "You",
                score = gameRepository.getHighScore(),
                time = gameRepository.getGameTime(),
                isCurrentPlayer = true
            )
        )

        return players.sortedByDescending { it.score }
    }
}