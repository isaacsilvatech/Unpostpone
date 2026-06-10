# Test Policy — Unpostpone

What we test, where, and with which tools. Owned by the **tester** rein;
overrides to this doc require the user.

## Stack (already wired in `app/build.gradle.kts`)

| Tool | Version | Source set | Purpose |
|---|---|---|---|
| JUnit 4 | 4.13.2 | `test` | Assertions, `@Test`, runners |
| `kotlinx-coroutines-test` | 1.10.2 | `test` | `runTest`, `TestDispatcher`, virtual time |
| `androidx.room:room-testing` | 2.7.1 | `test` | In-memory Room database for DAO tests |
| `robolectric` | 4.16.1 | `test` | Android resource / context on the JVM (sparingly) |
| `androidx.test.ext:junit` | 1.1.5 | `androidTest` | Instrumented test runner |
| `androidx.test:core` | 1.1.5 | `androidTest` | Activity / provider test rules |
| `androidx.test.espresso:espresso-core` | 3.5.1 | `androidTest` | View-based assertions |
| `androidx.compose.ui:ui-test-junit4` | (BOM) | `androidTest` | `createComposeRule()` |

**Don't add a new test library** without proposing it in a rein
report and adding it to `gradle/libs.versions.toml` first.

## What to test (priority order)

1. **Use cases** (`domain/usecase/**`) — the highest-value target.
   Pure functions over repository interfaces. Test with **fakes**,
   not mocks. Cover:
   - happy path
   - empty state (no data)
   - failure path (repository throws)
2. **Repositories** (`data/repository/**`) — DAO tests with
   `inMemoryDatabaseBuilder`; DataStore-backed preferences with a
   temp file or a fake. Don't test the SQL string — test the
   behaviour your callers depend on.
3. **ViewModels** (`presentation/**/*ViewModel`) — `runTest` +
   `TestDispatcher`. Inject use cases by constructor (no Hilt
   required for tests). Cover state-flow emissions, not internal
   method calls.
4. **Compose screens** — only when a screen has non-trivial
   interaction. Use `createComposeRule()` with
   `onNodeWithText`/`onNodeWithTag`. Avoid testing visual styling.
5. **Background work** (alarms, accessibility, foreground services) —
   not unit-testable in any meaningful way. Cover with
   **manual-test plans** that the user runs on a device; the
   blocker-expert and pomodoro-expert reins own those plans.

## What NOT to test

- Trivial getters/setters.
- Room generated code (it's not yours to break).
- Compose theme tokens (snapshot-tested in a future iteration if
  the user asks; not now).
- Manifest declarations (covered by the build).

## Layout

Mirror `main/` package layout under `test/` and `androidTest/`. If
the production class is at
`app/src/main/java/com/unpostpone/app/domain/usecase/pomodoro/StartPomodoroSessionUseCase.kt`,
the test goes at
`app/src/test/java/com/unpostpone/app/domain/usecase/pomodoro/StartPomodoroSessionUseCaseTest.kt`.

## Commands (the user runs these)

- Unit tests only (fast, JVM): `./gradlew :app:testDebugUnitTest`
- Instrumented (needs a device or emulator):
  `./gradlew :app:connectedDebugAndroidTest`
- Lint: `./gradlew :app:lintDebug`

The tester rein never runs these directly.

## Coverage target (proposal, not enforced)

- Domain (`domain/**`): aim for > 80% line coverage on use cases.
- Data (`data/**`): > 60% on repository impls (Room boilerplate
  doesn't count).
- Presentation: ViewModels >= 50%; Composables only the
  non-trivial ones.

Run `./gradlew :app:createDebugUnitTestCoverageReport` to get a
report — the user can wire this into CI later.
