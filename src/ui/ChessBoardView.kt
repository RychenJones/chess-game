package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as UiColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import game.GameController
import game.MoveResult
import model.Piece
import model.Position

@Composable
fun ChessBoardView(
    controller: GameController
) {
    val selectedPosition = remember { mutableStateOf<Position?>(null) }
    val feedbackMessage = remember { mutableStateOf("Select a piece to begin") }
    val state = controller.state

    fun handleSquareClick(position: Position) {
        val currentSelection = selectedPosition.value

        if (currentSelection == null) {
            if (controller.select(position)) {
                selectedPosition.value = position
                feedbackMessage.value = "Selected ${pieceDescription(requireNotNull(state.board.pieceAt(position)))}"
            } else {
                feedbackMessage.value = "Select a ${state.currentPlayer.name.lowercase().replaceFirstChar { it.uppercase() }} piece"
            }
            return
        }

        when (val result = controller.tryMove(position)) {
            is MoveResult.Success -> {
                selectedPosition.value = null
                val captureMessage = result.captured?.let {
                    " — captured ${pieceDescription(it)}"
                }.orEmpty()
                feedbackMessage.value =
                    "Move complete$captureMessage. " +
                        "${state.currentPlayer.name.lowercase().replaceFirstChar { it.uppercase() }}'s turn"
            }

            is MoveResult.Failure -> {
                feedbackMessage.value = result.reason
                if (controller.select(position)) {
                    selectedPosition.value = position
                }
            }
        }
    }

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Box(
            modifier = Modifier
                .width(536.dp)
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Current player: ${state.currentPlayer.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = feedbackMessage.value,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }

        Row {
            Column {
                repeat(8) { row ->
                    CoordinateLabel(
                        label = (8 - row).toString(),
                        modifier = Modifier
                            .width(24.dp)
                            .height(64.dp)
                    )
                }
            }

            Column {
                repeat(8) { row ->
                    Row {
                        repeat(8) { column ->
                            val position = Position(row, column)
                            val piece = state.board.pieceAt(position)
                            val isSelected = selectedPosition.value == position
                            val squareColor = if (isSelected) {
                                UiColor(0xFFF6E27F)
                            } else if ((row + column) % 2 == 0) {
                                UiColor(0xFFF0D9B5)
                            } else {
                                UiColor(0xFFB58863)
                            }

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(squareColor)
                                    .clickable {
                                        handleSquareClick(position)
                                    }
                                    .semantics {
                                        contentDescription = piece?.let {
                                            "${pieceDescription(it)} on ${position.toAlgebraic()}"
                                        } ?: "Empty square ${position.toAlgebraic()}"
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                piece?.let { currentPiece ->
                                    ChessPieceView(currentPiece)
                                }
                            }
                        }
                    }
                }

                Row {
                    ('a'..'h').forEach { file ->
                        CoordinateLabel(
                            label = file.toString(),
                            modifier = Modifier
                                .width(64.dp)
                                .height(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CoordinateLabel(label: String, modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = UiColor(0xFF5D4037)
        )
    }
}

internal fun pieceDescription(piece: Piece): String =
    "${piece.color.name.lowercase().replaceFirstChar { it.uppercase() }} " +
        piece.type.name.lowercase().replaceFirstChar { it.uppercase() }
