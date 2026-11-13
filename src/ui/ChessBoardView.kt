package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.TextButton
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
import model.GameStatus
import model.Piece
import model.PieceType
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

        if (currentSelection == position) {
            controller.deselect()
            selectedPosition.value = null
            feedbackMessage.value = "Selection cleared"
            return
        }

        when (val result = controller.tryMove(position)) {
            is MoveResult.Success -> {
                selectedPosition.value = null
                val captureMessage = result.captured?.let {
                    " — captured ${pieceDescription(it)}"
                }.orEmpty()
                val currentPlayerName = state.currentPlayer.name.lowercase().replaceFirstChar { it.uppercase() }
                feedbackMessage.value = when (state.gameStatus) {
                    GameStatus.CHECKMATE -> checkmateMessage(state.winner)
                    GameStatus.STALEMATE -> stalemateMessage()
                    GameStatus.INSUFFICIENT_MATERIAL -> insufficientMaterialMessage()
                    GameStatus.FIFTY_MOVE_DRAW -> fiftyMoveMessage()
                    GameStatus.ACTIVE -> {
                        val checkMessage = if (state.playerInCheck == state.currentPlayer) {
                            " ${currentPlayerName} is in check."
                        } else {
                            ""
                        }
                        "Move complete$captureMessage. " +
                            "$currentPlayerName's turn.$checkMessage"
                    }
                }
            }

            is MoveResult.Failure -> {
                feedbackMessage.value = result.reason
                if (controller.select(position)) {
                    selectedPosition.value = position
                } else {
                    selectedPosition.value = null
                }
            }
        }
    }

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Box(
            modifier = Modifier
                .width(560.dp)
                .padding(end = 24.dp)
                .height(40.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row {
                TextButton(
                    onClick = {
                        if (controller.undo()) {
                            selectedPosition.value = null
                            val playerName = state.currentPlayer.name.lowercase().replaceFirstChar { it.uppercase() }
                            feedbackMessage.value = "Move undone — $playerName's turn"
                        }
                    },
                    enabled = controller.canUndo
                ) {
                    Text("Undo")
                }
                TextButton(
                    onClick = {
                        controller.reset()
                        selectedPosition.value = null
                        feedbackMessage.value = "Game reset — White's turn"
                    }
                ) {
                    Text("Reset")
                }
            }
        }

        Box(
            modifier = Modifier
                .width(560.dp)
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when (state.gameStatus) {
                        GameStatus.CHECKMATE -> checkmateMessage(state.winner)
                        GameStatus.STALEMATE -> stalemateMessage()
                        GameStatus.INSUFFICIENT_MATERIAL -> insufficientMaterialMessage()
                        GameStatus.FIFTY_MOVE_DRAW -> fiftyMoveMessage()
                        GameStatus.ACTIVE -> {
                            "Current player: " +
                                state.currentPlayer.name.lowercase().replaceFirstChar { it.uppercase() } +
                                if (state.playerInCheck == state.currentPlayer) " — in check" else ""
                        }
                    },
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

        Box(modifier = Modifier.width(560.dp)) {
            val legalDestinations = controller.legalDestinationsForSelected()

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
                                val isLegalDestination = position in legalDestinations
                                val isCheckedKing = piece?.type == PieceType.KING &&
                                    state.playerInCheck == piece.color
                                val squareColor = if (isCheckedKing) {
                                    UiColor(0xFFE57373)
                                } else if (isSelected) {
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
                                        .clickable(
                                            enabled = state.gameStatus == GameStatus.ACTIVE,
                                            interactionSource = null
                                        ) {
                                            handleSquareClick(position)
                                        }
                                        .semantics {
                                            contentDescription = piece?.let {
                                                val checkDescription = if (isCheckedKing) ", in check" else ""
                                                val legalMoveDescription =
                                                    if (isLegalDestination) ", legal move destination" else ""
                                                "${pieceDescription(it)} on ${position.toAlgebraic()}" +
                                                    "$checkDescription$legalMoveDescription"
                                            } ?: "Empty square ${position.toAlgebraic()}" +
                                                if (isLegalDestination) ", legal move destination" else ""
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    piece?.let { currentPiece ->
                                        ChessPieceView(currentPiece)
                                    }
                                    if (isLegalDestination) {
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .background(
                                                    color = UiColor(0x995D4037),
                                                    shape = CircleShape
                                                )
                                        )
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

internal fun checkmateMessage(winner: model.Color?): String {
    val winnerName = winner?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Unknown"
    return "Checkmate — $winnerName wins"
}

internal fun stalemateMessage(): String = "Stalemate — draw"

internal fun insufficientMaterialMessage(): String = "Insufficient material — draw"

internal fun fiftyMoveMessage(): String = "Fifty-move rule — draw"
