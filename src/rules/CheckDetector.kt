package rules

import model.Board
import model.Color
import model.Move
import model.PieceType

/** Detects whether a king is currently attacked by an opposing piece. */
class CheckDetector(
    private val validator: MoveValidator = MoveValidator()
) {
    fun isInCheck(board: Board, color: Color): Boolean {
        val king = board.pieces().firstOrNull {
            it.color == color && it.type == PieceType.KING
        } ?: return false

        return board.pieces()
            .asSequence()
            .filter { it.color == color.opposite() }
            .any { attacker ->
                validator.validate(
                    board,
                    attacker.color,
                    Move(attacker.position, king.position)
                ) == ValidationResult.Valid
            }
    }
}
