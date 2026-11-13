package model

data class Move(
    val from: Position,
    val to: Position
)

data class LastMove(
    val from: Position,
    val to: Position,
    val pieceColor: Color,
    val pieceType: PieceType
) {
    fun isTwoSquarePawnAdvance(): Boolean {
        if (pieceType != PieceType.PAWN || from.column != to.column) {
            return false
        }

        val direction = if (pieceColor == Color.WHITE) -1 else 1
        val startingRow = if (pieceColor == Color.WHITE) 6 else 1
        return from.row == startingRow && to.row - from.row == 2 * direction
    }
}
