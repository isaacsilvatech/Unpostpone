# AGENTS.md

Unpostpone — Android procrastination control app ("Controle de procrastinação").
Single-module Kotlin/Compose app focused on blocking distracting apps via an
AccessibilityService and a foreground monitoring service.

## Setup commands

- Install deps:  `gradle wrapper` (wrapper is committed; run `.\gradlew --version` once to bootstrap)
- Build (debug): `./gradlew :app:assembleDebug`
- Build (release): `./gradlew :app:assembleRelease` (signing not configured — see Security)
- Test (unit):  `./gradlew :app:testDebugUnitTest`
- Test (instrumented): `./gradlew :app:connectedDebugAndroidTest` (requires an emulator/device)
- Lint:         `./gradlew :app:lintDebug`
- Clean:        `./gradlew clean`

**Build policy — DO NOT assemble APKs from the harness.** Orchestrators and
workers must NOT run `./gradlew :app:assembleDebug` or `:app:assembleRelease`
during normal work. The user builds the APK manually on their own machine
after reviewing the diff. Use `:app:compileDebugKotlin` and
`:app:testDebugUnitTest` for fast verification; leave packaging to the user.
Generated `app/build/outputs/apk/**/*.apk` files are not deliverables — never
attach them to chat unless the user explicitly asks.

Package manager: Gradle (Kotlin DSL) with version catalog at `gradle/libs.versions.toml`.
No `packageManager` lockfile — Gradle's dependency cache is the source of truth.

## Project layout

- `app/` — single Android application module (`com.unpostpone.app`)
  - `app/src/main/java/com/unpostpone/app/`
    - `core/util/` — shared constants and `Resource` wrapper
    - `data/local/` — Room database, DAOs, entities
    - `data/datasource/` — local data source adapters
    - `data/repository/` — repository implementations
    - `domain/model/` — pure Kotlin domain models (no Android types)
    - `domain/repository/` — repository interfaces
    - `domain/usecase/` — single-purpose use cases (grouped by feature)
    - `di/` — Hilt modules (`AppModule`, `DatabaseModule`, `RepositoryModule`)
    - `presentation/` — Compose screens + ViewModels, grouped by feature
      - `presentation/navigation/` — `NavGraph` + `Screen` sealed class
    - `service/` — system services (NOT presentation)
      - `service/accessibility/` — `UnpostponeAccessibilityService` (core blocker)
      - `service/monitoring/` — `AppMonitoringService` (foreground, `specialUse`)
    - `ui/theme/` — Material 3 `Color.kt`, `Theme.kt`, `Type.kt`, `Font.kt`
  - `app/src/main/res/` — resources, mipmaps, manifest XML, accessibility config
  - `app/src/test/java/` — JVM unit tests
  - `app/src/androidTest/java/` — instrumented (Espresso/Compose UI) tests
- `docs/` — product/design notes (`desc.txt`, `arctechure.txt`, `paleta-cores.txt`, icons)
- `gradle/libs.versions.toml` — version catalog (single source of truth for dependencies)

`.harness/` (this directory) — Mavis multi-agent team for ongoing work.

## Code style

- Kotlin official style (`kotlin.code.style=official` in `gradle.properties`).
- 4-space indent, no tabs. Trailing commas on multi-line collections.
- One public top-level declaration per file; filename matches the primary symbol.
- Domain layer MUST stay pure Kotlin — no `android.*` imports, no `androidx.*`.
- Presentation layer uses Compose; ViewModels expose `StateFlow<UiState>`.
- Hilt for DI: `@HiltAndroidApp` on `UnpostponeApplication`, `@AndroidEntryPoint` on
  activities/services, `@HiltViewModel` on ViewModels, `@Inject` constructors.
- Room for persistence: entities, DAOs, `AppDatabase` (KSP-generated).
- Concurrency: Kotlin Coroutines + `Flow` only. No RxJava. No `Thread`/`Handler` from app code.
- No `detekt` or `ktlint` is currently configured — use `lintDebug` and IntelliJ/Android Studio
  inspections until a formatter is added. When adding one, wire it into the version catalog and
  the build, and update this file.
