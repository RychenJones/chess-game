package model

class Board {
    private val piecesByPosition = androidx.compose.runtime.mutableStateMapOf<Position, Piece>()

    fun add(piece: Piece) {
        require(pieceAt(piece.position) == null) {
            "A piece already occupies position ${piece.position.toAlgebraic()}"
        }

        piecesByPosition[piece.position] = piece
    }

    fun pieceAt(position: Position): Piece? =
        piecesByPosition[position]

    fun remove(position: Position): Piece? {
        val piece = pieceAt(position)
        piecesByPosition.remove(position)
        return piece
    }

    /**
     * Moves a piece and returns any piece captured at the destination.
     * Movement rules are intentionally handled outside the board.
     */
    fun move(from: Position, to: Position): Piece? {
        require(from != to) { "A piece cannot move to its current position" }

        val movingPiece = requireNotNull(remove(from)) {
            "No piece occupies position ${from.toAlgebraic()}"
        }
        val capturedPiece = remove(to)

        movingPiece.position = to
        piecesByPosition[to] = movingPiece
        return capturedPiece
    }

    fun clear() {
        piecesByPosition.clear()
    }

    /** Returns an independent board copy for safe move simulation. */
    fun copy(): Board = Board().also { copiedBoard ->
        pieces().forEach { piece ->
            copiedBoard.add(Piece(piece.color, piece.type, piece.position))
        }
    }

    /**
     * Returns the active pieces without maintaining a second piece collection.
     * The position map remains the single source of truth.
     */
    fun pieces(): List<Piece> =
        piecesByPosition.values.toList()
}
