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

There is no top-level `check`/CI pipeline wired up. After non-trivial changes, run in this order: `lintDebug` → `:app:test` → `assembleDebug`.

## WSL environment

The repo is developed on WSL. AGP 9.x inside WSL **cannot** use the Windows SDK at `C:\Users\Isaac\AppData\Local\Android\Sdk` (it requires a Linux `aapt` binary, not `aapt.exe`). The build is wired against a native Linux SDK at `/opt/android-sdk`.

**Required env vars before any `./gradlew` invocation:**

```bash
export JAVA_HOME=/usr/lib/jvm/java-1.21.0-openjdk-amd64   # full JDK 21 (has javac)
export ANDROID_HOME=/opt/android-sdk
export PATH=$JAVA_HOME/bin:$PATH
```

Note: `/usr/lib/jvm/java-21-openjdk-amd64` is JRE headless only (no `javac`) and will fail with `JAVA_HOME is set to an invalid directory`. Always use the `java-1.21.0-openjdk-amd64` path.

If `JAVA_HOME` or `ANDROID_HOME` are missing in a fresh shell, persist them in `~/.bashrc` (or `~/.zshrc`):

```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-1.21.0-openjdk-amd64' >> ~/.bashrc
echo 'export ANDROID_HOME=/opt/android-sdk' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
```

**If `/opt/android-sdk` is missing** (fresh WSL install or wiped `/opt`), recreate it:

```bash
echo "123456" | sudo -S apt-get install -y unzip
mkdir -p /tmp && cd /tmp
curl -sSLO https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip
echo "123456" | sudo -S mkdir -p /opt/android-sdk
sudo chown -R "$USER":"$USER" /opt/android-sdk
unzip -q commandlinetools-linux-13114758_latest.zip -d /opt/android-sdk/cmdline-tools/
mv /opt/android-sdk/cmdline-tools/cmdline-tools /opt/android-sdk/cmdline-tools/latest
rm commandlinetools-linux-13114758_latest.zip
export JAVA_HOME=/usr/lib/jvm/java-1.21.0-openjdk-amd64
yes | /opt/android-sdk/cmdline-tools/latest/bin/sdkmanager --licenses
/opt/android-sdk/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-36" "build-tools;36.1.0"
```

On first build, AGP will auto-install any additional build-tools/platforms it needs into `/opt/android-sdk`.

**Quick environment health check** (run if a build fails with "SDK location not found" or "Build Tools revision X is corrupted"):

```bash
ls $ANDROID_HOME/build-tools/        # expect 36.0.0, 36.1.0
ls $ANDROID_HOME/build-tools/36.1.0/aapt 2>&1  # must exist (no .exe)
$JAVA_HOME/bin/javac -version         # must print javac 21.x
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
- **Room migrations are handwritten SQL.** `AppDatabase` is at version 2; bump the version and add a `MIGRATION_n_m` constant in `AppDatabase` (see existing `MIGRATION_1_2`). `exportSchema = false` — do not enable without also configuring a schema location.
- **Lint:** `app/lint.xml` suppresses only `MissingDefaultResource` (for the `dark_background` color used exclusively by `values-night`). Every suppression must explain why — keep the file small and commented.
- **Release build:** `buildTypes.release` sets `optimization.enable = false`. Don't "fix" it without a reason.
- **`local.properties` is gitignored** and points at the WSL Linux SDK path (`/opt/android-sdk`). The repo is developed on WSL/Windows; do not switch it back to the Windows SDK path (`C:\Users\Isaac\AppData\Local\Android\Sdk`) — AGP 9.x inside WSL refuses Windows binaries because it looks for `aapt` (no extension) but the Windows SDK only has `aapt.exe`. Don't commit it.
- **Configuration cache is enabled.** Avoid `BuildService` patterns that break it; if a build suddenly fails after a Gradle/AGP bump, try `./gradlew --no-configuration-cache` to bisect.
- **Accessibility + Usage-Stats permissions are core to the app.** The onboarding screen deep-links the user to `ACTION_ACCESSIBILITY_SETTINGS`, `ACTION_USAGE_ACCESS_SETTINGS`, and `ACTION_APP_NOTIFICATION_SETTINGS`. Tests that mock these paths must preserve the intent extras `EXTRA_BLOCKED_PACKAGE` and `EXTRA_FROM_UNLOCK_NOTIFICATION` on `MainActivity`.
- **Notification actions** use the custom action namespace `com.unpostpone.app.action.POMODORO_*` (see `PomodoroActionReceiver` in the manifest). Add new actions there, not in a new namespace.
- **Fullscreen session-complete activity** (`PomodoroSessionCompleteActivity`) is `singleTask` with `showOnLockScreen="true"` and `taskAffinity=""` — it intentionally has no back-stack history.
- **Special-use foreground services** must keep the matching `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` property in the manifest. Don't remove the `<property>` lines for `PomodoroTimerService` or `TemporaryUnlockService`; Play Store review looks for them.

## Tests

`src/test/` is currently just the JUnit 4 example (`ExampleUnitTest.kt`); `src/androidTest/` likewise is the `ExampleInstrumentedTest` scaffold. Real testing dependencies are already wired in `app/build.gradle.kts` and `libs.versions.toml`:

- JVM: JUnit 4, `kotlinx-coroutines-test`, `room-testing`, Robolectric, `androidx.test:core`, `androidx.test.ext:junit`.
- Instrumented: `androidx.compose.ui:ui-test-junit4`, Espresso core, `androidx.test.ext:junit` (runner is `androidx.test.runner.AndroidJUnitRunner`, already set in `defaultConfig`).

Prefer Robolectric for ViewModels/repositories that touch Room or `Context`; reserve `connectedAndroidTest` for things that genuinely need a device (accessibility service, foreground services, notifications).

## Resources

- App icons live in `app/src/main/res/mipmap-*` and `assets/icon-*.png` (the latter are the source designs — the in-app mipmaps are the generated webps).
- `keepRules/` — ProGuard/R8 keep rules; check it before stripping unused code in release builds.
- `res/xml/accessibility_service_config.xml` — accessibility service capabilities (event types, package names, flags). Editing it changes what the service observes; coordinate with `UnpostponeAccessibilityService`.
