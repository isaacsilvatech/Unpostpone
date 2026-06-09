package com.unpostpone.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val ExtraSmallRadius = 8.dp
val SmallRadius      = 12.dp
val MediumRadius     = 20.dp
val LargeRadius      = 28.dp
val ExtraLargeRadius = 36.dp
val HeroRadius       = 32.dp

val UnpostponeShapes = Shapes(
    extraSmall = RoundedCornerShape(ExtraSmallRadius),
    small      = RoundedCornerShape(SmallRadius),
    medium     = RoundedCornerShape(MediumRadius),
    large      = RoundedCornerShape(LargeRadius),
    extraLarge = RoundedCornerShape(ExtraLargeRadius),
)
