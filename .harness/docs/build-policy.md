# Build & Dependency Policy — Unpostpone

How dependencies, versions, and the build are managed. Owned by the
**orchestrator** (with the **android-developer** rein implementing).

## Version catalog

All versions live in **`gradle/libs.versions.toml`**. Every
dependency the app uses — production, test, or otherwise — has an
entry there. No version strings in `app/build.gradle.kts` other than
the `alias(libs.…)` references.

When adding a dependency:

1. Add a `versions.<name> = "x.y.z"` entry in `[versions]`.
2. Add a `libraries.<alias> = { group = …, name = …, version.ref = "<name>" }`
   entry in `[libraries]`.
3. Reference it in `app/build.gradle.kts` as
   `implementation(libs.<alias>)` (or `testImplementation`,
   `androidTestImplementation`, `ksp`, `kspTest`, `debugImplementation`,
   etc., as appropriate).
4. If it's a Gradle plugin, add a `plugins.<alias>` entry under
   `[plugins]` and apply with `alias(libs.plugins.<alias>)` in
   `app/build.gradle.kts` or `build.gradle.kts`.

## Plugins

Current set in `app/build.gradle.kts`:

- `com.android.application` (AGP 9.2.1)
- `org.jetbrains.kotlin.plugin.compose` (Kotlin 2.2.10)
- `com.google.dagger.hilt.android` (Hilt 2.59.2)
- `com.google.devtools.ksp` (KSP 2.3.6)

Adding a new plugin is a top-level concern; the user should approve
because it affects build time and IDE behaviour.

## SDK levels (do not change without the user)

- `compileSdk`: 36 (release API 36.1, declared via `release(36)
  { minorApiLevel = 1 }` in `app/build.gradle.kts`).
- `minSdk`: 29 (Android 10).
- `targetSdk`: 36.
- `sourceCompatibility` / `targetCompatibility`: Java 11.
- `kotlin` JVM target: inherits from Java 11 (verify the
  `kotlin { jvmToolchain(11) }` block if it exists; if not, add it
  when you bump Kotlin).

## Build types

`app/build.gradle.kts` currently defines a single `release` build
type with `optimization { enable = false }`. The
**android-developer** rein may need to:

- Add a `debug` build type (usually inherited, but confirm
  `applicationIdSuffix = ".debug"`).
- Add `signingConfigs` once the user supplies a keystore.
- Add `buildTypes.beta` if the user wants a staged rollout.

These are not in scope for the harness bootstrap; flag them as
follow-ups if the user asks.

## Compose

`buildFeatures { compose = true; buildConfig = true }` is set.
Don't disable `buildConfig` — other parts of the app may rely on
`BuildConfig.*` constants.

## Build commands

| Goal | Command | Who runs it |
|---|---|---|
| Compile debug | `./gradlew :app:assembleDebug` | **Anyone in the harness** (orchestrator and workers/reins) may run this as a self-check. Mention the result in the report. |
| Compile debug (Kotlin only, faster) | `./gradlew :app:compileDebugKotlin` | **Anyone in the harness** — preferred for fast syntax / unresolved-reference checks. |
| Compile release | `./gradlew :app:assembleRelease` | User (release builds can have signing-config surprises; the user wants to see the output). |
| Unit tests | `./gradlew :app:testDebugUnitTest` | **User only.** No one in the harness runs test tasks. |
| Instrumented tests | `./gradlew :app:connectedDebugAndroidTest` | **User only.** Requires a device/emulator the user controls. |
| Lint | `./gradlew :app:lintDebug` | User. |
| Clean | `./gradlew clean` | User. |

### Why anyone can compile but no one runs tests

Compile-only builds are a fast, free self-check: a missing
import, a typo in a class name, or a broken Hilt / KSP graph
shows up in seconds, and catching it before handing the change
back saves the user a round-trip. Tests are different: they
are the user's quality gate, and running them from inside the
harness would silently burn the user's test budget (CI
minutes, flaky-test retries, etc.) and would also let the
harness "approve" its own work.

## CI

There is no CI configuration in the repo today. The user owns
adding `.github/workflows/` or equivalent. Once CI exists, the
**code-reviewer** rein (not yet in the roster — add when CI lands)
will own PR review.
