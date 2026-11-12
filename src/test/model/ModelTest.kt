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
    fun `formats positions using standard chess coordinates`() {
        assertEquals("a8", Position(0, 0).toAlgebraic())
        assertEquals("e2", Position(6, 4).toAlgebraic())
        assertEquals("h1", Position(7, 7).toAlgebraic())
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

    @Test
    fun `board moves a piece in constant time and updates its position`() {
        val board = Board()
        val pawn = Piece(Color.WHITE, PieceType.PAWN, Position(6, 0))
        board.add(pawn)

        val captured = board.move(Position(6, 0), Position(5, 0))

        assertEquals(null, captured)
        assertEquals(null, board.pieceAt(Position(6, 0)))
        assertEquals(pawn, board.pieceAt(Position(5, 0)))
        assertEquals(Position(5, 0), pawn.position)
    }

    @Test
    fun `board move returns and removes a captured piece`() {
        val board = Board()
        val attacker = Piece(Color.WHITE, PieceType.ROOK, Position(7, 0))
        val target = Piece(Color.BLACK, PieceType.PAWN, Position(5, 0))
        board.add(attacker)
        board.add(target)

        val captured = board.move(Position(7, 0), Position(5, 0))

        assertEquals(target, captured)
        assertEquals(attacker, board.pieceAt(Position(5, 0)))
        assertEquals(1, board.pieces().size)
    }

    @Test
    fun `game state starts with the standard thirty two pieces`() {
        val state = GameState()

        assertEquals(32, state.board.pieces().size)
        assertEquals(Color.WHITE, state.currentPlayer)
        assertEquals(Piece(Color.BLACK, PieceType.ROOK, Position(0, 0)).type,
            state.board.pieceAt(Position(0, 0))?.type)
        assertEquals(PieceType.KING, state.board.pieceAt(Position(7, 4))?.type)
        assertEquals(PieceType.PAWN, state.board.pieceAt(Position(6, 3))?.type)
    }

    @Test
    fun `game state selects only the current players piece`() {
        val state = GameState()

        assertEquals(true, state.select(Position(6, 0)))
        assertEquals(Position(6, 0), state.selectedPosition)
        assertEquals(false, state.select(Position(1, 0)))
        assertEquals(null, state.selectedPosition)
    }

    @Test
    fun `game state applies a capture and changes turns`() {
        val state = GameState()
        val whitePawn = state.board.remove(Position(6, 0))!!
        val blackPawn = state.board.remove(Position(1, 0))!!
        whitePawn.position = Position(3, 0)
        blackPawn.position = Position(2, 1)
        state.board.add(whitePawn)
        state.board.add(blackPawn)

        state.select(Position(3, 0))
        val captured = state.applyMove(Move(Position(3, 0), Position(2, 1)))

        assertEquals(blackPawn, captured)
        assertEquals(Color.BLACK, state.currentPlayer)
        assertEquals(null, state.selectedPosition)
        assertEquals(whitePawn, state.board.pieceAt(Position(2, 1)))
    }

    @Test
    fun `reset restores the initial game state`() {
        val state = GameState()
        state.select(Position(6, 0))
        state.applyMove(Move(Position(6, 0), Position(5, 0)))

        state.reset()

        assertEquals(Color.WHITE, state.currentPlayer)
        assertEquals(null, state.selectedPosition)
        assertEquals(PieceType.PAWN, state.board.pieceAt(Position(6, 0))?.type)
        assertEquals(null, state.board.pieceAt(Position(5, 0)))
        assertEquals(32, state.board.pieces().size)
    }

    @Test
    fun `reset clears game-ending state`() {
        val state = GameState()
        state.setPlayerInCheck(Color.BLACK)
        state.setGameResult(GameStatus.CHECKMATE, Color.WHITE)

        state.reset()

        assertEquals(GameStatus.ACTIVE, state.gameStatus)
        assertEquals(null, state.winner)
        assertEquals(null, state.playerInCheck)
    }
}
