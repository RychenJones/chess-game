package rules

import kotlin.test.Test
import kotlin.test.assertEquals
import model.Board
import model.Color
import model.GameState
import model.Move
import model.Piece
import model.PieceType
import model.Position

class RulesTest {
    private val validator = MoveValidator()

    @Test
    fun `allows a pawn to move one or two squares from its starting row`() {
        val state = GameState()

        assertValid(state, Move(Position(6, 4), Position(5, 4)))
        assertValid(state, Move(Position(6, 4), Position(4, 4)))
    }

    @Test
    fun `rejects a pawn moving through a piece or moving sideways without capture`() {
        val state = GameState()

        assertInvalid(state, Move(Position(6, 4), Position(3, 4)))
        assertInvalid(state, Move(Position(6, 4), Position(5, 5)))
    }

    @Test
    fun `allows pawn diagonal capture`() {
        val board = Board()
        board.add(Piece(Color.WHITE, PieceType.PAWN, Position(4, 4)))
        board.add(Piece(Color.BLACK, PieceType.KNIGHT, Position(3, 5)))

        assertValid(board, Color.WHITE, Move(Position(4, 4), Position(3, 5)))
    }

    @Test
    fun `allows en passant only after an adjacent opposing pawn double step`() {
        val state = enPassantState()

        state.select(Position(7, 0))
        state.applyMove(Move(Position(7, 0), Position(7, 1)))
        state.select(Position(1, 5))
        state.applyMove(Move(Position(1, 5), Position(3, 5)))

        assertValid(state, Move(Position(3, 4), Position(2, 5)))
    }

    @Test
    fun `rejects en passant when the opposing pawn is not adjacent`() {
        val state = enPassantState()
        val blackPawn = state.board.remove(Position(1, 5))!!
        blackPawn.position = Position(1, 6)
        state.board.add(blackPawn)

        state.select(Position(7, 0))
        state.applyMove(Move(Position(7, 0), Position(7, 1)))
        state.select(Position(1, 6))
        state.applyMove(Move(Position(1, 6), Position(3, 6)))

        assertInvalid(state, Move(Position(3, 4), Position(2, 5)))
    }

    @Test
    fun `rejects en passant when the adjacent opposing piece is not a pawn`() {
        val state = enPassantState()

        state.select(Position(7, 0))
        state.applyMove(Move(Position(7, 0), Position(7, 1)))
        state.select(Position(1, 5))
        state.applyMove(Move(Position(1, 5), Position(3, 5)))
        state.board.remove(Position(3, 5))
        state.board.add(Piece(Color.BLACK, PieceType.KNIGHT, Position(3, 5)))

        assertInvalid(state, Move(Position(3, 4), Position(2, 5)))
    }

    @Test
    fun `rejects en passant onto an occupied destination`() {
        val state = enPassantState()
        state.board.add(Piece(Color.WHITE, PieceType.KNIGHT, Position(2, 5)))

        state.select(Position(7, 0))
        state.applyMove(Move(Position(7, 0), Position(7, 1)))
        state.select(Position(1, 5))
        state.applyMove(Move(Position(1, 5), Position(3, 5)))

        assertInvalid(state, Move(Position(3, 4), Position(2, 5)))
    }

    @Test
    fun `rejects an en passant opportunity after one reply`() {
        val state = enPassantState()

        state.select(Position(7, 0))
        state.applyMove(Move(Position(7, 0), Position(7, 1)))
        state.select(Position(1, 5))
        state.applyMove(Move(Position(1, 5), Position(3, 5)))
        state.select(Position(7, 1))
        state.applyMove(Move(Position(7, 1), Position(7, 2)))
        state.select(Position(0, 4))
        state.applyMove(Move(Position(0, 4), Position(0, 3)))

        assertInvalid(state, Move(Position(3, 4), Position(2, 5)))
    }

    @Test
    fun `allows knight to jump over pieces`() {
        val state = GameState()

        assertValid(state, Move(Position(7, 1), Position(5, 2)))
    }

    @Test
    fun `rejects blocked rook bishop and queen paths`() {
        val state = GameState()

        assertInvalid(state, Move(Position(7, 0), Position(4, 0)))
        assertInvalid(state, Move(Position(7, 2), Position(4, 5)))
        assertInvalid(state, Move(Position(7, 3), Position(3, 7)))
    }

    @Test
    fun `allows clear rook bishop and queen paths`() {
        val board = Board()
        board.add(Piece(Color.WHITE, PieceType.ROOK, Position(7, 0)))
        board.add(Piece(Color.WHITE, PieceType.BISHOP, Position(7, 2)))
        board.add(Piece(Color.WHITE, PieceType.QUEEN, Position(7, 3)))

        assertValid(board, Color.WHITE, Move(Position(7, 0), Position(4, 0)))
        assertValid(board, Color.WHITE, Move(Position(7, 2), Position(4, 5)))
        assertValid(board, Color.WHITE, Move(Position(7, 3), Position(4, 6)))
    }

    @Test
    fun `allows a king to move one square but not two`() {
        val board = Board()
        board.add(Piece(Color.WHITE, PieceType.KING, Position(4, 4)))

        assertValid(board, Color.WHITE, Move(Position(4, 4), Position(5, 5)))
        assertInvalid(board, Move(Position(4, 4), Position(6, 4)))
    }

    @Test
    fun `rejects wrong turns friendly pieces and zero distance moves`() {
        val state = GameState()

        assertInvalid(state, Move(Position(1, 0), Position(2, 0)))
        assertInvalid(state, Move(Position(6, 0), Position(7, 0)))
        assertInvalid(state, Move(Position(6, 0), Position(6, 0)))
    }

    @Test
    fun `allows captures of opposing pieces`() {
        val board = Board()
        board.add(Piece(Color.WHITE, PieceType.ROOK, Position(7, 0)))
        board.add(Piece(Color.BLACK, PieceType.KNIGHT, Position(4, 0)))

        assertValid(board, Color.WHITE, Move(Position(7, 0), Position(4, 0)))
    }

    private fun assertValid(state: GameState, move: Move) {
        assertEquals(ValidationResult.Valid, validator.validate(state, move))
    }

    private fun assertValid(board: Board, color: Color, move: Move) {
        assertEquals(ValidationResult.Valid, validator.validate(board, color, move))
    }

    private fun assertInvalid(state: GameState, move: Move) {
        check(validator.validate(state, move) is ValidationResult.Invalid) {
            "Expected move $move to be invalid"
        }
    }

    private fun assertInvalid(board: Board, move: Move) {
        check(validator.validate(board, Color.WHITE, move) is ValidationResult.Invalid) {
            "Expected move $move to be invalid"
        }
    }

    private fun enPassantState(): GameState = GameState().also { state ->
        state.board.pieces().forEach { state.board.remove(it.position) }
        state.board.add(Piece(Color.WHITE, PieceType.KING, Position(7, 4)))
        state.board.add(Piece(Color.BLACK, PieceType.KING, Position(0, 4)))
        state.board.add(Piece(Color.WHITE, PieceType.ROOK, Position(7, 0)))
        state.board.add(Piece(Color.WHITE, PieceType.PAWN, Position(3, 4)))
        state.board.add(Piece(Color.BLACK, PieceType.PAWN, Position(1, 5)))
    }
}
