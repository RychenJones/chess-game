package ui

import kotlin.test.Test
import kotlin.test.assertEquals
import model.Color
import model.GameState
import model.GameStatus
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

    @Test
    fun `formats checkmate message from winner`() {
        assertEquals("Checkmate — White wins", checkmateMessage(Color.WHITE))
        assertEquals("Checkmate — Black wins", checkmateMessage(Color.BLACK))
    }

    @Test
    fun `formats stalemate as a draw`() {
        assertEquals("Stalemate — draw", stalemateMessage())
    }

    @Test
    fun `game state exposes checkmate status for UI rendering`() {
        val state = GameState()
        state.setGameResult(GameStatus.CHECKMATE, Color.WHITE)

        assertEquals(GameStatus.CHECKMATE, state.gameStatus)
        assertEquals(Color.WHITE, state.winner)
    }

    @Test
    fun `game state exposes stalemate without a winner`() {
        val state = GameState()
        state.setGameResult(GameStatus.STALEMATE, null)

        assertEquals(GameStatus.STALEMATE, state.gameStatus)
        assertEquals(null, state.winner)
    }
}
