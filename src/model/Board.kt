package model

class Board {
    private val squares = Array(BOARD_SIZE) {
        arrayOfNulls<Piece>(BOARD_SIZE)
    }

    fun add(piece: Piece) {
        require(pieceAt(piece.position) == null) {
            "A piece already occupies position ${piece.position}"
        }

        squares[piece.position.row][piece.position.column] = piece
    }

    fun pieceAt(position: Position): Piece? =
        squares[position.row][position.column]

    fun remove(position: Position): Piece? {
        val piece = pieceAt(position)
        squares[position.row][position.column] = null
        return piece
    }

    /**
     * Moves a piece and returns any piece captured at the destination.
     * Movement rules are intentionally handled outside the board.
     */
    fun move(from: Position, to: Position): Piece? {
        require(from != to) { "A piece cannot move to its current position" }

        val movingPiece = requireNotNull(remove(from)) {
            "No piece occupies position $from"
        }
        val capturedPiece = remove(to)

        movingPiece.position = to
        squares[to.row][to.column] = movingPiece
        return capturedPiece
    }

    fun clear() {
        for (row in squares.indices) {
            for (column in squares[row].indices) {
                squares[row][column] = null
            }
        }
    }

    /**
     * Returns the active pieces without maintaining a second piece collection.
     * The board array remains the single source of truth.
     */
    fun pieces(): List<Piece> =
        squares.asSequence()
            .flatMap { row -> row.asSequence() }
            .filterNotNull()
            .toList()

    private companion object {
        const val BOARD_SIZE = 8
    }
}
