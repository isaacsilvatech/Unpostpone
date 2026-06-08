# Unpostpone — Design System

The visual identity, derived from the brand mark in `docs/icon.svg`. Every
screen in the app should follow these rules. When in doubt, the icon is the
final authority — look at it before designing anything new.

## 1. Brand analysis

The mark is built from four shapes and four colors. Each one carries a
specific emotional and functional role.

### Shapes

| Shape | Role in the icon | Role in the app |
|---|---|---|
| **Squircle background** (rounded square with continuous curvature) | Container for everything else; the canvas | The signature surface. Every card, dialog, bottom sheet uses the same generous corner radius (20dp medium, 28dp large). |
| **Open C / clock arc** (a circle that doesn't close) | The negative space at the bottom-right is intentional — the C is *incomplete*, suggesting potential | The progress metaphor. The progress ring on the dashboard is a C, not a full circle: time that is being *freed* from distraction. The "open" feeling is the product. |
| **Clock hands** (orange, slightly informal) | Time as a kinetic, human, slightly playful element — not a rigid alarm clock | The action moment. The "Start a focus session" button. The kinetic spark in an otherwise calm interface. |
| **Leaf** (sage green, growing through the C's opening) | What emerges from the open C — growth, life, organic | Success, streaks, goal completion. Always a small gesture, never a full-bleed color block. |

### Colors

| Hex | Role | Used for |
|---|---|---|
| `#0F4C5C` | Primary teal | Brand identity, focus, "you are here" indicators. Never as a background fill at large scale. |
| `#F7F4EA` | Calm ivory | The background of the app. The cream is the secret weapon — it makes the app feel like paper, not plastic. |
| `#F4A261` | Action orange | Primary CTA only. The "Start" button. The "Block" confirm. Kinetic moments. |
| `#84A98C` | Sage | Success states, streaks, the leaf. Always small. |
| `#2D6A73` | Teal soft | UI chrome, dividers, secondary surfaces. |

### Proportions and visual balance

- The C/arc takes ~60% of the icon's width — the negative space (40%) is
  the point. Don't fill it.
- The leaf is smaller than the clock, and tucked into the corner.
  Reinforces: restraint. Growth doesn't need to shout.
- The orange hands are mid-length — not extended to the edge of the C.
  The C is the stage; the hands perform inside it.

### Emotional impact

Calm, growth-oriented, premium, human. Specifically not:
- Military (no strict angles, no red, no badges).
- Gamified (no XP bars, no confetti, no level-up notifications).
- Corporate (no grey-on-grey, no all-caps labels, no stock-photo feel).
- Punitive (no red "BLOCKED" screens, no alarm-clock alarm).

## 2. Color system

The full Material 3 tonal palette is built from the four brand anchors.
Tones are chosen by hand to produce a calm, premium, low-arousal feel —
not a pure HCT derivative.

### Light theme

| M3 Role | Hex | Kotlin | Use |
|---|---|---|---|
| primary | `#0F4C5C` | `LightPrimary` | FAB, focused tab, link text |
| onPrimary | `#FFFFFF` | `LightOnPrimary` | Text/icons on primary |
| primaryContainer | `#CDE6EC` | `LightPrimaryContainer` | Selected chips, badges |
| onPrimaryContainer | `#001F26` | `LightOnPrimaryContainer` | Text on primaryContainer |
| secondary | `#2D6A73` | `LightSecondary` | Less-assertive UI chrome |
| onSecondary | `#FFFFFF` | `LightOnSecondary` | Text on secondary |
| secondaryContainer | `#CFE6EA` | `LightSecondaryContainer` | Subtle backgrounds |
| onSecondaryContainer | `#041F23` | `LightOnSecondaryContainer` | Text on secondaryContainer |
| tertiary | `#8C4F1F` | `LightTertiary` | Reserved for "action" / brand orange moments |
| onTertiary | `#FFFFFF` | `LightOnTertiary` | Text on tertiary |
| tertiaryContainer | `#FFDCC2` | `LightTertiaryContainer` | Orange tinted backgrounds |
| onTertiaryContainer | `#2E1500` | `LightOnTertiaryContainer` | Text on tertiaryContainer |
| error | `#A8324B` | `LightError` | Desaturated coral — never aggressive red |
| onError | `#FFFFFF` | `LightOnError` | Text on error |
| errorContainer | `#FFDAD9` | `LightErrorContainer` | Error backgrounds |
| onErrorContainer | `#40000F` | `LightOnErrorContainer` | Text on errorContainer |
| background | `#F7F4EA` | `LightBackground` | The cream. The screen. |
| onBackground | `#1A1C1B` | `LightOnBackground` | Body text on background |
| surface | `#FBFAF5` | `LightSurface` | Slightly lifted from background |
| onSurface | `#1A1C1B` | `LightOnSurface` | Text on surface |
| surfaceVariant | `#DCE5E3` | `LightSurfaceVariant` | Subtle dividers, disabled |
| onSurfaceVariant | `#3F4948` | `LightOnSurfaceVariant` | Secondary text |
| outline | `#6F7978` | `LightOutline` | Borders, focus rings |
| outlineVariant | `#BEC9C7` | `LightOutlineVariant` | Hairline dividers |
| scrim | `#000000` | `LightScrim` | Modal backdrops |
| inverseSurface | `#2D3130` | `LightInverseSurface` | Snackbars |
| inverseOnSurface | `#EFF1EE` | `LightInverseOnSurface` | Text on inverseSurface |
| inversePrimary | `#84D0DC` | `LightInversePrimary` | Highlight on inverseSurface |
| surfaceTint | `#0F4C5C` | `LightSurfaceTint` | M3 elevation tint |

### Dark theme

The dark theme is not an inversion. It is intentional:

- The brand teal moves to **luminous sky-teal** (`#84D0DC`) — it glows
  on dark surfaces instead of disappearing into them.
- Containers use **deep charcoal-teal** (`#004E5A`, `#324B50`) — they
  look like the same teal, just at 30% lightness.
- The background is **near-black with a teal undertone** (`#0F1413`),
  not pure black, so the brand is felt even at rest.
- The action orange warms up to `#FFB68C` — softer, more like a
  candle than a spark.

| M3 Role | Hex | Kotlin |
|---|---|---|
| primary | `#84D0DC` | `DarkPrimary` |
| onPrimary | `#00363F` | `DarkOnPrimary` |
| primaryContainer | `#004E5A` | `DarkPrimaryContainer` |
| onPrimaryContainer | `#CDE6EC` | `DarkOnPrimaryContainer` |
| secondary | `#B3CACE` | `DarkSecondary` |
| onSecondary | `#1B353A` | `DarkOnSecondary` |
| secondaryContainer | `#324B50` | `DarkSecondaryContainer` |
| onSecondaryContainer | `#CFE6EA` | `DarkOnSecondaryContainer` |
| tertiary | `#FFB68C` | `DarkTertiary` |
| onTertiary | `#4F2500` | `DarkOnTertiary` |
| tertiaryContainer | `#6F3A14` | `DarkTertiaryContainer` |
| onTertiaryContainer | `#FFDCC2` | `DarkOnTertiaryContainer` |
| error | `#FFB3B0` | `DarkError` |
| onError | `#5F0A19` | `DarkOnError` |
| errorContainer | `#7E1F2C` | `DarkErrorContainer` |
| onErrorContainer | `#FFDAD9` | `DarkOnErrorContainer` |
| background | `#0F1413` | `DarkBackground` |
| onBackground | `#E1E3E0` | `DarkOnBackground` |
| surface | `#0F1413` | `DarkSurface` |
| onSurface | `#E1E3E0` | `DarkOnSurface` |
| surfaceVariant | `#3F4948` | `DarkSurfaceVariant` |
| onSurfaceVariant | `#BEC9C7` | `DarkOnSurfaceVariant` |
| outline | `#889392` | `DarkOutline` |
| outlineVariant | `#3F4948` | `DarkOutlineVariant` |
| scrim | `#000000` | `DarkScrim` |
| inverseSurface | `#E1E3E0` | `DarkInverseSurface` |
| inverseOnSurface | `#2D3130` | `DarkInverseOnSurface` |
| inversePrimary | `#0F4C5C` | `DarkInversePrimary` |
| surfaceTint | `#84D0DC` | `DarkSurfaceTint` |

### Semantic extension colors

M3 has only `error` as a semantic role. Unpostpone adds three more,
exposed through `LocalSemanticColors` / `UnpostponeTheme.semantic`:

| Role | Light | Dark | Use |
|---|---|---|---|
| success | `#4A6B45` | `#9BC09A` | Goal completed, streak milestone |
| onSuccess | `#FFFFFF` | `#0E2607` | Text on success |
| successContainer | `#D0E5C8` | `#325032` | Subtle success background |
| onSuccessContainer | `#0E2607` | `#D0E5C8` | Text on successContainer |
| warning | `#8C5A1A` | `#FFB958` | Gentle nudge (not red) |
| onWarning | `#FFFFFF` | `#422C00` | Text on warning |
| warningContainer | `#FFDDB1` | `#5E411B` | Warning background |
| onWarningContainer | `#2D1700` | `#FFDDB1` | Text on warningContainer |
| info | `#2C5F70` | `#9ECDE0` | Neutral system message |
| onInfo | `#FFFFFF` | `#00363F` | Text on info |
| infoContainer | `#C2E5F0` | `#0E4856` | Info background |
| onInfoContainer | `#001F26` | `#C2E5F0` | Text on infoContainer |

```kotlin
val colors = UnpostponeTheme.semantic
Text("Meta concluída", color = colors.onSuccessContainer)
Surface(color = colors.successContainer, shape = MaterialTheme.shapes.medium) { ... }
```

## 3. Typography

Two families, chosen for distinct roles:

### Manrope (UI family)
A geometric grotesque with soft humanist terminals. Used by Linear, Notion
Calendar, Loom, Vercel. Slightly rounded, highly legible, feels modern
without being trendy. **The default for everything non-numeric.**

### JetBrains Mono (numeric family)
A monospaced typeface designed for code editors. Built-in tabular figures
keep focus time, streak counts, and statistics perfectly aligned in lists
and tables. The technical-but-warm feel matches the product personality
better than the more common Roboto Mono. **Use only for numbers.**

### Type scale

The full M3 15-step scale, tuned for the brand:

| Role | Family | Weight | Size / lh | Use |
|---|---|---|---|---|
| displayLarge | Manrope | ExtraBold | 57/64 | Splash headline (one time) |
| displayMedium | Manrope | Bold | 45/52 | Hero stats (e.g. weekly total) |
| displaySmall | Manrope | Bold | 36/44 | Section hero |
| headlineLarge | Manrope | Bold | 32/40 | Screen titles (rare) |
| headlineMedium | Manrope | Bold | 28/36 | Dashboard title |
| headlineSmall | Manrope | SemiBold | 24/32 | Card titles |
| titleLarge | Manrope | SemiBold | 22/28 | List-item primary |
| titleMedium | Manrope | SemiBold | 16/24 | List-item, dialog title |
| titleSmall | Manrope | SemiBold | 14/20 | Inline heading |
| bodyLarge | Manrope | Normal | 16/24 | Reading content |
| bodyMedium | Manrope | Normal | 14/20 | Default body |
| bodySmall | Manrope | Normal | 12/16 | Caption, helper |
| labelLarge | Manrope | SemiBold | 14/20 | Button text |
| labelMedium | Manrope | Medium | 12/16 | Tab text |
| labelSmall | Manrope | Medium | 11/16 | Overline |

### Number scale (JetBrains Mono, all with tabular figures)

| Style | Weight | Size / lh | Use |
|---|---|---|---|
| NumberDisplayLarge | Bold | 57/64 | The dashboard's "1h 24m" hero |
| NumberDisplayMedium | SemiBold | 45/52 | Weekly total |
| NumberDisplaySmall | Medium | 36/44 | Single big stat |
| NumberHeadline | SemiBold | 24/32 | Card-leading stat |
| NumberTitle | Medium | 16/24 | Inline stat |
| NumberBody | Normal | 14/20 | Body inline stat |

### Why Manrope + JetBrains Mono

- **Manrope** has wide language coverage (Latin, Cyrillic, Vietnamese),
  matches Android's modern aesthetic without copying it, and reads as
  "premium" at both 12sp and 48sp.
- **JetBrains Mono** is the most legible monospace for UI numbers, has
  gorgeous tabular figures, and signals "this is data, not prose" — a
  useful distinction in a productivity app.
- Both are **free** and loaded via **Google Fonts** (downloadable, not
  bundled) — the APK stays lean.

## 4. Shape system

The squircle background of the icon is the design DNA. Generous corners
everywhere. We push past M3 defaults:

| M3 slot | Default | Unpostpone | Used for |
|---|---|---|---|
| extraSmall | 4dp | **8dp** | Chips, badges, small inline |
| small | 8dp | **12dp** | Text fields, segmented controls |
| medium | 12dp | **20dp** | **Cards, dialogs — the signature shape** |
| large | 16dp | **28dp** | Bottom sheets, large cards |
| extraLarge | 28dp | **36dp** | Onboarding illustrations |

Rule of thumb: when uncertain, use `medium` (20dp). It is the most
recognizable shape in the app and always correct.

## 5. Motion

Three laws:

1. **Never bounce.** No overshoot, no spring-back on UI elements.
2. **Never flashy.** No confetti, no glow, no rotation, no scale > 1.05.
3. **Always meaningful.** Animate to communicate a state change.

### Easing

| Token | Cubic-bezier | Use |
|---|---|---|
| `EmphasizedEasing` | `(0.2, 0, 0, 1)` | **Default** for everything. M3 signature. |
| `StandardEasing` | `(0.4, 0, 0.2, 1)` | Crossfades, color tweens |
| `DecelerateEasing` | `(0, 0, 0.2, 1)` | Elements entering |
| `AccelerateEasing` | `(0.4, 0, 1, 1)` | Elements leaving |

### Durations

| Constant | Value | Use |
|---|---|---|
| `Fast` | 150ms | Ripple, hover, color crossfade |
| `Medium` | 250ms | Default state change |
| `Slow` | 400ms | Hero elements: focus ring, blocker |
| `PageIn` | 300ms | Entering a new screen |
| `PageOut` | 200ms | Leaving a screen (faster than entering) |
| `GoalPulse` | 600ms | The one place motion breathes — goal completion |

### Specific animations

- **Page transitions**: 300ms enter, 200ms exit. Slide 40dp from the
  right with a fade. Mirror on back.
- **Card appearance**: fade-in + 8dp upward slide, 250ms, emphasized.
  No scale change.
- **Progress updates**: the focus ring fills with `tween(400,
  EmphasizedEasing)`. Never linear. The eye reads easing as effort.
- **Goal completion**: success badge scales 1.0 → 1.05 → 1.0 over
  600ms with `GentleSpring`. No confetti. The number itself can fade
  to a soft sage tint.
- **Blocker screen entrance**: slide up from the bottom 100% screen
  height, 400ms, decelerate. The blocker is the moment of maximum
  design impact — it gets the longest motion.

## 6. Component guidelines

### Buttons

- **Primary**: filled, `primary` background, `onPrimary` text,
  `ButtonHeight` (52dp), `MaterialTheme.shapes.full` (pill — 26dp
  radius). Used for the main action per screen.
- **Secondary**: outlined, `outline` border, `primary` text. Same
  height. Use when paired with a primary button.
- **Tertiary / Text**: no border, `primary` text, transparent
  background. For "Cancel" / "Maybe later".
- **Action** (kinetic): the orange CTA. Use **tertiaryContainer**
  background with `onTertiaryContainer` text, full pill shape. This
  is the "Start focus session" button. Use sparingly — once per screen
  max.
- All buttons: 14sp label, `labelLarge` (Manrope SemiBold),
  letter-spacing 0.1sp.
- Disabled: `surfaceVariant` background, `onSurfaceVariant` text at
  38% alpha.

### Cards

- Background: `surface` (light) or `surfaceContainerLow` (M3 elevation
  tier).
- Shape: `medium` (20dp). Always.
- Padding: `Dimens.CardPadding` (16dp). For hero cards:
  `Dimens.CardPaddingLarge` (24dp).
- Elevation: M3 tier 1 (subtle drop shadow). No hard borders.
- Active state: elevation tier 2 + scale 1.0 → 1.02 over 150ms.
- Touch target: 48dp minimum (the entire card is tappable when
  relevant).

### Dialogs

- Background: `surfaceContainerHigh` (one tier above cards).
- Shape: `extraLarge` (28dp). Dialogs feel more important than cards.
- Padding: 24dp.
- Title: `headlineSmall` (Manrope SemiBold 24/32).
- Body: `bodyMedium`.
- Actions row: right-aligned, primary action last, with
  `SpacingL` (16dp) gap between.

### Navigation

#### Bottom navigation
- 4 destinations: **Dashboard**, **Goals**, **Statistics**, **Settings**.
- Height: 80dp (Material 3 default).
- Background: `surfaceContainer` (one tier above surface).
- Selected: filled pill behind icon, `secondaryContainer` background,
  `onSecondaryContainer` icon + label. 64dp wide, 32dp tall pill.
- Unselected: ghost icon + label, `onSurfaceVariant`.
- Center FAB-style action: a single "Start focus" pill that
  morphs to a focus ring when a session is active.

#### Navigation rail (tablet / large screens)
- 80dp wide.
- Same selection treatment as bottom nav, vertically.

### Permission screens

- Full-screen `surface` background.
- Centered icon (96dp) using the relevant M3 icon, in `primary`
  color.
- Headline (`headlineSmall`): "Precisamos da sua ajuda".
- Body (`bodyMedium`): one sentence explaining the permission in
  human terms. No legal language.
- Single primary button leading to Settings.
- No illustrations of people. No lock icons. No scary red.

### Blocking screens

- Full-screen **`primaryContainer` background** — this is the only
  place the brand teal gets to fill the screen. It is the moment.
- Centered brand mark at 160dp, in `onPrimaryContainer`.
- Goal name in `headlineSmall`, in `onPrimaryContainer`.
- Single primary action button: "Voltar ao foco" (Back to focus),
  filled `onPrimaryContainer` background, `primary` text, pill
  shape. The kinetic moment.
- Secondary text button below: "Pausar por 5 minutos" (Pause for 5
  minutes). This is the kindness the product owes the user.
- No "You have been blocked" language. No red. No alarm sound.

### Focus screens

- Full-screen `background` (cream).
- Centered focus ring (`FocusRingSize`, 240dp), drawn as a partial
  circle — the C/arc from the icon.
- Time remaining in `NumberDisplayLarge` at the ring's center.
- Below: current goal name in `titleLarge`.
- Pause / End controls at the bottom, as pill buttons.
- Ambient music note: none. The point is the silence.

## 7. Dashboard visual hierarchy

From top to bottom, the screen tells a single story: *How is your focus
going today, and what can you do about it?*

```
┌─────────────────────────────────────────────────────┐
│  [Date]                              [settings] ⓘ   │  ← top bar, calm
│                                                     │
│  Foco de hoje                                       │  ← labelSmall, onSurfaceVariant
│                                                     │
│  1h 24m                          60%                │  ← NumberDisplayLarge, primary
│                                                     │
│  ◔────────────●                                    │  ← focus ring, C-shape, 240dp
│                                                     │
│        [  Começar foco  ]                          │  ← primary action pill, tertiary
│                                                     │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌───────────────────────────────────────────────┐ │  ← Card: Active goal
│  │ Meta ativa                                    │ │  ← labelSmall
│  │ Terminar relatório semanal                    │ │  ← titleMedium
│  │ ▓▓▓▓▓▓▓▓░░░░░  65%                          │ │  ← progress bar, primary
│  └───────────────────────────────────────────────┘ │
│                                                     │
│  12 dias seguidos  🌿                              │  ← Streak, sage accent
│                                                     │
│  Esta semana                                        │  ← titleSmall
│  ▁▃▂▅▇▄▆▅                                          │  ← weekly chart, surfaceVariant
│                                                     │
│  Apps bloqueados                                    │
│  [icon] [icon] [icon] [icon] [+]                   │  ← horizontal scroll
│                                                     │
└─────────────────────────────────────────────────────┘
```

Density principle: **never more than 5 distinct visual blocks above the
fold**. If a screen has more, the user feels the product is yelling.

## 8. Kotlin implementation

All files live in `app/src/main/java/com/unpostpone/app/ui/theme/`:

| File | Purpose |
|---|---|
| `Color.kt` | Brand anchors, M3 light + dark `Color` values, `SemanticColors` data class + light/dark instances |
| `Font.kt` | `ManropeFontFamily`, `JetBrainsMonoFontFamily` via Google Fonts |
| `Type.kt` | Full M3 typography scale + number scale, with tabular figures on numbers |
| `Shape.kt` | `UnpostponeShapes` (8 / 12 / 20 / 28 / 36 corners) |
| `Motion.kt` | Easing tokens, duration constants, `GentleSpring` |
| `Dimens.kt` | Spacing + layout + icon + touch-target tokens |
| `Theme.kt` | `UnpostponeTheme { }` composable + `LocalSemanticColors` + `UnpostponeTheme.semantic` accessor |

### Usage

```kotlin
// Anywhere in the app:
@Composable
fun MyScreen() {
    UnpostponeTheme {
        Scaffold { padding ->
            Column(Modifier.padding(padding).padding(Dimens.ScreenGutter)) {
                Text(
                    text  = "Foco de hoje",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text  = "1h 24m",
                    style = NumberDisplayLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                // Success state uses the semantic extension:
                val semantic = UnpostponeTheme.semantic
                Surface(
                    color = semantic.successContainer,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(
                        text  = "Meta concluída",
                        color = semantic.onSuccessContainer,
                        modifier = Modifier.padding(Dimens.CardPadding),
                    )
                }
            }
        }
    }
}
```

The brand mark Composable (a Composable that draws the C/clock + leaf
from the icon) lives in `ui/components/BrandMark.kt` — a separate
deliverable if you want it; not in scope for the design system itself.
