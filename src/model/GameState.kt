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

    var playerInCheck: Color? by mutableStateOf(null)
        private set

    var gameStatus: GameStatus by mutableStateOf(GameStatus.ACTIVE)
        private set

    var winner: Color? by mutableStateOf(null)
        private set

    var lastMove: LastMove? by mutableStateOf(null)
        private set

    var halfMoveClock: Int by mutableStateOf(0)
        private set

    init {
        reset()
    }

    fun reset() {
        board.clear()
        setupPieces()
        currentPlayer = Color.WHITE
        selectedPosition = null
        playerInCheck = null
        gameStatus = GameStatus.ACTIVE
        winner = null
        lastMove = null
        halfMoveClock = 0
    }

    internal fun setPlayerInCheck(color: Color?) {
        playerInCheck = color
    }

    internal fun setGameResult(status: GameStatus, winningColor: Color?) {
        gameStatus = status
        winner = winningColor
    }

    internal fun snapshot(): GameStateSnapshot = GameStateSnapshot(
        board = board.copy(),
        currentPlayer = currentPlayer,
        selectedPosition = selectedPosition,
        playerInCheck = playerInCheck,
        gameStatus = gameStatus,
        winner = winner,
        lastMove = lastMove,
        halfMoveClock = halfMoveClock
    )

    internal fun restore(snapshot: GameStateSnapshot) {
        board.restoreFrom(snapshot.board)
        currentPlayer = snapshot.currentPlayer
        selectedPosition = snapshot.selectedPosition
        playerInCheck = snapshot.playerInCheck
        gameStatus = snapshot.gameStatus
        winner = snapshot.winner
        lastMove = snapshot.lastMove
        halfMoveClock = snapshot.halfMoveClock
    }

    internal fun clearSelection() {
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
    fun applyMove(move: Move, capturedPosition: Position? = null): Piece? {
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

        val capturedPiece = board.move(move.from, move.to, capturedPosition)
        halfMoveClock = if (movingPiece.type == PieceType.PAWN || capturedPiece != null) {
            0
        } else {
            halfMoveClock + 1
        }
        lastMove = LastMove(
            from = move.from,
            to = move.to,
            pieceColor = movingPiece.color,
            pieceType = movingPiece.type
        )
        currentPlayer = currentPlayer.opposite()
        selectedPosition = null
        return capturedPiece
    }

    /** Allows focused tests to arrange a position near the fifty-move threshold. */
    internal fun setHalfMoveClockForTesting(value: Int) {
        require(value >= 0) { "The half-move clock cannot be negative" }
        halfMoveClock = value
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

internal data class GameStateSnapshot(
    val board: Board,
    val currentPlayer: Color,
    val selectedPosition: Position?,
    val playerInCheck: Color?,
    val gameStatus: GameStatus,
    val winner: Color?,
    val lastMove: LastMove?,
    val halfMoveClock: Int
)
