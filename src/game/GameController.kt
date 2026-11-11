package game

import model.GameState
import model.Move
import model.Piece
import model.Position
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import rules.CheckDetector
import rules.MoveValidator
import rules.ValidationResult

class GameController(
    val state: GameState = GameState(),
    private val validator: MoveValidator = MoveValidator(),
    private val checkDetector: CheckDetector = CheckDetector(validator)
) {
    var canUndo: Boolean by mutableStateOf(false)
        private set

    private var undoSnapshot: model.GameStateSnapshot? = null

    fun select(position: Position): Boolean =
        state.select(position)

    fun tryMove(to: Position): MoveResult {
        val from = state.selectedPosition
            ?: return MoveResult.Failure("Select a piece before moving")

        return tryMove(Move(from, to))
    }

    fun tryMove(move: Move): MoveResult {
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
}

sealed class MoveResult {
    data class Success(val captured: Piece?) : MoveResult()

    data class Failure(val reason: String) : MoveResult()
}
