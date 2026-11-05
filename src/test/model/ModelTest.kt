package model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ModelTest {
    @Test
    fun `accepts positions on the board boundaries`() {
        assertEquals(Position(0, 0), Position(0, 0))
        assertEquals(Position(7, 7), Position(7, 7))
    }

    @Test
    fun `rejects rows outside the board`() {
        assertFailsWith<IllegalArgumentException> { Position(-1, 0) }
        assertFailsWith<IllegalArgumentException> { Position(8, 0) }
    }

    @Test
    fun `rejects columns outside the board`() {
        assertFailsWith<IllegalArgumentException> { Position(0, -1) }
        assertFailsWith<IllegalArgumentException> { Position(0, 8) }
    }

    @Test
    fun `stores a piece color type and position`() {
        val position = Position(6, 0)
        val piece = Piece(Color.WHITE, PieceType.PAWN, position)

        assertEquals(Color.WHITE, piece.color)
        assertEquals(PieceType.PAWN, piece.type)
        assertEquals(position, piece.position)
    }

    @Test
    fun `allows a piece position to be updated`() {
        val piece = Piece(Color.WHITE, PieceType.PAWN, Position(6, 0))

        piece.position = Position(5, 0)

        assertEquals(Position(5, 0), piece.position)
    }

    @Test
    fun `board stores and retrieves a piece by position`() {
        val board = Board()
        val pawn = Piece(Color.WHITE, PieceType.PAWN, Position(6, 0))

        board.add(pawn)

        assertEquals(pawn, board.pieceAt(Position(6, 0)))
    }

    @Test
    fun `board returns null for an empty position`() {
        val board = Board()

        assertEquals(null, board.pieceAt(Position(6, 0)))
    }

    @Test
    fun `board rejects two pieces in the same position`() {
        val board = Board()
        board.add(Piece(Color.WHITE, PieceType.PAWN, Position(6, 0)))

        assertFailsWith<IllegalArgumentException> {
            board.add(Piece(Color.BLACK, PieceType.PAWN, Position(6, 0)))
        }
    }
}
