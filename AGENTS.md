# Unpostpone — Agent Guide

Android focus / app-blocking app. Kotlin + Jetpack Compose + Hilt + Room, single Gradle module (`:app`).

## Quick reference

- Application id / namespace: `com.unpostpone.app`
- minSdk 29, target/compileSdk 36 (minor api 1), Java 11, Kotlin 2.2.10, AGP 9.2.1
- Gradle wrapper: 9.4.1 (`./gradlew`)
- Daemon JVM toolchain: 21
- Gradle config cache is on (`gradle.properties`)
- JDK source/target is 11 even though the toolchain is 21

## Build & verify

Run from the repo root. Use the wrapper, not a system `gradle`.

```bash
./gradlew :app:compileDebugKotlin      # syntax/typecheck only — no APK
./gradlew :app:kspDebugKotlin          # run KSP (Hilt + Room codegen)
./gradlew :app:test                    # JVM unit tests (src/test)
./gradlew :app:lintDebug               # Android Lint
./gradlew :app:lintReport              # full HTML lint report
./gradlew assembleDebug                # build the debug APK (only when needed)
./gradlew installDebug                 # install on a connected device/emulator
./gradlew :app:connectedDebugAndroidTest # instrumented tests (src/androidTest, needs device)
./gradlew :app:dependencies            # resolved dependency tree
```

## Commit style

Use [Conventional Commits](https://www.conventionalcommits.org/) — short, single-line, lowercase, no trailing period. Match the scope to the affected area (e.g. `dashboard`, `pomodoro`, `blocked-apps`, `settings`, `onboarding`).

```text
feat(dashboard): confirm dialog before disabling focus protection
fix(blocked-apps): hide uninstalled apps from the list
refactor(pomodoro): redesign view
```

Only commit when explicitly asked. Before committing, inspect `git status` and `git diff --staged`; stage only intended files and never commit secrets.

```

## Project layout (`:app` only — no other modules)

`app/src/main/java/com/unpostpone/app/`

- `MainActivity.kt` — single Activity, hosts the Compose `NavGraph`. Resolves start destination from intent extras (blocked-app deep link, pomodoro open, or onboarding gate).
- `UnpostponeApplication.kt` — `@HiltAndroidApp`; on first run with the accessibility service already enabled, auto-enables blocking and seeds the default blocked apps.
- `core/` — `tempunlock/`, `theme/`, `util/` (constants, date/format helpers, accessibility-service utilities, notification permission helper).
- `data/` — Room (`data/local/` with `AppDatabase`, DAOs, entities), data sources (`data/datasource/`), repository implementations (`data/repository/`).
- `domain/` — pure-Kotlin models, repository interfaces, use cases under `usecase/{blockedapp,goal,pomodoro,statistics}/`.
- `di/` — Hilt modules: `AppModule`, `DatabaseModule`, `RepositoryModule`, `PomodoroModule`.
- `presentation/` — one subpackage per screen (`dashboard`, `goals`, `pomodoro`, `settings`, `statistics`, `blocker`, `reblock`, `focusreminder`, `onboarding`); navigation lives in `presentation/navigation/`.
- `service/` — background components declared in `AndroidManifest.xml`:
  - `accessibility/UnpostponeAccessibilityService` — detects foreground app and triggers the blocker UI.
  - `pomodoro/PomodoroTimerService` (foreground, `specialUse`) + `PomodoroActionReceiver` + `PomodoroAlarmReceiver` + `PomodoroNotificationHelper` + `PomodoroRingtonePlayer`.
  - `tempunlock/TemporaryUnlockService` (foreground, `specialUse`) — temporary app unlock window.

Strings are split by feature: `res/values/<feature>_strings.xml` plus a `values-pt-rBR/` mirror. `Screen.kt` is the single source of truth for route names.

## Conventions & gotchas

- **No code comments unless strictly necessary.** Do not add explanatory comments, KDoc, or banner comments to code you write or edit. Only add a comment when the code is genuinely non-obvious and no name or structure can clarify it. Never narrate what the code does.
- **Compose + Material3**, theme wrapper is `com.unpostpone.app.ui.theme.UnpostponeTheme` (used by `MainActivity`). Theme mode (System/Light/Dark) is a Flow exposed via `ThemePreferences`.
- **KSP, not kapt.** Both Hilt and Room use KSP — `ksp(libs.hilt.compiler)`, `ksp(libs.room.compiler)`.
- **Room migrations are handwritten SQL.** `AppDatabase` is at version 3; bump the version and add a `MIGRATION_n_m` constant in `AppDatabase` (see existing `MIGRATION_1_2` and `MIGRATION_2_3`). `exportSchema = false` — do not enable without also configuring a schema location.
- **Lint:** `app/lint.xml` suppresses only `MissingDefaultResource` (for the `dark_background` color used exclusively by `values-night`). Every suppression must explain why — keep the file small and commented. **Do not run `:app:lintDebug` as part of a default verify cycle** — only run it when the user asks, or when a change is specifically a lint/resource fix.
- **Release build:** `buildTypes.release` sets `optimization.enable = false`. Don't "fix" it without a reason.
- **`local.properties` is gitignored** and points at the Windows SDK path (`C:\Users\isaac\AppData\Local\Android\Sdk`). Don't commit it.
- **Configuration cache is enabled.** Avoid `BuildService` patterns that break it; if a build suddenly fails after a Gradle/AGP bump, try `./gradlew --no-configuration-cache` to bisect.
- **Accessibility + Usage-Stats permissions are core to the app.** The onboarding screen deep-links the user to `ACTION_ACCESSIBILITY_SETTINGS`, `ACTION_USAGE_ACCESS_SETTINGS`, and `ACTION_APP_NOTIFICATION_SETTINGS`. Tests that mock these paths must preserve the intent extras `EXTRA_BLOCKED_PACKAGE` and `EXTRA_FROM_UNLOCK_NOTIFICATION` on `MainActivity`.
- **Notification actions** use the custom action namespace `com.unpostpone.app.action.POMODORO_*` (see `PomodoroActionReceiver` in the manifest). Add new actions there, not in a new namespace.
- **Fullscreen overtime activity** (`PomodoroOvertimeActivity`) is `singleTask` with `showOnLockScreen="true"` and `taskAffinity=""` — it intentionally has no back-stack history.
- **Special-use foreground services** must keep the matching `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` property in the manifest. Don't remove the `<property>` lines for `PomodoroTimerService` or `TemporaryUnlockService`; Play Store review looks for them.

## Resources

- App icons live in `app/src/main/res/mipmap-*` and `assets/icon-*.png` (the latter are the source designs — the in-app mipmaps are the generated webps).
- `keepRules/` — ProGuard/R8 keep rules; check it before stripping unused code in release builds.
- `res/xml/accessibility_service_config.xml` — accessibility service capabilities (event types, package names, flags). Editing it changes what the service observes; coordinate with `UnpostponeAccessibilityService`.
