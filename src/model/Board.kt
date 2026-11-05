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
