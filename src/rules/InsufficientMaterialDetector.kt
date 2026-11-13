package rules

import model.Board
import model.Color
import model.PieceType
import model.Position

/** Detects the explicitly supported insufficient-material draw positions. */
class InsufficientMaterialDetector {
    fun isInsufficientMaterial(board: Board): Boolean {
        val pieces = board.pieces()
        val kings = pieces.filter { it.type == PieceType.KING }
        if (kings.size != 2 || kings.map { it.color }.toSet().size != 2) {
            return false
        }

        val nonKings = pieces.filter { it.type != PieceType.KING }
        return when {
            nonKings.isEmpty() -> true
            nonKings.size == 1 && nonKings.single().type in setOf(PieceType.BISHOP, PieceType.KNIGHT) -> true
            nonKings.size == 2 &&
                nonKings.all { it.type == PieceType.BISHOP } &&
                nonKings.map { it.color }.toSet() == setOf(Color.WHITE, Color.BLACK) &&
                nonKings.map { it.position.squareColor() }.distinct().size == 1 -> true
            else -> false
        }
    }

    private fun Position.squareColor(): Int = (row + column) % 2
}
