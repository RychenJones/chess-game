package game

import model.GameState
import model.Move
import model.Piece
import model.Position
import rules.MoveValidator
import rules.ValidationResult

class GameController(
    val state: GameState = GameState(),
    private val validator: MoveValidator = MoveValidator()
) {
    fun select(position: Position): Boolean =
        state.select(position)

    fun tryMove(to: Position): MoveResult {
        val from = state.selectedPosition
            ?: return MoveResult.Failure("Select a piece before moving")

        return tryMove(Move(from, to))
    }

    fun tryMove(move: Move): MoveResult {
        if (state.selectedPosition != move.from) {
            return MoveResult.Failure("Select the piece at ${move.from} before moving")
        }

        return when (val validation = validator.validate(state, move)) {
            ValidationResult.Valid ->
                MoveResult.Success(state.applyMove(move))

            is ValidationResult.Invalid ->
                MoveResult.Failure(validation.reason)
        }
    }

    fun reset() {
        state.reset()
    }
}

sealed class MoveResult {
    data class Success(val captured: Piece?) : MoveResult()

    data class Failure(val reason: String) : MoveResult()
}
