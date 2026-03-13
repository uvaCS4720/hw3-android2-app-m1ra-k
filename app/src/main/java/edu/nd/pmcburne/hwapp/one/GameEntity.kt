import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey
    val gameID: String,
    val date: String,
    val gender: String,
    val homeTeamName: String,
    val awayTeamName: String,
    val homeScore: Int,
    val awayScore: Int,
    val gameState: String,
    val startTime: String,
    val currentPeriod: String,
    val timeRemaining: String,
    val winner: String
)