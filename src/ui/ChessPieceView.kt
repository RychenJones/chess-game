package ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as UiColor
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import model.Color
import model.Piece

@Composable
fun ChessPieceView(piece: Piece, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(56.dp)) {
        drawChessPiece(piece)
    }
}

private fun DrawScope.drawChessPiece(piece: Piece) {
    val scale = size.minDimension / 100f
    val fillBrush = when (piece.color) {
        Color.WHITE -> Brush.verticalGradient(
            colors = listOf(UiColor(0xFFFFFFFF), UiColor(0xFFD8D8D8)),
            startY = 8f * scale,
            endY = 92f * scale
        )
        Color.BLACK -> Brush.verticalGradient(
            colors = listOf(UiColor(0xFF4A4A4A), UiColor(0xFF111111)),
            startY = 8f * scale,
            endY = 92f * scale
        )
    }
    val outline = when (piece.color) {
        Color.WHITE -> UiColor(0xFF3A2A1D)
        Color.BLACK -> UiColor(0xFFF0D9B5)
    }

    fun point(x: Float, y: Float) = Offset(x * scale, y * scale)

    fun Path.moveTo(point: Offset) {
        moveTo(point.x, point.y)
    }

    fun Path.lineTo(point: Offset) {
        lineTo(point.x, point.y)
    }

    fun Path.cubicTo(first: Offset, second: Offset, third: Offset) {
        cubicTo(first.x, first.y, second.x, second.y, third.x, third.y)
    }

    fun drawShape(path: Path) {
        drawPath(path, fillBrush)
        drawPath(path, outline, style = Stroke(width = 2f * scale))
    }

    fun shape(block: Path.() -> Unit): Path = Path().apply(block)

    fun drawBase() {
        drawShape(shape {
            moveTo(point(22f, 71f))
            cubicTo(point(20f, 74f), point(20f, 78f), point(23f, 80f))
            lineTo(point(77f, 80f))
            cubicTo(point(80f, 78f), point(80f, 74f), point(78f, 71f))
            close()
        })
        drawShape(shape {
            moveTo(point(19f, 79f))
            cubicTo(point(15f, 80f), point(14f, 85f), point(17f, 90f))
            lineTo(point(83f, 90f))
            cubicTo(point(86f, 85f), point(85f, 80f), point(81f, 79f))
            close()
        })
        drawLine(outline, point(20f, 84f), point(80f, 84f), strokeWidth = 1.25f * scale)
    }

    when (piece.type) {
        model.PieceType.PAWN -> {
            drawCircle(fillBrush, radius = 10f * scale, center = point(50f, 25f))
            drawCircle(outline, radius = 10f * scale, center = point(50f, 25f), style = Stroke(2f * scale))
            drawShape(shape {
                moveTo(point(42f, 36f))
                cubicTo(point(37f, 41f), point(38f, 48f), point(45f, 54f))
                cubicTo(point(45f, 59f), point(41f, 64f), point(35f, 70f))
                cubicTo(point(33f, 72f), point(32f, 74f), point(31f, 76f))
                lineTo(point(69f, 76f))
                cubicTo(point(68f, 74f), point(67f, 72f), point(65f, 70f))
                cubicTo(point(59f, 64f), point(55f, 59f), point(55f, 54f))
                cubicTo(point(62f, 48f), point(63f, 41f), point(58f, 36f))
                close()
            })
            drawLine(outline, point(42f, 37f), point(58f, 37f), strokeWidth = 1.5f * scale)
            drawBase()
        }

        model.PieceType.ROOK -> {
            drawShape(shape {
                moveTo(point(25f, 20f))
                lineTo(point(36f, 20f))
                lineTo(point(36f, 29f))
                lineTo(point(44f, 29f))
                lineTo(point(44f, 20f))
                lineTo(point(56f, 20f))
                lineTo(point(56f, 29f))
                lineTo(point(64f, 29f))
                lineTo(point(64f, 20f))
                lineTo(point(75f, 20f))
                lineTo(point(71f, 39f))
                lineTo(point(66f, 44f))
                lineTo(point(65f, 72f))
                lineTo(point(35f, 72f))
                lineTo(point(34f, 44f))
                lineTo(point(29f, 39f))
                close()
            })
            drawLine(outline, point(40f, 47f), point(40f, 68f), strokeWidth = 1.25f * scale)
            drawLine(outline, point(60f, 47f), point(60f, 68f), strokeWidth = 1.25f * scale)
            drawLine(outline, point(36f, 43f), point(64f, 43f), strokeWidth = 1.25f * scale)
            drawBase()
        }

        model.PieceType.KNIGHT -> {
            drawShape(shape {
                moveTo(point(27f, 78f))
                lineTo(point(34f, 62f))
                lineTo(point(30f, 53f))
                cubicTo(point(37f, 51f), point(39f, 45f), point(35f, 38f))
                cubicTo(point(42f, 29f), point(55f, 27f), point(63f, 35f))
                cubicTo(point(70f, 42f), point(71f, 52f), point(67f, 59f))
                lineTo(point(58f, 55f))
                cubicTo(point(55f, 61f), point(59f, 69f), point(69f, 78f))
                close()
            })
            drawLine(outline, point(41f, 34f), point(48f, 47f), strokeWidth = 1.5f * scale)
            drawLine(outline, point(48f, 47f), point(60f, 42f), strokeWidth = 1.5f * scale)
            drawCircle(outline, radius = 1.5f * scale, center = point(55f, 37f))
            drawLine(outline, point(42f, 31f), point(36f, 39f), strokeWidth = 1.25f * scale)
            drawBase()
        }

        model.PieceType.BISHOP -> {
            drawShape(shape {
                moveTo(point(50f, 13f))
                cubicTo(point(42f, 23f), point(42f, 31f), point(49f, 38f))
                lineTo(point(41f, 46f))
                lineTo(point(59f, 46f))
                lineTo(point(51f, 38f))
                cubicTo(point(58f, 31f), point(58f, 23f), point(50f, 13f))
                close()
            })
            drawShape(shape {
                moveTo(point(42f, 44f))
                lineTo(point(58f, 44f))
                lineTo(point(64f, 70f))
                lineTo(point(70f, 73f))
                lineTo(point(30f, 73f))
                lineTo(point(36f, 70f))
                close()
            })
            drawLine(outline, point(44f, 33f), point(56f, 21f), strokeWidth = 2.5f * scale)
            drawLine(outline, point(40f, 47f), point(60f, 47f), strokeWidth = 1.25f * scale)
            drawBase()
        }

        model.PieceType.QUEEN -> {
            listOf(25f, 42f, 50f, 58f, 75f).forEach { x ->
                drawCircle(fillBrush, radius = 3.5f * scale, center = point(x, if (x == 50f) 35f else 20f))
                drawCircle(outline, radius = 3.5f * scale, center = point(x, if (x == 50f) 35f else 20f), style = Stroke(1.5f * scale))
            }
            drawShape(shape {
                moveTo(point(25f, 20f))
                lineTo(point(35f, 35f))
                lineTo(point(42f, 18f))
                lineTo(point(50f, 35f))
                lineTo(point(58f, 18f))
                lineTo(point(65f, 35f))
                lineTo(point(75f, 20f))
                lineTo(point(68f, 71f))
                lineTo(point(32f, 71f))
                close()
            })
            drawLine(outline, point(32f, 53f), point(68f, 53f), strokeWidth = 2f * scale)
            drawLine(outline, point(36f, 62f), point(64f, 62f), strokeWidth = 1.25f * scale)
            drawBase()
        }

        model.PieceType.KING -> {
            drawShape(shape {
                moveTo(point(46f, 10f))
                lineTo(point(54f, 10f))
                lineTo(point(54f, 19f))
                lineTo(point(63f, 19f))
                lineTo(point(63f, 27f))
                lineTo(point(54f, 27f))
                lineTo(point(54f, 35f))
                lineTo(point(46f, 35f))
                lineTo(point(46f, 27f))
                lineTo(point(37f, 27f))
                lineTo(point(37f, 19f))
                lineTo(point(46f, 19f))
                close()
            })
            drawShape(shape {
                moveTo(point(40f, 37f))
                lineTo(point(60f, 37f))
                lineTo(point(64f, 70f))
                lineTo(point(70f, 73f))
                lineTo(point(30f, 73f))
                lineTo(point(36f, 70f))
                close()
            })
            drawBase()
        }
    }
}
