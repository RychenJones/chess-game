package rules

import kotlin.math.abs
import model.Board
import model.Color
import model.Piece
import model.PieceType
import model.Position

/** Determines whether a piece attacks a target square, independent of turn or destination occupancy. */
class AttackDetector {
    fun attacks(board: Board, attacker: Piece, target: Position): Boolean {
        if (attacker.position == target) {
            return false
        }

        val rowDelta = target.row - attacker.position.row
        val columnDelta = target.column - attacker.position.column
        val absoluteRowDelta = abs(rowDelta)
        val absoluteColumnDelta = abs(columnDelta)

        return when (attacker.type) {
            PieceType.PAWN -> {
                val direction = if (attacker.color == Color.WHITE) -1 else 1
                rowDelta == direction && absoluteColumnDelta == 1
            }

            PieceType.ROOK -> {
                val straight = rowDelta == 0 || columnDelta == 0
                straight && PathChecker.isClear(board, attacker.position, target)
            }

            PieceType.KNIGHT -> {
                (absoluteRowDelta == 2 && absoluteColumnDelta == 1) ||
                    (absoluteRowDelta == 1 && absoluteColumnDelta == 2)
            }

            PieceType.BISHOP -> {
                absoluteRowDelta == absoluteColumnDelta && absoluteRowDelta > 0 &&
                    PathChecker.isClear(board, attacker.position, target)
            }

            PieceType.QUEEN -> {
                val straight = rowDelta == 0 || columnDelta == 0
                val diagonal = absoluteRowDelta == absoluteColumnDelta
                (straight || diagonal) && PathChecker.isClear(board, attacker.position, target)
            }

            PieceType.KING -> maxOf(absoluteRowDelta, absoluteColumnDelta) == 1
        }
    }
}
