package com.unpostpone.app.presentation.pomodoro.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.NumberDisplayLarge
import com.unpostpone.app.ui.theme.UnpostponeTheme

@Composable
fun PomodoroTimerRing(
    progress: Float,
    centerText: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
) {
    val ringColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val clampedProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = modifier.size(Dimens.FocusRingSize),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeTrack = Dimens.FocusRingTrack.toPx()
            val strokeProgress = Dimens.FocusRingStroke.toPx()
            val inset = (strokeProgress / 2f) + 4f
            val arcSize = Size(
                width = this.size.width - inset * 2,
                height = this.size.height - inset * 2,
            )
            val topLeft = Offset(inset, inset)

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeTrack),
            )

            if (clampedProgress > 0f) {
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = 360f * clampedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(
                        width = strokeProgress,
                        cap = StrokeCap.Round,
                    ),
                )

                val tipRadius = Dimens.FocusRingStroke.value * 0.6f
                val endAngleRad = Math.toRadians((-90f + 360f * clampedProgress).toDouble())
                val centerX = this.size.width / 2f
                val centerY = this.size.height / 2f
                val ringRadius = (this.size.width / 2f) - inset
                val tipX = centerX + (ringRadius * Math.cos(endAngleRad)).toFloat()
                val tipY = centerY + (ringRadius * Math.sin(endAngleRad)).toFloat()
                drawCircle(
                    color = ringColor,
                    radius = tipRadius,
                    center = Offset(tipX, tipY),
                )
            }

            val tickRadius = 4.dp.toPx()
            drawCircle(
                color = ringColor,
                radius = tickRadius,
                center = Offset(this.size.width / 2f, inset),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!eyebrow.isNullOrBlank()) {
                Text(
                    text = eyebrow.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(Dimens.SpacingS))
            }
            Text(
                text = centerText,
                style = NumberDisplayLarge,
                color = onSurface,
                maxLines = 1,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(name = "PomodoroTimerRing — Empty", showBackground = true)
@Composable
private fun PomodoroTimerRingPreview_Empty() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroTimerRing(
            progress = 0f,
            centerText = "25:00",
            eyebrow = "Focus",
        )
    }
}

@Preview(name = "PomodoroTimerRing — Half", showBackground = true)
@Composable
private fun PomodoroTimerRingPreview_Half() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroTimerRing(
            progress = 0.5f,
            centerText = "12:30",
            eyebrow = "Focus",
        )
    }
}

@Preview(name = "PomodoroTimerRing — Done", showBackground = true)
@Composable
private fun PomodoroTimerRingPreview_Done() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroTimerRing(
            progress = 1f,
            centerText = "00:00",
            eyebrow = "Focus",
        )
    }
}
