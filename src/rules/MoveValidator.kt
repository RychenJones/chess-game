package rules

import kotlin.math.abs
import model.Board
import model.Color
import model.GameState
import model.Move
import model.Piece
import model.PieceType
import model.Position

class MoveValidator {
    fun validate(state: GameState, move: Move): ValidationResult =
        validate(state.board, state.currentPlayer, move)

    fun validate(board: Board, currentPlayer: Color, move: Move): ValidationResult {
        if (move.from == move.to) {
            return ValidationResult.Invalid("A piece cannot move to its current position")
        }

        val movingPiece = board.pieceAt(move.from)
            ?: return ValidationResult.Invalid("No piece occupies position ${move.from}")

        if (movingPiece.color != currentPlayer) {
            return ValidationResult.Invalid("It is ${currentPlayer.name.lowercase()}\'s turn")
        }

        val destinationPiece = board.pieceAt(move.to)
        if (destinationPiece?.color == movingPiece.color) {
            return ValidationResult.Invalid("A piece cannot move onto a friendly piece")
        }

        val validMovement = when (movingPiece.type) {
            PieceType.PAWN -> isValidPawnMove(board, movingPiece, move, destinationPiece)
            PieceType.ROOK -> isValidRookMove(board, move)
            PieceType.KNIGHT -> isValidKnightMove(move)
            PieceType.BISHOP -> isValidBishopMove(board, move)
            PieceType.QUEEN -> isValidQueenMove(board, move)
            PieceType.KING -> isValidKingMove(move)
        }

        return if (validMovement) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(
                "${movingPiece.type.name.lowercase().replaceFirstChar { it.uppercase() }} cannot move from " +
                    "${move.from} to ${move.to}"
            )
        }
    }

    private fun isValidPawnMove(
        board: Board,
        piece: Piece,
        move: Move,
        destination: Piece?
    ): Boolean {
        val direction = if (piece.color == Color.WHITE) -1 else 1
        val startingRow = if (piece.color == Color.WHITE) 6 else 1
        val rowDelta = move.to.row - move.from.row
        val columnDelta = move.to.column - move.from.column

        if (columnDelta == 0 && destination == null && rowDelta == direction) {
            return true
        }

        if (
            columnDelta == 0 &&
            destination == null &&
            move.from.row == startingRow &&
            rowDelta == 2 * direction
        ) {
            val middle = Position(move.from.row + direction, move.from.column)
            return board.pieceAt(middle) == null
        }

        return abs(columnDelta) == 1 && rowDelta == direction && destination != null
    }

    private fun isValidRookMove(board: Board, move: Move): Boolean {
        val straight = move.from.row == move.to.row || move.from.column == move.to.column
        return straight && PathChecker.isClear(board, move.from, move.to)
    }

    private fun isValidKnightMove(move: Move): Boolean {
        val rowDelta = abs(move.to.row - move.from.row)
        val columnDelta = abs(move.to.column - move.from.column)
        return (rowDelta == 2 && columnDelta == 1) ||
            (rowDelta == 1 && columnDelta == 2)
    }

    private fun isValidBishopMove(board: Board, move: Move): Boolean {
        val rowDelta = abs(move.to.row - move.from.row)
        val columnDelta = abs(move.to.column - move.from.column)
        return rowDelta == columnDelta && rowDelta > 0 &&
            PathChecker.isClear(board, move.from, move.to)
    }

    private fun isValidQueenMove(board: Board, move: Move): Boolean {
        val straight = move.from.row == move.to.row || move.from.column == move.to.column
        val diagonal = abs(move.to.row - move.from.row) == abs(move.to.column - move.from.column)
        return (straight || diagonal) && PathChecker.isClear(board, move.from, move.to)
    }

    private fun isValidKingMove(move: Move): Boolean =
        maxOf(abs(move.to.row - move.from.row), abs(move.to.column - move.from.column)) == 1
}
