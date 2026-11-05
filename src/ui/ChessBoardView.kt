package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as UiColor
import androidx.compose.ui.unit.dp

@Composable
fun ChessBoardView() {
    Column {
        repeat(8) { row ->
            Row {
                repeat(8) { column ->
                    val squareColor = if ((row + column) % 2 == 0) {
                        UiColor(0xFFF0D9B5)
                    } else {
                        UiColor(0xFFB58863)
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(squareColor)
                    )
                }
            }
        }
    }
}
