package rules

import model.Board
import model.Color
import model.LastMove
import model.Move
import model.Piece
import model.Position

/** Generates movement-valid candidate moves before king-safety filtering. */
class MoveGenerator(
    private val validator: MoveValidator = MoveValidator()
) {
    fun candidateMoves(board: Board, color: Color, lastMove: LastMove? = null): List<Move> =
        board.pieces()
            .asSequence()
            .filter { it.color == color }
            .flatMap { piece -> candidateMovesForPiece(board, color, piece, lastMove).asSequence() }
            .toList()

    private fun candidateMovesForPiece(
        board: Board,
        color: Color,
        piece: Piece,
        lastMove: LastMove?
    ): List<Move> = buildList {
        for (row in 0..7) {
            for (column in 0..7) {
                val destination = Position(row, column)
                val move = Move(piece.position, destination)
                if (validator.validate(board, color, move, lastMove) == ValidationResult.Valid) {
                    add(move)
                }
            }
        }
    }
}
