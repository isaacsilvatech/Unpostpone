package com.unpostpone.app.presentation.dashboard.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unpostpone.app.presentation.dashboard.format.FocusTimeFormatter
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.NumberHeadlineXLarge
import com.unpostpone.app.ui.theme.UnpostponeTheme


@Composable
fun HeroFocusRing(
    focusedMinutes: Int,
    dailyTargetMinutes: Int = 480,
    eyebrow: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val progress = if (dailyTargetMinutes <= 0) 0f
    else (focusedMinutes.toFloat() / dailyTargetMinutes.toFloat())
        .coerceIn(0f, 1f)
    val ringColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier.size(Dimens.FocusRingSize),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeTrack = Dimens.FocusRingTrack.toPx()
            val strokeProgress = Dimens.FocusRingStroke.toPx()
            val inset = (strokeProgress / 2f) + 4f  // keep stroke fully visible
            val arcSize = androidx.compose.ui.geometry.Size(
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

            if (progress > 0f) {
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(
                        width = strokeProgress,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    ),
                )

                val tipRadius = Dimens.FocusRingStroke.value * 0.6f  // ~8.4dp
                val endAngleRad = Math.toRadians((-90f + 360f * progress).toDouble())
                val centerX = this.size.width / 2f
                val centerY = this.size.height / 2f
                val ringRadius = (this.size.width / 2f) - inset
                val tipX = centerX + (ringRadius * kotlin.math.cos(endAngleRad)).toFloat()
                val tipY = centerY + (ringRadius * kotlin.math.sin(endAngleRad)).toFloat()
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
            Text(
                text = eyebrow.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(Dimens.SpacingS))
            Text(
                text = FocusTimeFormatter.formatFocusTime(focusedMinutes),
                style = NumberHeadlineXLarge,
                color = onSurface,
                maxLines = 1,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Dimens.SpacingS))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}


@Preview(name = "HeroFocusRing — Empty (light)", showBackground = true)
@Composable
private fun HeroFocusRingPreview_Empty() {
    UnpostponeTheme(darkTheme = false) {
        HeroFocusRing(
            focusedMinutes = 0,
            eyebrow = "Today",
            subtitle = "0 of 3 goals complete",
        )
    }
}

@Preview(name = "HeroFocusRing — Quarter (light)", showBackground = true)
@Composable
private fun HeroFocusRingPreview_Quarter() {
    UnpostponeTheme(darkTheme = false) {
        HeroFocusRing(
            focusedMinutes = 120,
            eyebrow = "Today",
            subtitle = "1 of 3 goals complete",
        )
    }
}

@Preview(name = "HeroFocusRing — Done (light)", showBackground = true)
@Composable
private fun HeroFocusRingPreview_Done() {
    UnpostponeTheme(darkTheme = false) {
        HeroFocusRing(
            focusedMinutes = 480,
            eyebrow = "Today",
            subtitle = "3 of 3 goals complete",
        )
    }
}

@Preview(name = "HeroFocusRing — Empty (dark)", showBackground = true)
@Composable
private fun HeroFocusRingPreview_Empty_Dark() {
    UnpostponeTheme(darkTheme = true) {
        HeroFocusRing(
            focusedMinutes = 0,
            eyebrow = "Today",
            subtitle = "0 of 3 goals complete",
        )
    }
}

@Preview(name = "HeroFocusRing — Quarter (dark)", showBackground = true)
@Composable
private fun HeroFocusRingPreview_Quarter_Dark() {
    UnpostponeTheme(darkTheme = true) {
        HeroFocusRing(
            focusedMinutes = 120,
            eyebrow = "Today",
            subtitle = "1 of 3 goals complete",
        )
    }
}

@Preview(name = "HeroFocusRing — Done (dark)", showBackground = true)
@Composable
private fun HeroFocusRingPreview_Done_Dark() {
    UnpostponeTheme(darkTheme = true) {
        HeroFocusRing(
            focusedMinutes = 480,
            eyebrow = "Today",
            subtitle = "3 of 3 goals complete",
        )
    }
}
