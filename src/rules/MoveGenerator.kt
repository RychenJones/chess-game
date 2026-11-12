package rules

import model.Board
import model.Color
import model.Move
import model.Piece
import model.Position

/** Generates movement-valid candidate moves before king-safety filtering. */
class MoveGenerator(
    private val validator: MoveValidator = MoveValidator()
) {
    fun candidateMoves(board: Board, color: Color): List<Move> =
        board.pieces()
            .asSequence()
            .filter { it.color == color }
            .flatMap { piece -> candidateMovesForPiece(board, color, piece).asSequence() }
            .toList()

    private fun candidateMovesForPiece(
        board: Board,
        color: Color,
        piece: Piece
    ): List<Move> = buildList {
        for (row in 0..7) {
            for (column in 0..7) {
                val destination = Position(row, column)
                val move = Move(piece.position, destination)
                if (validator.validate(board, color, move) == ValidationResult.Valid) {
                    add(move)
                }
            }
        }
    }
}
