import java.time.LocalDate

fun Int.formatTwoDigits(): String = String.format("%02d", this)

class BasketballRepository(
    private val api: BasketballApiService,
    private val dao: GameDao
) {
    suspend fun getGamesFromDb(date: String, gender: String): List<GameEntity> {
        return dao.getGames(date, gender)
    }

    suspend fun fetchAndSaveScores(gender: String, date: LocalDate) {
        val response = api.getScores(
            gender,
            date.year.toString(),
            date.monthValue.formatTwoDigits(),
            date.dayOfMonth.formatTwoDigits()
        )

        val games = response.games.map { it.toEntity(date, gender) }
        dao.insertGames(games)
    }
}

private fun GameWrapper.toEntity(date: LocalDate, gender: String): GameEntity {
    val g = this.game
    return GameEntity(
        gameID = g.gameID,
        date = date.toString(),
        gender = gender,
        homeTeamName = g.home.names.short,
        awayTeamName = g.away.names.short,
        homeScore = g.home.score.toIntOrNull() ?: 0,
        awayScore = g.away.score.toIntOrNull() ?: 0,
        gameState = g.gameState,
        startTime = g.startTime,
        currentPeriod = g.currentPeriod,
        timeRemaining = g.contestClock,
        winner = when {
            g.home.winner -> "home"
            g.away.winner -> "away"
            else -> "none"
        }
    )
}

