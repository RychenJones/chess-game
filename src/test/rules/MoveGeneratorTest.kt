package rules

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import model.Board
import model.Color
import model.GameState
import model.Move
import model.Piece
import model.PieceType
import model.Position

class MoveGeneratorTest {
    private val generator = MoveGenerator()

    @Test
    fun `generates twenty candidate moves for either player at the starting position`() {
        val state = GameState()

        assertEquals(20, generator.candidateMoves(state.board, Color.WHITE).size)
        assertEquals(20, generator.candidateMoves(state.board, Color.BLACK).size)
    }

    @Test
    fun `generates pawn captures and excludes friendly destinations`() {
        val board = Board()
        val pawn = Piece(Color.WHITE, PieceType.PAWN, Position(4, 4))
        board.add(pawn)
        board.add(Piece(Color.BLACK, PieceType.KNIGHT, Position(3, 5)))
        board.add(Piece(Color.WHITE, PieceType.PAWN, Position(3, 3)))

        val moves = generator.candidateMoves(board, Color.WHITE)

        assertTrue(Move(Position(4, 4), Position(3, 5)) in moves)
        assertTrue(Move(Position(4, 4), Position(3, 3)) !in moves)
    }
}
