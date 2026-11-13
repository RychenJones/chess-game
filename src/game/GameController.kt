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
import rules.LegalMoveFinder
import rules.MoveValidator
import rules.ValidationResult

class GameController(
    val state: GameState = GameState(),
    private val validator: MoveValidator = MoveValidator(),
    private val checkDetector: CheckDetector = CheckDetector(),
    private val legalMoveFinder: LegalMoveFinder = LegalMoveFinder(checkDetector = checkDetector)
) {
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
            .legalMoves(state.board, state.currentPlayer)
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
                simulatedBoard.move(move.from, move.to)
                if (checkDetector.isInCheck(simulatedBoard, movingColor)) {
                    return MoveResult.Failure(
                        "Move would leave ${movingColor.name.lowercase()} king in check"
                    )
                }

                undoSnapshot = state.snapshot()
                val captured = state.applyMove(move)
                state.setPlayerInCheck(
                    state.currentPlayer.takeIf { checkDetector.isInCheck(state.board, it) }
                )
                if (
                    legalMoveFinder.legalMoves(state.board, state.currentPlayer).isEmpty()
                ) {
                    val terminalStatus = if (state.playerInCheck != null) {
                        GameStatus.CHECKMATE
                    } else {
                        GameStatus.STALEMATE
                    }
                    val winner = movingColor.takeIf { terminalStatus == GameStatus.CHECKMATE }
                    state.setGameResult(terminalStatus, winner)
                } else {
                    state.setGameResult(GameStatus.ACTIVE, null)
                }
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
        if (state.gameStatus == GameStatus.STALEMATE) {
            return MoveResult.Failure("Game over — Stalemate draw")
        }
        val winnerName = state.winner?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "The opposing player"
        return MoveResult.Failure("Game over — $winnerName wins")
    }
}

sealed class MoveResult {
    data class Success(val captured: Piece?) : MoveResult()

    data class Failure(val reason: String) : MoveResult()
}
