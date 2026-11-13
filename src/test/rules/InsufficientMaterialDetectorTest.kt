package rules

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import model.Board
import model.Color
import model.Piece
import model.PieceType
import model.Position

class InsufficientMaterialDetectorTest {
    private val detector = InsufficientMaterialDetector()

    @Test
    fun `detects king versus king`() {
        assertTrue(detector.isInsufficientMaterial(boardWith(
            Piece(Color.WHITE, PieceType.KING, Position(7, 4)),
            Piece(Color.BLACK, PieceType.KING, Position(0, 4))
        )))
    }

    @Test
    fun `detects king and bishop versus king`() {
        assertTrue(detector.isInsufficientMaterial(boardWith(
            Piece(Color.WHITE, PieceType.KING, Position(7, 4)),
            Piece(Color.WHITE, PieceType.BISHOP, Position(4, 2)),
            Piece(Color.BLACK, PieceType.KING, Position(0, 4))
        )))
    }

    @Test
    fun `detects king and knight versus king`() {
        assertTrue(detector.isInsufficientMaterial(boardWith(
            Piece(Color.WHITE, PieceType.KING, Position(7, 4)),
            Piece(Color.WHITE, PieceType.KNIGHT, Position(4, 2)),
            Piece(Color.BLACK, PieceType.KING, Position(0, 4))
        )))
    }

    @Test
    fun `detects same colored bishop versus bishop`() {
        assertTrue(detector.isInsufficientMaterial(boardWith(
            Piece(Color.WHITE, PieceType.KING, Position(7, 4)),
            Piece(Color.WHITE, PieceType.BISHOP, Position(4, 2)),
            Piece(Color.BLACK, PieceType.KING, Position(0, 4)),
            Piece(Color.BLACK, PieceType.BISHOP, Position(1, 5))
        )))
    }

    @Test
    fun `rejects opposite colored bishops`() {
        assertFalse(detector.isInsufficientMaterial(boardWith(
            Piece(Color.WHITE, PieceType.KING, Position(7, 4)),
            Piece(Color.WHITE, PieceType.BISHOP, Position(4, 2)),
            Piece(Color.BLACK, PieceType.KING, Position(0, 4)),
            Piece(Color.BLACK, PieceType.BISHOP, Position(1, 4))
        )))
    }

    @Test
    fun `rejects pawns rooks queens and unapproved minor material`() {
        val kings = arrayOf(
            Piece(Color.WHITE, PieceType.KING, Position(7, 4)),
            Piece(Color.BLACK, PieceType.KING, Position(0, 4))
        )

        assertFalse(detector.isInsufficientMaterial(boardWith(*kings, Piece(Color.WHITE, PieceType.PAWN, Position(6, 4)))))
        assertFalse(detector.isInsufficientMaterial(boardWith(*kings, Piece(Color.WHITE, PieceType.ROOK, Position(6, 4)))))
        assertFalse(detector.isInsufficientMaterial(boardWith(*kings, Piece(Color.WHITE, PieceType.QUEEN, Position(6, 4)))))
        assertFalse(detector.isInsufficientMaterial(boardWith(
            *kings,
            Piece(Color.WHITE, PieceType.KNIGHT, Position(6, 2)),
            Piece(Color.WHITE, PieceType.KNIGHT, Position(5, 3))
        )))
    }

    @Test
    fun `does not mutate the board while inspecting it`() {
        val board = boardWith(
            Piece(Color.WHITE, PieceType.KING, Position(7, 4)),
            Piece(Color.WHITE, PieceType.BISHOP, Position(4, 2)),
            Piece(Color.BLACK, PieceType.KING, Position(0, 4))
        )
        val before = board.pieces().map { Triple(it.color, it.type, it.position) }.toSet()

        detector.isInsufficientMaterial(board)

        val after = board.pieces().map { Triple(it.color, it.type, it.position) }.toSet()
        assertTrue(before == after)
    }

    private fun boardWith(vararg pieces: Piece): Board = Board().also { board ->
        pieces.forEach(board::add)
    }
}
