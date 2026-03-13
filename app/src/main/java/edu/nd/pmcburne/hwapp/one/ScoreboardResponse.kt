data class ScoreboardResponse(
    val updated_at: String,
    val games: List<GameWrapper>
)

data class GameWrapper(
    val game: GameDetail
)

data class GameDetail(
    val gameID: String,
    val away: TeamData,
    val home: TeamData,
    val gameState: String,
    val startTime: String,
    val currentPeriod: String,
    val contestClock: String,
    val finalMessage: String
)

data class TeamData(
    val score: String,
    val winner: Boolean,
    val names: TeamNames
)

data class TeamNames(
    val short: String,
    val char6: String
)