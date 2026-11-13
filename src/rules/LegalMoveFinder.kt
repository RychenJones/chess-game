package rules

import model.Board
import model.Color
import model.GameState
import model.LastMove
import model.Move

/** Filters movement-valid candidates to moves that preserve the moving king's safety. */
class LegalMoveFinder(
    private val moveGenerator: MoveGenerator = MoveGenerator(),
    private val checkDetector: CheckDetector = CheckDetector()
) {
    fun legalMoves(state: GameState): List<Move> =
        legalMoves(state.board, state.currentPlayer, state.lastMove)

    fun legalMoves(board: Board, color: Color, lastMove: LastMove? = null): List<Move> =
        moveGenerator.candidateMoves(board, color, lastMove).filter { move ->
            val simulatedBoard = board.copy()
            EnPassant.capturedPawnPosition(simulatedBoard, color, move, lastMove)
                ?.let(simulatedBoard::remove)
            simulatedBoard.move(move.from, move.to)
            !checkDetector.isInCheck(simulatedBoard, color)
        }
}
