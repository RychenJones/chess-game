package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class GameState {
    val board = Board()

    var currentPlayer: Color by mutableStateOf(Color.WHITE)
        private set

    var selectedPosition: Position? by mutableStateOf(null)
        private set

    init {
        reset()
    }

    fun reset() {
        board.clear()
        setupPieces()
        currentPlayer = Color.WHITE
        selectedPosition = null
    }

    fun select(position: Position): Boolean {
        val piece = board.pieceAt(position)
        val canSelect = piece?.color == currentPlayer
        selectedPosition = if (canSelect) position else null
        return canSelect
    }

    /**
     * Applies a move that has already passed movement-rule validation.
     * Returns the captured piece, if any.
     */
    fun applyMove(move: Move): Piece? {
        require(move.from == selectedPosition) {
            "The move must begin at the selected position"
        }

        val movingPiece = requireNotNull(board.pieceAt(move.from)) {
            "No piece occupies position ${move.from.toAlgebraic()}"
        }
        require(movingPiece.color == currentPlayer) {
            "It is ${currentPlayer.name.lowercase()}\'s turn"
        }

        val destinationPiece = board.pieceAt(move.to)
        require(destinationPiece?.color != currentPlayer) {
            "A piece cannot move onto a friendly piece"
        }

        val capturedPiece = board.move(move.from, move.to)
        currentPlayer = currentPlayer.opposite()
        selectedPosition = null
        return capturedPiece
    }

    private fun setupPieces() {
        val backRank = listOf(
            PieceType.ROOK,
            PieceType.KNIGHT,
            PieceType.BISHOP,
            PieceType.QUEEN,
            PieceType.KING,
            PieceType.BISHOP,
            PieceType.KNIGHT,
            PieceType.ROOK
        )

        for (column in 0..7) {
            board.add(Piece(Color.BLACK, backRank[column], Position(0, column)))
            board.add(Piece(Color.BLACK, PieceType.PAWN, Position(1, column)))
            board.add(Piece(Color.WHITE, PieceType.PAWN, Position(6, column)))
            board.add(Piece(Color.WHITE, backRank[column], Position(7, column)))
        }
    }
}
