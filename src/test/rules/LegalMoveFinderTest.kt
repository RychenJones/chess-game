package rules

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import model.Board
import model.Color
import model.Move
import model.Piece
import model.PieceType
import model.Position

class LegalMoveFinderTest {
    private val finder = LegalMoveFinder()

    @Test
    fun `filters a rook move that exposes its own king`() {
        val board = boardOf(
            Piece(Color.WHITE, PieceType.KING, Position(7, 4)),
            Piece(Color.WHITE, PieceType.ROOK, Position(6, 4)),
            Piece(Color.BLACK, PieceType.ROOK, Position(0, 4)),
            Piece(Color.BLACK, PieceType.KING, Position(0, 0))
        )
        val exposedMove = Move(Position(6, 4), Position(6, 7))

        assertFalse(exposedMove in finder.legalMoves(board, Color.WHITE))
    }

    @Test
    fun `filters a king move into rook attack but keeps a safe king move`() {
        val board = boardOf(
            Piece(Color.WHITE, PieceType.KING, Position(7, 4)),
            Piece(Color.BLACK, PieceType.ROOK, Position(0, 5)),
            Piece(Color.BLACK, PieceType.KING, Position(0, 0))
        )
        val moves = finder.legalMoves(board, Color.WHITE)

        assertFalse(Move(Position(7, 4), Position(7, 5)) in moves)
        assertTrue(Move(Position(7, 4), Position(7, 3)) in moves)
    }

    @Test
    fun `preserves the starting position candidate count when all moves are safe`() {
        val board = Board()
        val backRank = listOf(
            PieceType.ROOK,
            PieceType.KNIGHT,
            PieceType.BISHOP,
            PieceType.QUEEN,
            PieceType.KING,
            PieceType.BISHOP,
            PieceType.KNIGHT,
            PieceType.ROOK
        )
        for (column in 0..7) {
            board.add(Piece(Color.BLACK, backRank[column], Position(0, column)))
            board.add(Piece(Color.BLACK, PieceType.PAWN, Position(1, column)))
            board.add(Piece(Color.WHITE, PieceType.PAWN, Position(6, column)))
            board.add(Piece(Color.WHITE, backRank[column], Position(7, column)))
        }

        assertEquals(20, finder.legalMoves(board, Color.WHITE).size)
    }

    private fun boardOf(vararg pieces: Piece): Board =
        Board().also { board -> pieces.forEach(board::add) }
}
