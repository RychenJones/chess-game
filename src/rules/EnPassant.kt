package rules

import kotlin.math.abs
import model.Board
import model.Color
import model.LastMove
import model.Move
import model.PieceType
import model.Position

/** Encapsulates the history-dependent eligibility rules for en passant. */
object EnPassant {
    fun capturedPawnPosition(
        board: Board,
        movingColor: Color,
        move: Move,
        lastMove: LastMove?
    ): Position? {
        val movingPiece = board.pieceAt(move.from) ?: return null
        if (movingPiece.color != movingColor || movingPiece.type != PieceType.PAWN) {
            return null
        }

        val direction = if (movingColor == Color.WHITE) -1 else 1
        val rowDelta = move.to.row - move.from.row
        val columnDelta = move.to.column - move.from.column
        if (abs(columnDelta) != 1 || rowDelta != direction || board.pieceAt(move.to) != null) {
            return null
        }

        val adjacentPosition = Position(move.from.row, move.to.column)
        val adjacentPiece = board.pieceAt(adjacentPosition)
        if (
            adjacentPiece?.color != movingColor.opposite() ||
            adjacentPiece.type != PieceType.PAWN ||
            lastMove == null ||
            lastMove.pieceColor != movingColor.opposite() ||
            !lastMove.isTwoSquarePawnAdvance() ||
            lastMove.to != adjacentPosition
        ) {
            return null
        }

        return adjacentPosition
    }
}
