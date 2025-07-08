package plinko.test.task.plinko.data.model

data class MatchResult(
    val hasMatches: Boolean,
    val matchedPositions: List<Int>
)