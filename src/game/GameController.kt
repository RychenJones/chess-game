package game

import model.GameState
import model.GameStatus
import model.Move
import model.Piece
import model.Position
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import rules.CheckDetector
import rules.EnPassant
import rules.InsufficientMaterialDetector
import rules.LegalMoveFinder
import rules.MoveValidator
import rules.ValidationResult

class GameController(
    val state: GameState = GameState(),
    private val validator: MoveValidator = MoveValidator(),
    private val checkDetector: CheckDetector = CheckDetector(),
    private val legalMoveFinder: LegalMoveFinder = LegalMoveFinder(checkDetector = checkDetector),
    private val insufficientMaterialDetector: InsufficientMaterialDetector = InsufficientMaterialDetector()
) {
    private companion object {
        const val FIFTY_MOVE_DRAW_THRESHOLD = 100
    }

    var canUndo: Boolean by mutableStateOf(false)
        private set

    private var undoSnapshot: model.GameStateSnapshot? = null

    fun select(position: Position): Boolean =
        state.gameStatus == GameStatus.ACTIVE && state.select(position)

    fun deselect() {
        state.clearSelection()
    }

    /**
     * Returns the legal destinations for the currently selected piece.
     * Movement rules remain owned by [LegalMoveFinder]; the UI only consumes
     * these positions to render selection feedback.
     */
    fun legalDestinationsForSelected(): Set<Position> {
        val selectedPosition = state.selectedPosition ?: return emptySet()
        val selectedPiece = state.board.pieceAt(selectedPosition) ?: return emptySet()
        if (
            state.gameStatus != GameStatus.ACTIVE ||
            selectedPiece.color != state.currentPlayer
        ) {
            return emptySet()
        }

        return legalMoveFinder
            .legalMoves(state)
            .asSequence()
            .filter { it.from == selectedPosition }
            .map { it.to }
            .toSet()
    }

    fun tryMove(to: Position): MoveResult {
        if (state.gameStatus != GameStatus.ACTIVE) {
            return gameOverFailure()
        }

        val from = state.selectedPosition
            ?: return MoveResult.Failure("Select a piece before moving")

        return tryMove(Move(from, to))
    }

    fun tryMove(move: Move): MoveResult {
        if (state.gameStatus != GameStatus.ACTIVE) {
            return gameOverFailure()
        }

        if (state.selectedPosition != move.from) {
            return MoveResult.Failure("Select the piece at ${move.from.toAlgebraic()} before moving")
        }

        return when (val validation = validator.validate(state, move)) {
            ValidationResult.Valid -> {
                val movingColor = state.currentPlayer
                val simulatedBoard = state.board.copy()
                EnPassant.capturedPawnPosition(
                    simulatedBoard,
                    movingColor,
                    move,
                    state.lastMove
                )?.let(simulatedBoard::remove)
                simulatedBoard.move(move.from, move.to)
                if (checkDetector.isInCheck(simulatedBoard, movingColor)) {
                    return MoveResult.Failure(
                        "Move would leave ${movingColor.name.lowercase()} king in check"
                    )
                }

                undoSnapshot = state.snapshot()
                val capturedPosition = EnPassant.capturedPawnPosition(
                    state.board,
                    movingColor,
                    move,
                    state.lastMove
                )
                val captured = state.applyMove(move, capturedPosition)
                state.setPlayerInCheck(
                    state.currentPlayer.takeIf { checkDetector.isInCheck(state.board, it) }
                )
                val legalMoves = legalMoveFinder.legalMoves(state)
                val terminalStatus = when {
                    legalMoves.isEmpty() && state.playerInCheck != null -> GameStatus.CHECKMATE
                    legalMoves.isEmpty() -> GameStatus.STALEMATE
                    insufficientMaterialDetector.isInsufficientMaterial(state.board) ->
                        GameStatus.INSUFFICIENT_MATERIAL
                    state.halfMoveClock >= FIFTY_MOVE_DRAW_THRESHOLD -> GameStatus.FIFTY_MOVE_DRAW
                    else -> GameStatus.ACTIVE
                }
                val winner = movingColor.takeIf { terminalStatus == GameStatus.CHECKMATE }
                state.setGameResult(terminalStatus, winner)
                canUndo = true
                MoveResult.Success(captured)
            }

            is ValidationResult.Invalid ->
                MoveResult.Failure(validation.reason)
        }
    }

    fun reset() {
        state.reset()
        undoSnapshot = null
        canUndo = false
    }

    fun undo(): Boolean {
        val snapshot = undoSnapshot ?: return false

        state.restore(snapshot)
        state.clearSelection()
        undoSnapshot = null
        canUndo = false
        return true
    }

    private fun gameOverFailure(): MoveResult.Failure {
        return when (state.gameStatus) {
            GameStatus.STALEMATE -> MoveResult.Failure("Game over — Stalemate draw")
            GameStatus.INSUFFICIENT_MATERIAL ->
                MoveResult.Failure("Game over — Insufficient material draw")
            GameStatus.FIFTY_MOVE_DRAW -> MoveResult.Failure("Game over — Fifty-move rule draw")
            else -> {
                val winnerName = state.winner?.name?.lowercase()?.replaceFirstChar { it.uppercase() }
                    ?: "The opposing player"
                MoveResult.Failure("Game over — $winnerName wins")
            }
        }
    }
}

sealed class MoveResult {
    data class Success(val captured: Piece?) : MoveResult()

    data class Failure(val reason: String) : MoveResult()
}