- **Avoid redundant comments.** Prefer self-explanatory code (descriptive names) over comments.
  Do NOT add comments that restate what the code does, decorative section dividers, ASCII-art
  banners, or KDoc on private/internal/obvious members. OK to keep a short `why` comment where
  the intent is not obvious from the code, and KDoc on genuinely public APIs. The harness
  enforces this — see `.harness/agent.md`.

## Testing instructions

- Frameworks: JUnit4 for unit, Espresso + Compose UI test for instrumented.
- Unit tests live in `app/src/test/java/com/unpostpone/app/...` and run on the JVM.
- Instrumented tests live in `app/src/androidTest/java/com/unpostpone/app/...` and require
  a connected device or running emulator.
- Currently only `ExampleUnitTest` and `ExampleInstrumentedTest` exist as stubs — every
  new behavior needs a real test alongside it (ViewModel state, use case, DAO query).
- Tests for the `service/` layer are tricky (real Android services) — prefer extracting
  pure logic into a `domain/usecase/` and unit-testing that. For service-lifecycle tests,
  use `ServiceTestRule` or `androidx.test.core.app.ServiceScenario`.
- All tests must pass before opening a PR.

## PR & commit conventions

- Default branch: `main`. Never push to it directly — branch from `main`.
- Branch naming: `feat/<scope>-<short>`, `fix/<scope>-<short>`, `chore/<scope>`.
- Commit messages: Conventional Commits (`feat:`, `fix:`, `chore:`, `refactor:`,
  `docs:`, `test:`). Existing examples in git log: `feat(theme): fonts`,
  `feat(theme): icon and colors`, `fix(build): resolve dependencies`.
- Open PR via `gh pr create` once CI is green (no CI yet — see below).
- Keep commits small and focused; one logical change per commit.

## Security

- This app handles sensitive Android permissions: `BIND_ACCESSIBILITY_SERVICE`,
  `QUERY_ALL_PACKAGES`, `PACKAGE_USAGE_STATS`, `FOREGROUND_SERVICE_SPECIAL_USE`.
  Any change touching `service/`, `AndroidManifest.xml`, or these permissions
  needs explicit review.
- Never commit secrets. `local.properties` is in `.gitignore` (contains
  `sdk.dir` and any future signing/release keys). `.env` files must be
  gitignored before any are introduced.
- No release signing config yet — `release` build type is `optimization { enable = false }`
  and will not produce a signed APK. Add `signingConfigs` and a `keystore.properties`
  file (gitignored) before publishing.
- The `app/build/` and `.gradle/` directories are gitignored — never commit generated
  artifacts.
- The accessibility service has full UI access on the device; treat code in
  `service/accessibility/` as security-critical and prefer small, reviewable diffs.

## CI/CD

- No CI configured (`.github/`, `.gitlab-ci.yml`, etc. are absent). When adding CI:
  - Run `./gradlew :app:testDebugUnitTest lintDebug :app:assembleDebug` on PRs.
  - Cache `~/.gradle/caches` and `.gradle/` between runs.
  - Sign the release APK in a separate workflow with secrets from the repo settings.
- The `app/src/main/keepRules/rules.keep` file is the R8/Proguard keep rules for
  the release build — review before any release build is enabled.

## Documentation pointers

- Product description: `docs/desc.txt`
- Architecture decisions: `docs/arctechure.txt` (Kotlin + Compose, MVVM + Clean
  Architecture, Room, Hilt, Coroutines/Flow)
- Color palette: `docs/paleta-cores.txt` (primary `#0F4C5C`, secondary `#2D6A73`,
  action `#F4A261`, success `#84A98C`, neutral `#F7F4EA`)
- Agent team structure: `.harness/agent.md` (Harness orchestrator) +
  `.harness/reins/<name>/agent.md` (specialist reins)
