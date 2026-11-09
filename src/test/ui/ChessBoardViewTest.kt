package ui

import kotlin.test.Test
import kotlin.test.assertEquals
import model.Color
import model.Piece
import model.Position

class ChessBoardViewTest {
    @Test
    fun `describes each piece by color and type`() {
        assertEquals(
            "White Pawn",
            pieceDescription(Piece(Color.WHITE, model.PieceType.PAWN, Position(0, 0)))
        )
        assertEquals(
            "Black King",
            pieceDescription(Piece(Color.BLACK, model.PieceType.KING, Position(0, 0)))
        )
    }
}
