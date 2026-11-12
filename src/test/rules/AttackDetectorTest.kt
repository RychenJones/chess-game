package rules

import kotlin.test.Test
import kotlin.test.assertEquals
import model.Board
import model.Color
import model.Piece
import model.PieceType
import model.Position

class AttackDetectorTest {
    private val detector = AttackDetector()

    @Test
    fun `pawn attacks an empty diagonal square`() {
        val board = Board()
        val pawn = Piece(Color.WHITE, PieceType.PAWN, Position(4, 4))
        board.add(pawn)

        assertEquals(true, detector.attacks(board, pawn, Position(3, 5)))
        assertEquals(false, detector.attacks(board, pawn, Position(3, 4)))
    }

    @Test
    fun `sliding attacks stop at blocking pieces`() {
        val board = Board()
        val rook = Piece(Color.WHITE, PieceType.ROOK, Position(7, 0))
        board.add(rook)
        board.add(Piece(Color.WHITE, PieceType.PAWN, Position(5, 0)))

        assertEquals(false, detector.attacks(board, rook, Position(3, 0)))
        assertEquals(true, detector.attacks(board, rook, Position(6, 0)))
    }
}
