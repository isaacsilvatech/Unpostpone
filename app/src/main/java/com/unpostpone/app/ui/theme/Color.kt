package com.unpostpone.app.ui.theme

import androidx.compose.ui.graphics.Color


val Teal       = Color(0xFF0C4D5B)  // primary — the only saturated color
val TealLight  = Color(0xFF1F6778)  // primary light — softer teal
val TealDark   = Color(0xFF083944)  // primary dark — pressed, on-primary text
val TealHint   = Color(0xFFE8F2F4)  // primary container — whisper of teal
val TealHintDark = Color(0xFF1F4047) // dark-mode primary container

val LightBackground            = Color(0xFFF6F6F7)   // page bg — lifts cards off it
val LightOnBackground          = Color(0xFF0A0A0A)   // primary text
val LightSurface               = Color(0xFFFFFFFF)   // cards
val LightOnSurface             = Color(0xFF0A0A0A)
val LightSurfaceVariant        = Color(0xFFEEEEF0)   // subtle neutral fill
val LightOnSurfaceVariant      = Color(0xFF6B6B6B)   // muted text

val LightSurfaceContainerLowest  = Color(0xFFFFFFFF)
val LightSurfaceContainerLow     = Color(0xFFFAFAFB)
val LightSurfaceContainer        = Color(0xFFF8F8F9)
val LightSurfaceContainerHigh    = Color(0xFFF4F4F5)
val LightSurfaceContainerHighest = Color(0xFFEFEFF0)

val LightOutline               = Color(0xFFBFBFBF)   // visible borders
val LightOutlineVariant        = Color(0xFFE0E0E0)   // hairline dividers

val LightPrimary               = Teal
val LightOnPrimary             = Color(0xFFFFFFFF)
val LightPrimaryContainer      = TealHint              // whisper of teal
val LightOnPrimaryContainer    = Teal

val LightSecondary             = TealLight
val LightOnSecondary           = Color(0xFFFFFFFF)
val LightSecondaryContainer    = TealHint
val LightOnSecondaryContainer  = Teal

val LightTertiary              = Color(0xFF525252)   // neutral mid-gray
val LightOnTertiary            = Color(0xFFFFFFFF)
val LightTertiaryContainer     = Color(0xFFF0F0F0)
val LightOnTertiaryContainer   = Color(0xFF1A1A1A)

val LightError                 = Color(0xFFB91C1C)
val LightOnError               = Color(0xFFFFFFFF)
val LightErrorContainer        = Color(0xFFFEE2E2)
val LightOnErrorContainer      = Color(0xFF7F1D1D)

val LightInverseSurface        = Color(0xFF1A1A1A)
val LightInverseOnSurface      = Color(0xFFF5F5F5)
val LightInversePrimary        = Color(0xFF84D0DC)
val LightScrim                 = Color(0xFF000000)
val LightSurfaceTint           = Teal

val DarkBackground             = Color(0xFF0A0A0A)
val DarkOnBackground           = Color(0xFFF5F5F5)
val DarkSurface                = Color(0xFF1C1C1C)
val DarkOnSurface              = Color(0xFFF5F5F5)
val DarkSurfaceVariant         = Color(0xFF2C2C2C)
val DarkOnSurfaceVariant       = Color(0xFFB0B0B0)

val DarkSurfaceContainerLowest  = Color(0xFF0A0A0A)
val DarkSurfaceContainerLow     = Color(0xFF121212)
val DarkSurfaceContainer        = Color(0xFF1C1C1C)
val DarkSurfaceContainerHigh    = Color(0xFF262626)
val DarkSurfaceContainerHighest = Color(0xFF303030)

val DarkOutline                = Color(0xFF555555)
val DarkOutlineVariant         = Color(0xFF383838)

val DarkPrimary                = Color(0xFF5BA3B0)   // lifted teal for dark
val DarkOnPrimary              = TealDark
val DarkPrimaryContainer       = TealHintDark
val DarkOnPrimaryContainer     = Color(0xFFCFE8EE)

val DarkSecondary              = Color(0xFF84A8AE)
val DarkOnSecondary            = Color(0xFF0A2226)
val DarkSecondaryContainer     = TealHintDark
val DarkOnSecondaryContainer   = Color(0xFFCFE8EE)

val DarkTertiary               = Color(0xFFA3A3A3)
val DarkOnTertiary             = Color(0xFF1A1A1A)
val DarkTertiaryContainer      = Color(0xFF262626)
val DarkOnTertiaryContainer    = Color(0xFFE5E5E5)

val DarkError                  = Color(0xFFFCA5A5)
val DarkOnError                = Color(0xFF450A0A)
val DarkErrorContainer         = Color(0xFF5F1A1A)
val DarkOnErrorContainer       = Color(0xFFFECACA)

val DarkInverseSurface         = Color(0xFFF5F5F5)
val DarkInverseOnSurface       = Color(0xFF0A0A0A)
val DarkInversePrimary         = Teal
val DarkScrim                  = Color(0xFF000000)
val DarkSurfaceTint            = DarkPrimary

val LightHeroSurface = Color(0xFFF1F7F8)  // cool off-white, hint of teal
val DarkHeroSurface  = Color(0xFF13262A)  // cool dark teal-tint
