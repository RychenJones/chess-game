package game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import model.Color
import model.Move
import model.Piece
import model.PieceType
import model.Position

class GameTest {
    @Test
    fun `controller applies only valid selected moves`() {
        val controller = GameController()

        assertEquals(true, controller.select(Position(6, 4)))
        val result = controller.tryMove(Position(4, 4))

        assertIs<MoveResult.Success>(result)
        assertEquals(PieceType.PAWN, controller.state.board.pieceAt(Position(4, 4))?.type)
        assertEquals(null, controller.state.board.pieceAt(Position(6, 4)))
        assertEquals(Color.BLACK, controller.state.currentPlayer)
    }

    @Test
    fun `controller leaves state unchanged when a move is invalid`() {
        val controller = GameController()
        controller.select(Position(6, 4))

        val result = controller.tryMove(Position(3, 4))

        assertIs<MoveResult.Failure>(result)
        assertEquals(PieceType.PAWN, controller.state.board.pieceAt(Position(6, 4))?.type)
        assertEquals(null, controller.state.board.pieceAt(Position(3, 4)))
        assertEquals(Color.WHITE, controller.state.currentPlayer)
    }

    @Test
    fun `controller applies captures and reports the captured piece`() {
        val controller = GameController()
        val state = controller.state
        val whitePawn = state.board.remove(Position(6, 0))!!
        val blackPawn = state.board.remove(Position(1, 1))!!
        whitePawn.position = Position(3, 0)
        blackPawn.position = Position(2, 1)
        state.board.add(whitePawn)
        state.board.add(blackPawn)

        controller.select(Position(3, 0))
        val result = controller.tryMove(Move(Position(3, 0), Position(2, 1)))

        val success = assertIs<MoveResult.Success>(result)
        assertEquals(blackPawn, success.captured)
        assertEquals(whitePawn, state.board.pieceAt(Position(2, 1)))
        assertEquals(Color.BLACK, state.currentPlayer)
    }

    @Test
    fun `controller reset restores the initial state`() {
        val controller = GameController()
        controller.select(Position(6, 4))
        controller.tryMove(Position(5, 4))

        controller.reset()

        assertEquals(Color.WHITE, controller.state.currentPlayer)
        assertEquals(PieceType.PAWN, controller.state.board.pieceAt(Position(6, 4))?.type)
        assertEquals(null, controller.state.board.pieceAt(Position(5, 4)))
    }

    @Test
    fun `controller reports check after a successful move`() {
        val controller = GameController()
        val state = controller.state
        state.board.pieces().forEach { state.board.remove(it.position) }
        state.board.add(Piece(Color.WHITE, PieceType.KING, Position(7, 4)))
        state.board.add(Piece(Color.WHITE, PieceType.ROOK, Position(6, 4)))
        state.board.add(Piece(Color.BLACK, PieceType.KING, Position(0, 4)))

        controller.select(Position(6, 4))
        val result = controller.tryMove(Position(1, 4))

        assertIs<MoveResult.Success>(result)
        assertEquals(Color.BLACK, state.playerInCheck)
    }

    @Test
    fun `controller rejects a king moving into check`() {
        val controller = GameController()
        val state = controller.state
        state.board.pieces().forEach { state.board.remove(it.position) }
        state.board.add(Piece(Color.WHITE, PieceType.KING, Position(7, 4)))
        state.board.add(Piece(Color.BLACK, PieceType.ROOK, Position(0, 5)))
        state.board.add(Piece(Color.BLACK, PieceType.KING, Position(0, 0)))

        controller.select(Position(7, 4))
        val result = controller.tryMove(Position(7, 5))

        assertIs<MoveResult.Failure>(result)
        assertEquals("Move would leave white king in check", result.reason)
        assertEquals(PieceType.KING, state.board.pieceAt(Position(7, 4))?.type)
        assertEquals(null, state.board.pieceAt(Position(7, 5)))
        assertEquals(Color.WHITE, state.currentPlayer)
    }

    @Test
    fun `controller rejects a move that exposes its own king`() {
        val controller = GameController()
        val state = controller.state
        state.board.pieces().forEach { state.board.remove(it.position) }
        state.board.add(Piece(Color.WHITE, PieceType.KING, Position(7, 4)))
        state.board.add(Piece(Color.WHITE, PieceType.ROOK, Position(6, 4)))
        state.board.add(Piece(Color.BLACK, PieceType.ROOK, Position(0, 4)))
        state.board.add(Piece(Color.BLACK, PieceType.KING, Position(0, 0)))

        controller.select(Position(6, 4))
        val result = controller.tryMove(Position(6, 7))

        assertIs<MoveResult.Failure>(result)
        assertEquals("Move would leave white king in check", result.reason)
        assertEquals(PieceType.ROOK, state.board.pieceAt(Position(6, 4))?.type)
        assertEquals(null, state.board.pieceAt(Position(6, 7)))
        assertEquals(Color.WHITE, state.currentPlayer)
    }

    @Test
    fun `controller undoes only the most recent move`() {
        val controller = GameController()

        controller.select(Position(6, 4))
        assertIs<MoveResult.Success>(controller.tryMove(Position(5, 4)))
        assertEquals(true, controller.canUndo)

        assertEquals(true, controller.undo())
        assertEquals(PieceType.PAWN, controller.state.board.pieceAt(Position(6, 4))?.type)
        assertEquals(null, controller.state.board.pieceAt(Position(5, 4)))
        assertEquals(Color.WHITE, controller.state.currentPlayer)
        assertEquals(false, controller.canUndo)
        assertEquals(false, controller.undo())
    }

    @Test
    fun `controller undo restores a captured piece`() {
        val controller = GameController()
        val state = controller.state
        val whitePawn = state.board.remove(Position(6, 0))!!
        val blackPawn = state.board.remove(Position(1, 1))!!
        whitePawn.position = Position(3, 0)
        blackPawn.position = Position(2, 1)
        state.board.add(whitePawn)
        state.board.add(blackPawn)

        controller.select(Position(3, 0))
        assertIs<MoveResult.Success>(controller.tryMove(Move(Position(3, 0), Position(2, 1))))

        assertEquals(true, controller.undo())
        assertEquals(whitePawn.type, state.board.pieceAt(Position(3, 0))?.type)
        assertEquals(blackPawn.type, state.board.pieceAt(Position(2, 1))?.type)
        assertEquals(Color.WHITE, state.currentPlayer)
    }
}
