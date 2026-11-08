package game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import model.Color
import model.Move
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
}
