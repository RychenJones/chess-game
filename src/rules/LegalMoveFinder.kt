package rules

import model.Board
import model.Color
import model.Move

/** Filters movement-valid candidates to moves that preserve the moving king's safety. */
class LegalMoveFinder(
    private val moveGenerator: MoveGenerator = MoveGenerator(),
    private val checkDetector: CheckDetector = CheckDetector()
) {
    fun legalMoves(board: Board, color: Color): List<Move> =
        moveGenerator.candidateMoves(board, color).filter { move ->
            val simulatedBoard = board.copy()
            simulatedBoard.move(move.from, move.to)
            !checkDetector.isInCheck(simulatedBoard, color)
        }
}
