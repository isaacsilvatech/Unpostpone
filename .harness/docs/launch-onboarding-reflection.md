# Launch, Onboarding & Reflection — UX Spec

The first moments a user has with Unpostpone. The arc: **welcomed →
informed → supported**. Not pressured, not gated, not guilt-tripped.

## 1. The user journey

```
┌──────────────────────────────────────────────────────────────────────┐
│  COLD START                                                          │
│  ──────────                                                          │
│  T+0.0s   Splash animation begins                                   │
│  T+0.4s   Clock arc drawn — "time"                                  │
│  T+0.7s   Center pulse — "focus"                                    │
│  T+1.0s   Hands swing in — "what you do with time"                  │
│  T+1.3s   Leaf grows — "what time can become"                       │
│  T+1.7s   Wordmark fades in                                         │
│  T+2.3s   Hold                                                      │
│  T+2.6s   Navigate to Onboarding (first launch) or Dashboard        │
│                                                                      │
│  ONBOARDING (first launch only)                                      │
│  ─────────────────────────────                                      │
│  Page 0   Welcome — brand mark, headline, subtitle, "Get started"   │
│           Swipe →                                                    │
│  Page 1   Features — 3 cards (Focus / Goals / Insights)             │
│           Swipe →                                                    │
│  Page 2   Privacy — "what we use" + "what we don't"                  │
│           Swipe →                                                    │
│  Page 3   Permissions — 3 cards (Accessibility / Usage / Notifs)     │
│           "Get started" → completes onboarding                      │
│                                                                      │
│  RETRY ONBOARDING (Settings → "Replay onboarding")                   │
│  ────────────────────────────────────────────                        │
│  Same flow. SharedPreferences flag is reset before navigation.       │
│                                                                      │
│  REFLECTION (triggered by AccessibilityService on a blocked app)     │
│  ────────────────────────────────────────────────────────             │
│  T+0.0s   Mark fades in with floating motion, message shown         │
│  T+0.0s   Two actions HIDDEN                                         │
│  T+5.0s   Actions fade in (500ms)                                    │
│  T+user   Tap "Stay focused" → return to home / finish activity     │
│           Tap "Continue"    → close reflection, app proceeds        │
└──────────────────────────────────────────────────────────────────────┘
```

## 2. Visual storyboards

### Experience 1 — Splash

```
  ┌──────────────────────────────────────────────────┐
  │                                                  │
  │                                                  │
  │                                                  │
  │                                                  │
  │                  ◔ ───●  ◯                       │
  │                  │  ╲  ╱                        │
  │                  ●   ╳                          │
  │                       ╲                         │
  │                        🍃                       │
  │                                                  │
  │                  Unpostpone                      │
  │            Use your time intentionally           │
  │                                                  │
  │                                                  │
  │                                                  │
  └──────────────────────────────────────────────────┘

  Phase 1 (0.0–0.4s):   the C/arc draws in
  Phase 2 (0.4–0.7s):   the orange center dot scales in
  Phase 3 (0.5–0.75s):  the hour hand (shorter) and minute hand
                        (longer) swing in from behind the center
  Phase 4 (0.7–0.9s):   the leaf grows from the bottom-right
  Phase 5 (0.85–1.0):   the wordmark fades in
```

The background is `MaterialTheme.colorScheme.background` — the cream. The
mark itself is drawn in `onBackground` (deep text), `tertiary` (orange),
and `UnpostponeTheme.semantic.success` (sage). No image assets, no
Lottie. The whole thing is `Canvas`.

### Experience 2 — Onboarding (4 pages)

```
  PAGE 0 — Welcome                  PAGE 1 — Features
  ┌────────────────────┐            ┌────────────────────┐
  │                    │            │                    │
  │        ◔ ●         │            │  Designed for      │
  │        │ ╲         │            │  focus             │
  │        ●  ╲        │            │                    │
  │            🍃      │            │  ┌──────────────┐  │
  │                    │            │  │ ⏱ Focus      │  │
  │                    │            │  │   Sessions   │  │
  │  Take control of   │            │  └──────────────┘  │
  │  your time         │            │  ┌──────────────┐  │
  │                    │            │  │ ⚑ Daily      │  │
  │  Unpostpone helps  │            │  │   Goals      │  │
  │  you stay focused… │            │  └──────────────┘  │
  │                    │            │  ┌──────────────┐  │
  │  Learn more        │            │  │ 📊 Gentle    │  │
  │           [Cont.]  │            │  │   Insights   │  │
  │                    │            │  └──────────────┘  │
  └────────────────────┘            └────────────────────┘

  PAGE 2 — Privacy                  PAGE 3 — Permissions
  ┌────────────────────┐            ┌────────────────────┐
  │  Privacy first     │            │  A few permissions │
  │                    │            │                    │
  │  ┌──────────────┐  │            │  ┌──────────────┐  │
  │  │ 🎯 What we   │  │            │  │ ♿ Accessib.  │  │
  │  │   use        │  │            │  │   [Enable]   │  │
  │  └──────────────┘  │            │  └──────────────┘  │
  │  ┌──────────────┐  │            │  ┌──────────────┐  │
  │  │ 🛡 What we   │  │            │  │ ⏱ Usage      │  │
  │  │  don't       │  │            │  │   [Enable]   │  │
  │  │  collect     │  │            │  └──────────────┘  │
  │  └──────────────┘  │            │  ┌──────────────┐  │
  │                    │            │  │ 🔔 Notifs    │  │
  │                    │            │  │   [Enable]   │  │
  │                    │            │  └──────────────┘  │
  │                    │            │                    │
  │           [Get    │            │           [Get    │
  │            started]│            │            started]│
  └────────────────────┘            └────────────────────┘
```

