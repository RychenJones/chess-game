import ui.ChessBoardView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import game.GameController

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Chess Game",
        state = rememberWindowState(width = 580.dp, height = 720.dp)
    ) {
        MaterialTheme {
            val gameController = remember { GameController() }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Chess Game", style = MaterialTheme.typography.headlineMedium)
                ChessBoardView(gameController)
            }
        }
    }
}
