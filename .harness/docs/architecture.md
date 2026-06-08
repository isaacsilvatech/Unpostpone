# Unpostpone — Architecture & Engineering Standards

This document is the **shared reference** for the team. The reins link here
instead of inlining the rules. Update this file when the standards change;
don't drift the rules across multiple `agent.md` files.

## Stack

- **Language**: Kotlin 2.2.10 (official code style)
- **UI**: Jetpack Compose (BOM 2026.02.01) + Material 3 + Google Fonts
  (Manrope)
- **DI**: Hilt 2.59.2
- **Persistence**: Room 2.7.1 (KSP-generated)
- **Navigation**: androidx.navigation 2.9.0
- **Concurrency**: Kotlin Coroutines + Flow
- **Build**: Gradle Kotlin DSL, version catalog at
  `gradle/libs.versions.toml`
- **JVM target**: Java 11 (source & target compatibility)
- **SDK**: minSdk 29, targetSdk 36, compileSdk 36.1

Package root: `com.unpostpone.app`.

## Layering

Strict clean-architecture layering — keep the boundaries clean.

```
presentation/   Compose + ViewModel + StateFlow (Android, Compose, Hilt, Lifecycle)
       │
       ▼
domain/         PURE KOTLIN ONLY. model + repository interfaces + use cases.
       ▲
       │
data/           Room + Hilt modules + repository implementations.
                Knows about Room, Android Context (for Hilt providers), Dispatchers.
       │
       ▼
service/        Android system services. AccessibilityService + foreground service.
                Consumes domain via Hilt-injected use cases; never touches data/ directly.
```

### Hard rules

1. **`domain/` is pure Kotlin.** No `android.*`, no `androidx.*`, no Room
   annotations, no Hilt annotations. Allowed: `kotlinx.coroutines.*`,
   `kotlinx.coroutines.flow.*`, kotlin stdlib, java stdlib.
2. **Repository interfaces in `domain/`, implementations in `data/`.**
   The implementation is bound to the interface via `@Binds` in
   `RepositoryModule`.
3. **Use cases are the public API for non-UI consumers.** A screen calls
   one or more use cases via its ViewModel; ViewModels never touch
   repositories directly except for trivial pass-throughs.
4. **Services consume use cases, not repositories.** The accessibility
   service and the foreground monitoring service are wired by Hilt and
   call into `domain/usecase/` to decide whether to block, which goal
   is active, etc.
5. **ViewModels are `HiltViewModel`s with `StateFlow<UiState>`.**
   State shape is a `data class FooUiState(...)`; user intents are a
   `sealed interface FooEvent` (or `enum` for small screens).

## State management pattern (presentation)

```kotlin
data class FooUiState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface FooEvent {
    data object Refresh : FooEvent
    data class ItemClicked(val id: String) : FooEvent
}

@HiltViewModel
class FooViewModel @Inject constructor(
    private val getFoo: GetFooUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(FooUiState())
    val state: StateFlow<FooUiState> = _state.asStateFlow()

    fun onEvent(event: FooEvent) { /* ... */ }
}

@Composable
fun FooScreen(
    state: FooUiState,
    onEvent: (FooEvent) -> Unit,
) { /* ... */ }
```

The composable takes the state + a lambda — never the ViewModel
directly. This keeps previews working without a Hilt environment.

## Navigation

- `presentation/navigation/Screen.kt` is the single source of truth for
  routes.
- Add a new `data object` for each screen, with a typed `createRoute(...)`
  builder if it takes arguments.
- Pass arguments via `NavType.*`; complex payloads go in the repository
  and the route carries an id.
- The blocker screen route is `blocker/{packageName}` and is reached
  from the accessibility service via deep-link or pending intent (see
  `android-service-expert` for the launch side).

## Persistence (Room)

- Entities are `data class` annotated with `@Entity(tableName = "...")`.
- DAOs return `Flow<List<T>>` for observable reads and `suspend fun` for
  writes. No `LiveData` from DAOs.
- `AppDatabase` is a `RoomDatabase` abstract class. Bump the version on
  schema changes; register a `Migration` (don't ship
  `fallbackToDestructiveMigration`).
