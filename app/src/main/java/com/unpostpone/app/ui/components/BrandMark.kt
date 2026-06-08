package com.unpostpone.app.ui.components

import androidx.compose.animation.core.Easing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.unpostpone.app.ui.theme.BrandAction
import com.unpostpone.app.ui.theme.BrandCream
import com.unpostpone.app.ui.theme.BrandSage
import com.unpostpone.app.ui.theme.EmphasizedEasing
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// ════════════════════════════════════════════════════════════════════════════
//  Unpostpone — Brand Mark
//  The icon, drawn entirely in Compose Canvas. No image assets, no Lottie —
//  every shape is a drawArc / drawCircle / drawLine / drawPath, so the mark
//  scales to any size and inherits the active theme.
//
//  Animation model:
//    The component takes a single `progress` Float in [0, 1]. Each shape
//    activates in its own slice of that range. Callers drive the master
//    progress with whatever animation API they like (LaunchedEffect, infinite
//    transition, gestures). Default phase map:
//
//      0.00 – 0.40   clock arc draws  (sweep 0° → 270°)
//      0.35 – 0.50   center circle scales 0 → 1
//      0.50 – 0.75   clock hands swing into place
//      0.70 – 0.90   leaf grows from bottom-right
//      0.85 – 1.00   wordmark fades in (handled by parent for layout reasons)
//
//  Three visual modes are supported via the static `mode` parameter:
//    • Splash  — full progress 0..1 animation, brand colors on cream bg
//    • Hero    — fully drawn mark, used on dashboard, blocker, focus screens
//    • Focus   — drawn at a smaller size with a thin progress overlay (the
//                the C/arc becomes a progress ring the user is filling)
// ════════════════════════════════════════════════════════════════════════════

enum class BrandMarkMode { Splash, Hero, Focus }

/**
 * Draw the Unpostpone brand mark.
 *
 * @param progress master animation progress in [0, 1] (used for Splash mode)
 * @param size     outer size; the mark draws inside a square
 * @param mode     visual mode (Splash = animated, Hero = static, Focus = static + slim)
 * @param arcProgress  for Focus mode: 0..1 ring progress to overlay on the arc
 * @param arcColor     color of the clock arc (defaults to BrandCream)
 * @param handColor    color of the clock hands + center (defaults to BrandAction)
 * @param leafColor    color of the leaf (defaults to BrandSage)
 */
@Composable
fun BrandMark(
    modifier: Modifier = Modifier,
    progress: Float = 1f,
    size: Dp = 240.dp,
    mode: BrandMarkMode = BrandMarkMode.Hero,
    arcProgress: Float = 1f,
    arcColor: Color = BrandCream,
    handColor: Color = BrandAction,
    leafColor: Color = BrandSage,
) {
    Box(
        modifier = modifier.size(size),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val canvasSize = min(size.width.toPx(), size.height.toPx())
            val centerX = this.size.width / 2f
            val centerY = this.size.height / 2f
            val radius = canvasSize * 0.34f
            val strokeWidth = radius * 0.18f
            val handWidth = strokeWidth * 0.42f

            // 1 ── Clock arc ─────────────────────────────────────────────
            val arcPhase = when (mode) {
                BrandMarkMode.Splash -> (progress / 0.40f).coerceIn(0f, 1f)
                BrandMarkMode.Hero, BrandMarkMode.Focus -> 1f
            }
            if (arcPhase > 0f) {
                val easeArc = EmphasizedEasing.transform(arcPhase)
                val sweep = 270f * easeArc
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
            val centerPhase = when (mode) {
                BrandMarkMode.Splash -> ((progress - 0.35f) / 0.15f).coerceIn(0f, 1f)
                BrandMarkMode.Hero, BrandMarkMode.Focus -> 1f
            }
            if (centerPhase > 0f) {
                val ease = EmphasizedEasing.transform(centerPhase)
                val r = radius * 0.10f * ease
                drawCircle(
                    color = handColor,
                    radius = r,
                    center = Offset(centerX, centerY),
                )
            }

            // 3 ── Clock hands ───────────────────────────────────────────
            val handPhase = when (mode) {
                BrandMarkMode.Splash -> ((progress - 0.50f) / 0.25f).coerceIn(0f, 1f)
                BrandMarkMode.Hero, BrandMarkMode.Focus -> 1f
            }
            if (handPhase > 0f) {
                val ease = EmphasizedEasing.transform(handPhase)
                // Hands start tucked at the center and swing outward
                val hourAngleDeg = lerp(-90f,   -2f, ease)   // ~12 o'clock
                val minAngleDeg  = lerp( 90f,  -58f, ease)   // ~2 o'clock
                val hourLen = radius * 0.55f
                val minLen  = radius * 0.78f
                drawHand(centerX, centerY, hourLen, hourAngleDeg.toDouble(), handWidth, handColor)
                drawHand(centerX, centerY, minLen,  minAngleDeg.toDouble(),  handWidth, handColor)
            }

            // 4 ── Leaf ──────────────────────────────────────────────────
            val leafPhase = when (mode) {
                BrandMarkMode.Splash -> ((progress - 0.70f) / 0.20f).coerceIn(0f, 1f)
                BrandMarkMode.Hero, BrandMarkMode.Focus -> 1f
            }
            if (leafPhase > 0f) {
                val ease = EmphasizedEasing.transform(leafPhase)
                val scale = lerp(0f, 1f, ease)
                val rotation = lerp(-12f, 0f, ease)
                translate(
                    left = centerX + radius * 0.62f,
                    top  = centerY + radius * 0.58f,
                ) {
                    rotate(degrees = rotation, pivot = Offset.Zero) {
                        drawLeaf(
                            color = leafColor,
                            length = radius * 0.70f * scale,
                        )
                    }
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
        quadraticBezierTo(
            controlX = widthCtrl,
            controlY = 0f,
            endX = tipX,
            endY = tipY,
        )
        quadraticBezierTo(
            controlX = -widthCtrl,
            controlY = 0f,
            endX = baseX,
            endY = baseY,
        )
        close()
    }
    drawPath(path = path, color = color)
}

private fun lerp(start: Float, end: Float, fraction: Float): Float =
    start + (end - start) * fraction
