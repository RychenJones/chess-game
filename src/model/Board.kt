package model

class Board {
    private val pieces = mutableListOf<Piece>()

    fun add(piece: Piece) {
        require(pieceAt(piece.position) == null) {
            "A piece already occupies position ${piece.position}"
        }

        pieces.add(piece)
    }

    fun pieceAt(position: Position): Piece? =
        pieces.firstOrNull { it.position == position }
}
