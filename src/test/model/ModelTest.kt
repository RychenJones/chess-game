package model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ModelTest {
    @Test
    fun `accepts positions on the board boundaries`() {
        assertEquals(Position(0, 0), Position(0, 0))
        assertEquals(Position(7, 7), Position(7, 7))
    }

    @Test
    fun `rejects rows outside the board`() {
        assertFailsWith<IllegalArgumentException> { Position(-1, 0) }
        assertFailsWith<IllegalArgumentException> { Position(8, 0) }
    }

    @Test
    fun `rejects columns outside the board`() {
        assertFailsWith<IllegalArgumentException> { Position(0, -1) }
        assertFailsWith<IllegalArgumentException> { Position(0, 8) }
    }
}
