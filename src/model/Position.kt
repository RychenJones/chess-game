package model

data class Position(
    val row: Int,
    val column: Int
) {
    init {
        require(row in 0..7) { "Row must be between 0 and 7: $row" }
        require(column in 0..7) { "Column must be between 0 and 7: $column" }
    }

    /** Returns standard chess notation, such as a1 or e4. */
    fun toAlgebraic(): String {
        val file = ('a'.code + column).toChar()
        val rank = 8 - row
        return "$file$rank"
    }
}
