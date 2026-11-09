package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as UiColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import model.GameState
import model.Piece
import model.Position

@Composable
fun ChessBoardView(state: GameState) {
    Column {
        repeat(8) { row ->
            Row {
                repeat(8) { column ->
                    val position = Position(row, column)
                    val piece = state.board.pieceAt(position)
                    val squareColor = if ((row + column) % 2 == 0) {
                        UiColor(0xFFF0D9B5)
                    } else {
                        UiColor(0xFFB58863)
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(squareColor)
                            .semantics {
                                contentDescription = piece?.let(::pieceDescription)
                                    ?: "Empty square at row ${row + 1}, column ${column + 1}"
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
    }
}

internal fun pieceDescription(piece: Piece): String =
    "${piece.color.name.lowercase().replaceFirstChar { it.uppercase() }} " +
        piece.type.name.lowercase().replaceFirstChar { it.uppercase() }
