package rules

import model.Board
import model.Position

object PathChecker {
    /**
     * Checks only the squares between the origin and destination. The caller
     * is responsible for confirming that the move is a straight or diagonal
     * line before calling this function.
     */
    fun isClear(board: Board, from: Position, to: Position): Boolean {
        val rowStep = step(to.row - from.row)
        val columnStep = step(to.column - from.column)
        var row = from.row + rowStep
        var column = from.column + columnStep

        while (row != to.row || column != to.column) {
            if (board.pieceAt(Position(row, column)) != null) {
                return false
            }

            row += rowStep
            column += columnStep
        }

        return true
    }

    private fun step(delta: Int): Int =
        when {
            delta < 0 -> -1
            delta > 0 -> 1
            else -> 0
        }
}
