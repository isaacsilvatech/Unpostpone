package com.unpostpone.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.unpostpone.app.ui.theme.Teal
import com.unpostpone.app.ui.theme.TealLight
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// ════════════════════════════════════════════════════════════════════════════
//  Unpostpone — Brand Mark
//  The icon, drawn entirely in Compose Canvas. No image assets, no Lottie —
//  every shape is a drawArc / drawCircle / drawLine / drawPath, so the mark
//  scales to any size and inherits the active theme.
//
//  Two visual modes are supported via the static `mode` parameter:
//    • Hero    — fully drawn mark, used on dashboard, blocker, focus screens
//    • Focus   — drawn at a smaller size with a thin progress overlay (the
//                the C/arc becomes a progress ring the user is filling)
// ════════════════════════════════════════════════════════════════════════════

enum class BrandMarkMode { Hero, Focus }

/**
 * Draw the Unpostpone brand mark.
 *
 * @param size     outer size; the mark draws inside a square
 * @param mode     visual mode (Hero = static, Focus = static + slim)
 * @param arcProgress  for Focus mode: 0..1 ring progress to overlay on the arc
 * @param arcColor     color of the clock arc (defaults to TealLight)
 * @param handColor    color of the clock hands + center (defaults to Teal)
 * @param leafColor    color of the leaf — caller-provided, no default
 */
@Composable
fun BrandMark(
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    mode: BrandMarkMode = BrandMarkMode.Hero,
    arcProgress: Float = 1f,
    arcColor: Color = TealLight,
    handColor: Color = Teal,
    leafColor: Color,                       // caller must pass
) {
    Box(
        modifier = modifier.size(size),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val canvasSize = size.toPx()
            val centerX = this.size.width / 2f
            val centerY = this.size.height / 2f
            val radius = canvasSize * 0.34f
            val strokeWidth = radius * 0.18f
            val handWidth = strokeWidth * 0.42f

            // 1 ── Clock arc ─────────────────────────────────────────────
            if (mode == BrandMarkMode.Hero || mode == BrandMarkMode.Focus) {
                val sweep = 270f
                // Focus mode: dim the unfilled track with the same color at 25% alpha
                val trackColor = if (mode == BrandMarkMode.Focus) arcColor.copy(alpha = 0.25f) else arcColor
                // Draw the track first (full arc) so it shows through in Focus mode
                if (mode == BrandMarkMode.Focus) {
                    drawArc(
                        color = trackColor,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = Offset(centerX - radius, centerY - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                }
                // Draw the filled portion
                drawArc(
                    color = arcColor,
                    startAngle = 135f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }

            // 2 ── Center circle ─────────────────────────────────────────
            if (mode == BrandMarkMode.Hero || mode == BrandMarkMode.Focus) {
                val r = radius * 0.10f
                drawCircle(
                    color = handColor,
                    radius = r,
                    center = Offset(centerX, centerY),
                )
            }

            // 3 ── Clock hands ───────────────────────────────────────────
            if (mode == BrandMarkMode.Hero || mode == BrandMarkMode.Focus) {
                // Hands at their final positions; Hero mode shows them statically.
                val hourAngleDeg = -2f     // ~12 o'clock
                val minAngleDeg  = -58f    // ~2 o'clock
                val hourLen = radius * 0.55f
                val minLen  = radius * 0.78f
                drawHand(centerX, centerY, hourLen, hourAngleDeg.toDouble(), handWidth, handColor)
                drawHand(centerX, centerY, minLen,  minAngleDeg.toDouble(),  handWidth, handColor)
            }

            // 4 ── Leaf ──────────────────────────────────────────────────
            if (mode == BrandMarkMode.Hero || mode == BrandMarkMode.Focus) {
                translate(
                    left = centerX + radius * 0.62f,
                    top  = centerY + radius * 0.58f,
                ) {
                    drawLeaf(
                        color = leafColor,
                        length = radius * 0.70f,
                    )
                }
            }
        }
    }
}

// ── Drawing helpers ──────────────────────────────────────────────────────

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHand(
    cx: Float,
    cy: Float,
    length: Float,
    angleDeg: Double,
    width: Float,
    color: Color,
) {
    val rad = Math.toRadians(angleDeg)
    val dx = (cos(rad) * length).toFloat()
    val dy = (sin(rad) * length).toFloat()
    drawLine(
        color = color,
        start = Offset(cx, cy),
        end   = Offset(cx + dx, cy + dy),
        strokeWidth = width,
        cap = StrokeCap.Round,
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLeaf(
    color: Color,
    length: Float,
) {
    // A simple leaf = two quadratic curves meeting at a tip and a base
    val tipX = 0f
    val tipY = -length * 0.5f
    val baseX = 0f
    val baseY = length * 0.5f
    val widthCtrl = length * 0.45f
    val path = Path().apply {
        moveTo(baseX, baseY)
        quadraticTo(
            widthCtrl,
            0f,
            tipX,
            tipY,
        )
        quadraticTo(
            -widthCtrl,
            0f,
            baseX,
            baseY,
        )
        close()
    }
    drawPath(path = path, color = color)
}
