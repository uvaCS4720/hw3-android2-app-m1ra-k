package edu.nd.pmcburne.hwapp.one

import BasketballScoresViewModel
import GameEntity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.DateTimeFormatter

class BasketballScoresActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                val viewModel: BasketballScoresViewModel = viewModel(
                    factory = BasketballViewModelFactory(applicationContext)
                )

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BasketballScoresScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BasketballScoresScreen(viewModel: BasketballScoresViewModel, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val games by viewModel.games.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val isWomen by viewModel.isWomen.collectAsState()

    val displayFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val newDate = java.time.LocalDate.of(year, month + 1, dayOfMonth)
            viewModel.updateDate(newDate)
        },
        selectedDate.year,
        selectedDate.monthValue - 1,
        selectedDate.dayOfMonth
    )

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { datePickerDialog.show() }) {
                Text(text = selectedDate.format(displayFormatter))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Men")
                Switch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    checked = isWomen,
                    onCheckedChange = { viewModel.toggleGender() }
                )
                Text("Women")
            }

            IconButton(onClick = { viewModel.refresh() }) {
                Icon(Icons.Default.Refresh, contentDescription = null)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (games.isEmpty()) {
                Text("No games found for this date", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(games) { game ->
                        GameItem(game = game)
                    }

                }
            }
        }
    }
}

@Composable
fun GameItem(game: GameEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Home: ${game.homeTeamName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Away: ${game.awayTeamName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (game.gameState != "pre") {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = game.homeScore.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (game.winner == "home") FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = game.awayScore.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (game.winner == "away") FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val statusText = when (game.gameState) {
                "final" -> "FINAL"
                "live" -> "CURRENTLY PLAYING: ${game.currentPeriod} - ${game.timeRemaining}"
                else -> "UPCOMING: ${game.startTime}"
            }

            Text(
                text = statusText,
                color = if (game.gameState == "live") Color.Red else Color.Gray,
                style = MaterialTheme.typography.labelLarge
            )

            if (game.gameState == "final") {
                val winnerName = if (game.winner == "home") game.homeTeamName else game.awayTeamName
                Text(
                    text = "Winner: $winnerName",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF388E3C)
                )
            }
        }
    }
}