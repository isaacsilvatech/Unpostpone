# Code Standards — Unpostpone

Project-wide conventions every agent on Unpostpone follows. Rein
`agent.md` files link here instead of inlining rules.

## Package layout (clean architecture)

```
com.unpostpone.app
├── core/          # cross-cutting: theme, util, tempunlock helpers
├── data/          # Room + DataStore + repository impls
│   ├── datasource/
│   ├── local/     # entity/, dao/, database class
│   └── repository/
├── di/            # Hilt modules: AppModule, DatabaseModule, …
├── domain/        # pure Kotlin: model/, repository/ (interfaces), usecase/
├── presentation/  # one package per feature: dashboard, goals, statistics, …
│   └── <feature>/
│       ├── components/   # shared UI bits for this feature (optional)
│       └── format/       # formatters (optional)
├── service/       # Android system services: accessibility, pomodoro, tempunlock
└── ui/            # top-level Compose theme glue (re-exported from core/theme)
```

A new feature = a new package under `presentation/<feature>/` + a
ViewModel + use cases under `domain/usecase/<feature>/` + repository
under `data/repository/` + Room pieces under `data/local/` if
persistent. No shortcuts.

## Naming

- **Packages**: lowercase, no underscores, feature-named
  (`pomodoro`, not `pomodoroTimer`).
- **Classes**: PascalCase, no abbreviations (`PomodoroSession`, not
  `PomoSess`).
- **Composable functions**: PascalCase (`PomodoroScreen`, not
  `pomodoroScreen`).
- **Composable state holders (ViewModel)**: `XxxViewModel`.
- **Use cases**: verb-noun (`StartPomodoroSessionUseCase`,
  `GetBlockedAppsUseCase`).
- **Repositories (interface)**: `<Feature>Repository`. **Impl**:
  `<Feature>RepositoryImpl`. Don't use the `Default…` prefix — this
  repo doesn't.
- **DAOs**: `<Feature>Dao`. **Entities**: `<Feature>Entity` (or
  singular noun if it represents a single row, e.g. `PomodoroSession`
  not `PomodoroSessionEntity` — match the existing code; don't
  re-rename an entity to add `Entity`).

## Compose

- **Material 3 only.** No Material 2 imports.
- **BOM-managed** — never pin a Compose version manually; rely on
  `androidx.compose.bom`.
- **Theme tokens** from `core/theme/` (and `ui/theme/`). Don't
  hardcode `Color(0xFF…)` in a screen; if you need a new token,
  add it to the theme first.
- **Manrope** is the project font, loaded via Google Fonts provider
  in `ui/theme/`. Don't add a second font family.
- **State**: ViewModel exposes `StateFlow<XxxUiState>`; the
  composable collects with `collectAsStateWithLifecycle()`.
- **Dark mode detection: read `LocalIsDarkTheme.current`, never
  call `isSystemInDarkTheme()` directly inside a composable.**
  `UnpostponeTheme` exposes `LocalIsDarkTheme` from `ui.theme` so
  every composable sees the same value the `MaterialTheme` is
  actually using. `isSystemInDarkTheme()` only reflects the OS
  setting, not the user's `ThemeMode` preference (which can be
  `SystemDefault` / `Light` / `Dark` in Settings). Reading the
  OS setting directly inside a composable is a divergence bug:
  the rest of the screen uses `MaterialTheme.colorScheme` and
  follows the preference, but a hardcoded `if (isSystemInDarkTheme())
  DarkHeroSurface else LightHeroSurface` will use the wrong
  variant the moment those two values disagree. The only two
  legitimate callers of `isSystemInDarkTheme()` are
  `MainActivity.onCreate` (to combine with the user's `ThemeMode`
  preference into the single source of truth) and `UnpostponeTheme`
  itself (the default parameter value).

## Hilt

- **Application** is `UnpostponeApplication` (annotated `@HiltAndroidApp`).
- **ViewModels**: `@HiltViewModel class XxxViewModel @Inject constructor(...)`.
- **Modules**: one per concern (`DatabaseModule`, `PomodoroModule`,
  `RepositoryModule`, `AppModule`). Don't dump every binding into
  `AppModule`.