The page-indicator row is at the bottom of every page: 4 pill dots, the
active one widened to 24dp. The "Continue" pill on the right uses
`tertiary` color (the orange CTA) — the only orange on the page.

### Experience 3 — Reflection / Focus Reminder

```
  T = 0.0s                         T = 5.0s
  ┌────────────────────┐           ┌────────────────────┐
  │                    │           │                    │
  │                    │           │                    │
  │        ◔ ●         │           │        ◔ ●         │
  │        │ ╲         │           │        │ ╲         │
  │        ●  ╲        │           │        ●  ╲        │
  │            🍃      │           │            🍃      │
  │                    │           │                    │
  │                    │           │                    │
  │  Small progress    │           │  Small progress    │
  │  every day         │           │  every day         │
  │  creates           │           │  creates           │
  │  meaningful        │           │  meaningful        │
  │  results.          │           │  results.          │
  │                    │           │  Take a moment.    │
  │                    │           │                    │
  │                    │           │  ┌──────────────┐  │
  │                    │           │  │ Stay focused │  │
  │  (no buttons yet)  │           │  └──────────────┘  │
  │                    │           │      Continue      │
  └────────────────────┘           └────────────────────┘
```

The mark floats (4dp vertical drift over 3.2s) and breathes (1.0 → 1.02
scale, also 3.2s, same easing). The drift and the breath share the
same infinite transition so they stay synchronized.

## 3. Screen architecture

```
presentation/
  splash/
    SplashScreen.kt          ← Composable: cream background, animated BrandMark
    SplashViewModel.kt       ← drives master progress, picks next destination
  onboarding/
    OnboardingScreen.kt      ← HorizontalPager host, 4 page Composables
    OnboardingViewModel.kt   ← tracks current page, completes onboarding
  focusreminder/
    FocusReminderScreen.kt   ← single screen with reflection delay
    FocusReminderViewModel.kt ← rotates message, fires actions after 5s

ui/components/
  BrandMark.kt               ← animated Canvas mark (the centerpiece)

domain/repository/
  OnboardingPreferences.kt   ← interface (pure Kotlin, no Android types)

data/repository/
  OnboardingPreferencesImpl.kt ← SharedPreferences-backed implementation
```

### Why this structure

- **Each screen owns its ViewModel.** MVVM + `StateFlow<UiState>` +
  `collectAsStateWithLifecycle()`, consistent with the rest of the app.
- **`BrandMark` is in `ui/components/`, not in any one feature
  package.** It's a shared visual primitive; the splash, the dashboard,
  the blocker, and the reflection all draw it.
- **OnboardingPreferences is an interface in `domain/`, implemented in
  `data/`.** The ViewModel depends on the interface, not the
  implementation. Tests can swap in a fake.
- **The focus reminder is a separate screen, not a Dialog or a
  BottomSheet.** It's reached by a deep link from the
  AccessibilityService, so it lives in its own backstack entry and can
  be `popBackStack()`ed independently of the rest of the app.

## 4. Navigation flow

```
   Splash
     │
     ├── first launch  →  Onboarding
     │                       │
     │                       ├── 0  Welcome
     │                       ├── 1  Features
     │                       ├── 2  Privacy
     │                       ├── 3  Permissions
     │                       │       │
     │                       │       ├─ Accessibility ─→ Settings
     │                       │       ├─ Usage access ──→ Settings
     │                       │       └─ Notifications ─→ Settings
     │                       │
     │                       └── "Get started"  →  Dashboard
     │
     └── returning user ─→  Dashboard
                              │
                              ├── Goals
                              ├── Statistics
                              └── Settings
                                    │
                                    └── "Replay onboarding"  →  Onboarding

   FocusReminder  ←── AccessibilityService deep-link (packageName)
       │
       ├── "Stay focused"  →  popBackStack
       └── "Continue"     →  popBackStack
```

