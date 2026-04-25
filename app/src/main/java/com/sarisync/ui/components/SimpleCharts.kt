package com.sarisync.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

/**
 * A simple bar chart drawn with Compose Canvas.
 *
 * @param labels  X-axis labels (e.g., dates like "04/12")
 * @param values  Corresponding numeric values for each bar
 * @param barColor  Fill colour of the bars
 * @param modifier  Layout modifier
 */
@Composable
fun SimpleBarChart(
    labels: List<String>,
    values: List<Double>,
    barColor: Color,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
) {
    if (values.isEmpty()) return

    val maxValue = values.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0

    Canvas(modifier = modifier) {
        val chartWidth = size.width
        val chartHeight = size.height
        val bottomPadding = 40f
        val topPadding = 16f
        val drawableHeight = chartHeight - bottomPadding - topPadding
        val barCount = values.size
        val totalSpacing = chartWidth * 0.2f
        val spacing = if (barCount > 1) totalSpacing / barCount else 8f
        val barWidth = (chartWidth - totalSpacing) / barCount

        values.forEachIndexed { index, value ->
            val barHeight = (value / maxValue * drawableHeight).toFloat()
            val x = index * (barWidth + spacing) + spacing / 2

            // Bar
            drawRect(
                color = barColor,
                topLeft = Offset(x, topPadding + drawableHeight - barHeight),
                size = Size(barWidth, barHeight)
            )

            // Label
            drawContext.canvas.nativeCanvas.drawText(
                labels.getOrElse(index) { "" },
                x + barWidth / 2,
                chartHeight - 8f,
                android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 24f
                    color = android.graphics.Color.GRAY
                }
            )
        }
    }
}

/**
 * A simple dual-line chart drawn with Compose Canvas.
 * Shows two data series (e.g., Revenue vs Cost) over time.
 *
 * @param labels      X-axis labels
 * @param series1     First data series values
 * @param series2     Second data series values
 * @param color1      Colour for series 1
 * @param color2      Colour for series 2
 * @param modifier    Layout modifier
 */
@Composable
fun SimpleDualLineChart(
    labels: List<String>,
    series1: List<Double>,
    series2: List<Double>,
    color1: Color,
    color2: Color,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
) {
    if (series1.isEmpty()) return

    val allValues = series1 + series2
    val maxValue = allValues.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0

    Canvas(modifier = modifier) {
        val chartWidth = size.width
        val chartHeight = size.height
        val bottomPadding = 40f
        val topPadding = 16f
        val leftPadding = 8f
        val rightPadding = 8f
        val drawableWidth = chartWidth - leftPadding - rightPadding
        val drawableHeight = chartHeight - bottomPadding - topPadding
        val pointCount = series1.size

        // Draw grid lines (3 horizontal)
        for (i in 0..3) {
            val y = topPadding + drawableHeight * (1f - i / 3f)
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(leftPadding, y),
                end = Offset(chartWidth - rightPadding, y),
                strokeWidth = 1f
            )
        }

        fun getX(index: Int): Float {
            return if (pointCount <= 1) chartWidth / 2
            else leftPadding + index * drawableWidth / (pointCount - 1)
        }

        fun getY(value: Double): Float {
            return topPadding + drawableHeight - (value / maxValue * drawableHeight).toFloat()
        }

        // Draw series 1 (line + dots)
        drawSeriesLine(series1, color1, ::getX, ::getY)

        // Draw series 2 (line + dots)
        drawSeriesLine(series2, color2, ::getX, ::getY)

        // Labels
        labels.forEachIndexed { index, label ->
            drawContext.canvas.nativeCanvas.drawText(
                label,
                getX(index),
                chartHeight - 8f,
                android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 22f
                    color = android.graphics.Color.GRAY
                }
            )
        }
    }
}

private fun DrawScope.drawSeriesLine(
    values: List<Double>,
    color: Color,
    getX: (Int) -> Float,
    getY: (Double) -> Float
) {
    if (values.size < 2) {
        // Single point — just draw a dot
        if (values.isNotEmpty()) {
            drawCircle(color, radius = 6f, center = Offset(getX(0), getY(values[0])))
        }
        return
    }

    val path = Path()
    path.moveTo(getX(0), getY(values[0]))
    for (i in 1 until values.size) {
        path.lineTo(getX(i), getY(values[i]))
    }
    drawPath(path, color, style = Stroke(width = 4f, cap = StrokeCap.Round))

    // Dots
    values.forEachIndexed { index, value ->
        drawCircle(color, radius = 5f, center = Offset(getX(index), getY(value)))
    }
}