- **Inject use cases, not repositories, into ViewModels.** Use cases
  are the public API of the domain layer; the ViewModel doesn't
  know about Room.

## Coroutines

- `viewModelScope` for ViewModel work.
- `lifecycleScope` from the Compose `LocalLifecycleOwner` for UI
  work tied to a screen.
- Use the dispatcher injected via Hilt (`@IoDispatcher`, etc.) —
  not `Dispatchers.IO` directly — for repository / data layer code.
  Add the qualifier in `di/` if it doesn't exist yet.
- Never block the main thread on Room / DataStore / network.

## Logging

- Use `android.util.Log` with tag `Unpostpone/<ClassName>`. No
  `println`. No `Timber` (it's not in the catalog; don't add it
  without justification).
- Don't log user data (block-list package names are fine; clipboard
  contents are not).

## Errors

- Domain layer throws typed exceptions or returns
  `Result<…>` — match what the use case already returns. Don't
  introduce a sealed `XxxError` class without coordinating with
  android-developer.
- Presentation layer maps domain errors to user-facing strings
  using `core/util/` helpers. No raw stack traces in UI.

## Comments

**Default to no comment.** Code that needs a comment to be
understood usually needs to be renamed or rewritten instead.
Workers and reins must follow these rules:

- **No section banners inside function bodies.** Lines like
  `// Zone 1 — Context`, `// Step 2 — fetch`, `// ---- Controls ----`
  are a code smell. If a function has four distinct phases, extract
  them into private composables / helpers named after the phase
  (`SessionTypeChip`, `PomodoroTimerRing`, `PomodoroControls`,
  `CompletedFocusStat`) — the structure then lives in the call
  site, not in noise.
- **No "what" comments.** If a comment can be removed and the
  code below it still reads the same way, the comment is
  redundant. Examples of comments to delete:
  - `val tickRadius = 4.dp // radius of the dot at the top of the ring`
  - `val PresetChipMinWidth = 160.dp // minimum width for a FlowRow preset card`
  - `// Flat Column — controls are NOT a sibling of the ring`
  - `// between preset chips in the FlowRow`
  These restate the constant name, the function it sits in, or
  the obvious structure of a well-named `Column { A(); B(); C() }`.
- **Comments that earn their place** are the exception, not the
  rule. Keep them only when they explain a **non-obvious
  decision** that the code cannot express:
  - A `// why` for a magic value with no obvious source
    (e.g. `// 50dp matches the Material 3 tonal-button minimum tap
    target on tablets`).
  - A `// why` for an intentional deviation from a pattern
    (e.g. `// intentionally NOT using MaterialTheme.colorScheme.primary
    here — see ticket XXX for accessibility contrast reason`).
  - A `// references doc/spec link` for non-trivial business
    rules (e.g. `// per Android 14 foregroundServiceType rules: ...`).
  - A `// TODO(<owner>): <date>` for known follow-ups. Owner is
    a rein name or a person's GitHub handle, not "TODO".
- **KDoc on public API is fine.** A short KDoc on a public
  composable, use case, or repository interface (one to three
  lines: what it does, when to call it) is encouraged and
  expected. KDoc on private helpers is not.
- **No decorative `// ─── Section ───` banners in source files.**
  In Kotlin, the visual structure is the file's package +
  function order. If a file genuinely has five unrelated concerns,
  split it. The `Dimens.kt` style of `// ── Focus ring ──` /
  `// ── Touch targets ──` is OK **only** inside theme/design
  token files where the comments group related constants — and
  even there, prefer named sub-objects (e.g. `object Spacing`,
  `object Stroke`) over comment headers.
- **No "for the reviewer" or "for the verifier" comments.** Code
  is for the next developer reading it, not for the agent that
  just wrote it. If you need to explain a workaround to a
  reviewer, put it in the changelog or the deliverable doc, not
  in the source.

The verifier will FAIL a task that adds redundant "what"
comments. If the comment doesn't pass the "remove it and the
code still reads correctly" test, delete it before submitting.
