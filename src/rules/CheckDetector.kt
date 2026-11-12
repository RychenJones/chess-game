package rules

import model.Board
import model.Color
import model.PieceType

/** Detects whether a king is currently attacked by an opposing piece. */
class CheckDetector(
    private val attackDetector: AttackDetector = AttackDetector()
) {
    fun isInCheck(board: Board, color: Color): Boolean {
        val king = board.pieces().firstOrNull {
            it.color == color && it.type == PieceType.KING
        } ?: return false

        return board.pieces()
            .asSequence()
            .filter { it.color == color.opposite() }
            .any { attacker -> attackDetector.attacks(board, attacker, king.position) }
    }
}