Permission requests in onboarding open the **platform Settings intent**
because Android does not let third-party apps grant
`BIND_ACCESSIBILITY_SERVICE` or `PACKAGE_USAGE_STATS` from a normal
in-app dialog. The flow is: user reads the explanation → taps Enable →
lands in Settings → comes back. The onboarding pager stays in place.

## 5. Animation recommendations

| Element | Animation | Duration | Easing |
|---|---|---|---|
| Splash — clock arc | sweep 0° → 270° | 400ms | Emphasized (0.2, 0, 0, 1) |
| Splash — center | scale 0 → 1 | 150ms | Emphasized |
| Splash — hands | angle lerp from tucked to final | 250ms | Emphasized |
| Splash — leaf | scale + 12° rotation | 200ms | Emphasized |
| Splash — wordmark | fade-in | 400ms | Linear |
| Onboarding — page change | horizontal slide + fade | 300ms | Emphasized |
| Page dots | width tween 8 → 24dp | 250ms | Emphasized |
| Reflection — mark float | translateY -4 ↔ +4 | 3200ms | Sine (Reverse) |
| Reflection — mark breath | scale 1.0 ↔ 1.02 | 3200ms | Sine (Reverse) |
| Reflection — actions reveal | fade-in | 500ms | Emphasized |
| Reflection — outcome | crossfade to next | 250ms | Emphasized |

**Motion rules (from the design system):**
- Never bounce. No overshoot.
- The reflection mark's float and breath share the same infinite
  transition so they stay synchronized — the eye reads them as one
  breath, not two competing animations.
- All entrance animations use EmphasizedEasing. All exit animations use
  a slightly faster (200ms) Linear or Emphasized curve. The eye
  registers entry slower than exit; this is how the brain works.

## 6. Implementation notes

### Hilt wiring
- `OnboardingPreferences` is bound to `OnboardingPreferencesImpl` in
  `AppModuleBinds` (a sibling of `AppModule` for `@Binds`).
- `SharedPreferences` is `@Provides`-ed in `AppModule` using
  `@ApplicationContext`.

### Why SharedPreferences, not DataStore
DataStore is the modern recommendation, but adding it requires a
Gradle change (`androidx.datastore:datastore-preferences:1.1.1`) and
a one-flag storage is not worth that change. When the rest of the app
adopts DataStore, swap the impl in one place — the interface stays.

### Splash → first-run branching
- The Splash is the start destination.
- After the animation, `SplashViewModel` checks
  `onboardingPreferences.hasCompletedOnboarding()`.
- If false → push Onboarding. If true → pop Splash, push Dashboard.

### Permission intents
- Accessibility → `Settings.ACTION_ACCESSIBILITY_SETTINGS`
- Usage access → `Settings.ACTION_USAGE_ACCESS_SETTINGS`
- Notifications → `Settings.ACTION_APP_NOTIFICATION_SETTINGS` (Android
  13+ needs `POST_NOTIFICATIONS` runtime permission — that lives in
  `MainActivity.onCreate` for now; trigger it from the onboarding
  callback in a follow-up)

### Accessibility (a11y) on these screens
- Every `TextButton` and `Button` has its label as text content — no
  icon-only buttons without `contentDescription`.
- The page dots have `contentDescription = "Page N of 4"` via a
  `semantics` block (TODO: add to the pager).
- The reflection mark has `Modifier.semantics { contentDescription = "Unpostpone" }`
  so TalkBack reads the brand.
- All tap targets ≥ 48dp (the M3 `Button` and `TextButton` defaults).

### Files added
| File | Lines | Role |
|---|---|---|
| `ui/components/BrandMark.kt` | ~230 | Animated Canvas brand mark |
| `presentation/splash/SplashScreen.kt` | ~95 | The launch experience |
| `presentation/splash/SplashViewModel.kt` | ~70 | Drives animation + routing |
| `presentation/onboarding/OnboardingScreen.kt` | ~430 | 4-page pager |
| `presentation/onboarding/OnboardingViewModel.kt` | ~30 | Pager state + completion |
| `presentation/focusreminder/FocusReminderScreen.kt` | ~250 | Reflection screen |
| `presentation/focusreminder/FocusReminderViewModel.kt` | ~50 | Message rotation, 5s timer |
| `domain/repository/OnboardingPreferences.kt` | ~10 | Domain interface |
| `data/repository/OnboardingPreferencesImpl.kt` | ~25 | SharedPreferences impl |
| `presentation/navigation/Screen.kt` | (updated) | Added Splash, Onboarding, FocusReminder |
| `presentation/navigation/NavGraph.kt` | (updated) | Wired the new screens + permission intents |
| `di/AppModule.kt` | (updated) | Provides SharedPreferences + binds prefs |
| `res/values/strings.xml` | (updated) | New strings for all three screens |
