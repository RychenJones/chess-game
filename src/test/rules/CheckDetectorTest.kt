package rules

import kotlin.test.Test
import kotlin.test.assertEquals
import model.Board
import model.Color
import model.Piece
import model.PieceType
import model.Position

class CheckDetectorTest {
    private val detector = CheckDetector()

    @Test
    fun `detects rook check and ignores blocked rook attacks`() {
        val board = boardOf(
            Piece(Color.WHITE, PieceType.KING, Position(7, 4)),
            Piece(Color.BLACK, PieceType.ROOK, Position(0, 4))
        )

        assertEquals(true, detector.isInCheck(board, Color.WHITE))

        board.add(Piece(Color.WHITE, PieceType.PAWN, Position(4, 4)))

        assertEquals(false, detector.isInCheck(board, Color.WHITE))
    }

    @Test
    fun `detects pawn diagonal attacks but not forward attacks`() {
        val diagonalBoard = boardOf(
            Piece(Color.WHITE, PieceType.KING, Position(4, 4)),
            Piece(Color.BLACK, PieceType.PAWN, Position(3, 3))
        )
        val forwardBoard = boardOf(
            Piece(Color.WHITE, PieceType.KING, Position(4, 4)),
            Piece(Color.BLACK, PieceType.PAWN, Position(3, 4))
        )

        assertEquals(true, detector.isInCheck(diagonalBoard, Color.WHITE))
        assertEquals(false, detector.isInCheck(forwardBoard, Color.WHITE))
    }

    @Test
    fun `detects knight bishop queen and king attacks`() {
        assertEquals(
            true,
            detector.isInCheck(
                boardOf(
                    Piece(Color.WHITE, PieceType.KING, Position(4, 4)),
                    Piece(Color.BLACK, PieceType.KNIGHT, Position(3, 2))
                ),
                Color.WHITE
            )
        )
        assertEquals(
            true,
            detector.isInCheck(
                boardOf(
                    Piece(Color.WHITE, PieceType.KING, Position(4, 4)),
                    Piece(Color.BLACK, PieceType.BISHOP, Position(2, 2))
                ),
                Color.WHITE
            )
        )
        assertEquals(
            true,
            detector.isInCheck(
                boardOf(
                    Piece(Color.WHITE, PieceType.KING, Position(4, 4)),
                    Piece(Color.BLACK, PieceType.QUEEN, Position(0, 0))
                ),
                Color.WHITE
            )
        )
        assertEquals(
            true,
            detector.isInCheck(
                boardOf(
                    Piece(Color.WHITE, PieceType.KING, Position(4, 4)),
                    Piece(Color.BLACK, PieceType.KING, Position(3, 5))
                ),
                Color.WHITE
            )
        )
    }

    private fun boardOf(vararg pieces: Piece): Board =
        Board().also { board -> pieces.forEach(board::add) }
}
