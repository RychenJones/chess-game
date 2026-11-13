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
     * Moves a piece and returns the captured piece.
     * Movement rules are intentionally handled outside the board.
     * [capturedPosition] supports captures, such as en passant, where the
     * captured piece is not on the destination square.
     */
    fun move(from: Position, to: Position, capturedPosition: Position? = null): Piece? {
        require(from != to) { "A piece cannot move to its current position" }
        require(capturedPosition != to) {
            "A separately captured piece cannot occupy the destination square"
        }

        val movingPiece = requireNotNull(remove(from)) {
            "No piece occupies position ${from.toAlgebraic()}"
        }
        val capturedPiece = if (capturedPosition == null) remove(to) else remove(capturedPosition)
        if (capturedPosition != null) {
            require(pieceAt(to) == null) {
                "A separately captured move must have an empty destination"
            }
        }

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

    fun restoreFrom(source: Board) {
        clear()
        source.pieces().forEach { piece ->
            add(Piece(piece.color, piece.type, piece.position))
        }
    }

    /**
     * Returns the active pieces without maintaining a second piece collection.
     * The position map remains the single source of truth.
     */
    fun pieces(): List<Piece> =
        piecesByPosition.values.toList()
}