- The compiler is KSP (`ksp(libs.room.compiler)`) — no kapt.
- New tables / columns go in `data/local/entity/` and are surfaced via
  `data/local/dao/`.

## DI (Hilt)

- `@HiltAndroidApp` on `UnpostponeApplication`.
- `@AndroidEntryPoint` on `MainActivity` and any service that needs
  injection.
- `@HiltViewModel` on every ViewModel.
- `@Module @InstallIn(SingletonComponent::class)` for app-scoped
  bindings:
  - `DatabaseModule` — `@Provides` for `AppDatabase` and each DAO.
  - `RepositoryModule` — `@Binds` for repository interfaces.
  - `AppModule` — anything else (e.g. `CoroutineDispatcher` qualifiers).
- Don't mix `@Provides` and `@Binds` in the same module.

## Permissions & security

This app uses high-risk Android permissions. Treat the `service/` layer
as security-critical; small, reviewable diffs only.

| Permission | Source | When needed |
|---|---|---|
| `FOREGROUND_SERVICE` | Manifest | App-monitoring foreground service. |
| `FOREGROUND_SERVICE_SPECIAL_USE` | Manifest | Android 14+ requires explicit type for this use case. |
| `BIND_ACCESSIBILITY_SERVICE` | User in Settings | The blocker service. |
| `QUERY_ALL_PACKAGES` | Manifest + tools-ignore | Detecting installed apps to add to the blocked list. |
| `PACKAGE_USAGE_STATS` | User in Settings (via `ACTION_USAGE_ACCESS_SETTINGS`) | Statistics feature. |

Any **new** permission or accessibility capability must:
1. Have a one-line comment in the manifest explaining *why* it's needed.
2. Be reported to the orchestrator for explicit user review before merge.

## Testing

- Frameworks: JUnit4, Espresso, Compose UI test.
- ViewModel state transitions → unit test with `TestDispatcher` +
  `runTest`.
- DAO queries → in-memory Room (`Room.inMemoryDatabaseBuilder`) +
  `runTest`.
- Use cases → unit test with a hand-rolled fake repository (no mocking
  framework required).
- Compose screens → `createComposeRule()`; assert on semantics tree, not
  pixels.
- Service lifecycle → `ServiceScenario` or `ServiceTestRule`; prefer
  extracting the decision logic into a `domain/usecase/` and testing
  that instead.
- `app/src/test/java/com/unpostpone/app/...` (unit) and
  `app/src/androidTest/java/com/unpostpone/app/...` (instrumented).

## Strings

- `R.string.*` for all user-visible text.
- The app currently ships in **Portuguese (PT-BR)** — keep new strings
  in PT-BR unless the user asks for another locale.

## Git workflow

- Default branch: `main`. Never push to it directly.
- Branch from `main`: `feat/<scope>-<short>`, `fix/<scope>-<short>`,
  `chore/<scope>`.
- Conventional Commits: `feat:`, `fix:`, `chore:`, `refactor:`,
  `docs:`, `test:`. Use the `(<scope>)` form for clarity (e.g.
  `feat(theme): fonts`).
- Open PR via `gh pr create` once the build is green. No CI yet; when
  it's added, run `./gradlew :app:testDebugUnitTest lintDebug
  :app:assembleDebug` on PRs.

## Versioning

- Single source of truth: `gradle/libs.versions.toml`. Add a new
  dependency there, then reference it via `libs.xxx` in
  `app/build.gradle.kts`.
- Bump the version code / name in `app/build.gradle.kts` `defaultConfig`
  on each release.

## Things to do (gaps, not blockers)

- No CI/CD configured (`.github/`, `.gitlab-ci.yml`, etc. are absent).
- No `detekt` or `ktlint` — rely on `lintDebug` and IDE inspections.
- No release signing — `release` build type is not production-ready
  yet.
- No real test coverage beyond the `ExampleUnitTest` /
  `ExampleInstrumentedTest` stubs.
- The `app/src/main/keepRules/rules.keep` file is the R8/Proguard keep
  rules for release — review before enabling release builds.
