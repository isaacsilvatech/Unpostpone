# Unpostpone — Internationalization (i18n) Strategy

The plan for making the app speak English, Portuguese (BR), and any
language we add later — without a code rewrite each time.

## 1. Goals

- **Zero hardcoded user-facing text** in Composables.
- **English is the source of truth** in `values/`. Translations live in
  `values-<bcp47>/` mirrors. To add a language, add a folder and a
  parallel set of resource files; no Kotlin code changes required.
- **Dynamic language switching** at runtime, persisted across launches.
- **Locale-aware formatting** for dates, numbers, and plurals.
- **RTL-ready** layout primitives even though we only ship LTR languages
  today — so adding Arabic, Hebrew, or Persian later is a translation
  drop, not a refactor.

## 2. Resource structure

```
res/
  values/                       ← English (source of truth)
    strings.xml                   common (app name, generic actions, errors)
    dashboard_strings.xml         dashboard
    goals_strings.xml             goals
    statistics_strings.xml        statistics
    settings_strings.xml          settings
    blocker_strings.xml           blocker
    onboarding_strings.xml        onboarding pager
    focus_reminder_strings.xml    reflection screen
    permissions_strings.xml       permission cards
    plurals.xml                   (in the relevant file alongside the string)
  values-pt-rBR/                ← Portuguese (Brazil)
    strings.xml
    dashboard_strings.xml
    goals_strings.xml
    ... (one mirror per file)
  values-ar/                    ← (future) Arabic — RTL will Just Work
  values-pt-rPT/                ← (future) European Portuguese
  xml/
    locales_config.xml          ← list of supported locales, for API 33+
```

### Why split per feature

- **Smaller merge conflicts** when two devs add strings to different
  features.
- **Easy to remove a feature** — delete one file instead of pickling
  through the monolithic `strings.xml`.
- **Translation memory reuse** — translators see the strings in feature
  context (e.g. "Add" in `goals_add_create` is unambiguous because the
  filename tells them which screen).

### Naming convention

`<feature>_<purpose>[_<modifier>]` — snake_case, no abbreviations, the
feature prefix is the resource filename minus `_strings.xml`.

| Prefix | Used for |
|---|---|
| `app_` | App-wide (tagline, app name) |
| `action_` | Reusable actions (Cancel, Save, Back) |
| `nav_` | Bottom navigation labels |
| `error_` | Error messages |
| `dashboard_` | Dashboard |
| `goals_` | Goals |
| `statistics_` | Statistics |
| `settings_` | Settings |
| `blocker_` | Blocker |
| `focus_reminder_` | Focus reminder / reflection |
| `onboarding_` | Onboarding pager |
| `perm_` | Permission cards (used inside onboarding) |

## 3. Translation files

Both English (`values/`) and Portuguese (`values-pt-rBR/`) are
complete today. 18 resource files per language. Use the same key
names, the same format-arg positions, the same plural categories.

### Quick verification checklist

For every string:
- ☐ No hardcoded text in any `.kt` file (except brand name "Unpostpone"
      inside the brand-mark Composable, which is a glyph, not UI copy).
- ☐ Uses `stringResource(R.string.x)` in Composables.
- ☐ Uses `context.getString(R.string.x)` in non-Composable code
      (services, ViewModels can use `Application`).
- ☐ Format args use `%1$s` / `%1$d` (positional) — never `%s` — so
      translators can reorder them.
- ☐ Strings with quantity use `<plurals>` (minutes, goals count).
- ☐ No sentence fragments in code: don't concat `"Hello, " + name`.
      Use a format string `"Hello, %1$s"`.

## 4. Language manager approach

A single source of truth: `LanguageManager`. The Manager owns a
`StateFlow<SupportedLanguage>`, persists to SharedPreferences, and
broadcasts the change via `AppCompatDelegate.setApplicationLocales()`.

```
                       ┌──────────────────┐
                       │ LanguageManager  │
                       │  (singleton)     │
                       └──────┬───────────┘
                              │  reads/writes
                              ▼
                       ┌──────────────────┐
                       │ SharedPreferences│  ◄── survives process death
                       │  (prefs)         │
                       └──────────────────┘

                       ┌──────────────────┐
                       │ LocalAppLocale   │  ◄── CompositionLocal for
                       │ (CompositionLocal)│      composables that need
                       └──────────────────┘      the value before recreation

                       ┌──────────────────┐
                       │ AppCompatDelegate│  ◄── triggers configuration
                       │ setApplication... │      change → activity recreate
                       └──────────────────┘
```

### Flow when the user changes language in Settings

1. User taps a row in `LanguagePickerDialog`.
2. `SettingsViewModel.setLanguage(SupportedLanguage.PortugueseBrazil)`
   calls `LanguageManager.setLanguage(...)`.
3. Manager:
   a. Writes the new tag to SharedPreferences.
   b. Updates the `StateFlow` (composables that observe it, like the
      picker's checkmark, update immediately).
   c. Calls `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("pt-BR"))`.
4. Android tears down the activity and rebuilds it with the new
   `Configuration`. `stringResource` calls now resolve to `values-pt-rBR/`.
5. `MainActivity.onCreate` reads the persisted locale on cold start and
   pushes it to `AppCompatDelegate` before the first composition, so
   there is **no flash of the default locale** for returning users.

### Flow on a cold start

1. `MainActivity.onCreate` runs.
2. `LanguageManagerImpl.applyPersistedToAppCompat()` reads
   `prefs[unpostpone.locale.tag]` and calls
   `AppCompatDelegate.setApplicationLocales(...)` synchronously, BEFORE
   `setContent { ... }`.
3. Compose first composition reads the right `values-<locale>/` from
   the first frame.

### `SupportedLanguage` enum

```kotlin
enum class SupportedLanguage(
    val tag: String,
    val javaLocale: Locale,
    val nativeName: String,
    val displayName: String,
) {
    SystemDefault("", Locale.getDefault(), "System default", "System default"),
    English("en", Locale.ENGLISH, "English", "English"),
    PortugueseBrazil("pt-BR", Locale("pt", "BR"), "Português (Brasil)", "Português (Brasil)"),
}
```

- `tag` is the BCP-47 tag the Android resource system uses.
- `javaLocale` is the `java.util.Locale` for `DateTimeFormatter` /
  `NumberFormat`.
- `nativeName` is shown in the picker (each language in its own
  script — "Português" not "Portuguese").
- `displayName` is the English fallback (unused in the picker; reserved
  for settings that need a canonical name).

### To add a new language

1. Add the entry to `SupportedLanguage` (with `nativeName` in the
   language's own script).
2. Add it to `SupportedLanguage.pickable`.
3. Create `res/values-<tag>/` with all 9 mirrors.
4. Add `<locale android:name="<tag>" />` to `res/xml/locales_config.xml`.
5. Translate.

That's it. No Kotlin changes, no app update required for the manifest
itself (the manifest change is shipped with the next release).

## 5. Refactoring examples

### Before (dashboard date — hardcoded Portuguese pattern)

```kotlin
val today = remember {
    SimpleDateFormat("EEEE, dd 'de' MMMM", Locale.forLanguageTag("pt-BR"))
        .format(Date())
        .replaceFirstChar { it.uppercase() }
}
```

### After (locale-aware long date)

```kotlin
val locale = AppLocale.formatting  // reactive CompositionLocal read
val today = remember(locale) {
    DateTimeFormatter
        .ofLocalizedDate(FormatStyle.FULL)
        .withLocale(locale)
        .withZone(ZoneId.systemDefault())
        .format(Instant.now())
}
```

`FormatStyle.FULL` gives:
- en-US: "Sunday, June 7, 2026"
- pt-BR: "domingo, 7 de junho de 2026"
- (future ar): "الأحد، 7 يونيو 2026"

### Before (string concat in a card)

```kotlin
Text("Meta: ${goal.targetMinutes} min", ...)
```

### After (positional format args)

```xml
<!-- goals_strings.xml -->
<string name="goals_target_minutes">Target: %1$d min</string>
<!-- values-pt-rBR/goals_strings.xml -->
<string name="goals_target_minutes">Meta: %1$d min</string>
```

```kotlin
Text(stringResource(R.string.goals_target_minutes, goal.targetMinutes), ...)
```

### Before (conjugation by if-else for plurality)

```kotlin
val label = if (n == 1) "$n goal" else "$n goals"
```

### After (plurals resource)

```xml
<plurals name="dashboard_goals_count">
    <item quantity="one">%d goal</item>
    <item quantity="other">%d goals</item>
</plurals>
<!-- pt-rBR -->
<plurals name="dashboard_goals_count">
    <item quantity="one">%d meta</item>
    <item quantity="other">%d metas</item>
</plurals>
```

```kotlin
val label = pluralStringResource(R.plurals.dashboard_goals_count, n, n)
```

Plurals are also how Arabic handles the dual form (`quantity="two"`),
Russian's three-way plural (`one/few/many`), and Polish's complex
categories — one declaration, free across all languages.

### Before (icon button with hardcoded contentDescription)

```kotlin
IconButton(onClick = ...) {
    Icon(Icons.Default.Add, contentDescription = "Nova Meta")
}
```

### After (string resource, RTL-safe)

```kotlin
IconButton(onClick = ...) {
    Icon(
        Icons.Default.Add,
        contentDescription = stringResource(R.string.goals_add_cd),
    )
}
```

## 6. Locale-aware formatting helpers

Use these from `androidx.compose.ui.res` and `java.time`:

| Need | API |
|---|---|
| A string with format args | `stringResource(R.string.x, arg1, arg2)` |
| A pluralized string | `pluralStringResource(R.plurals.x, quantity, quantity, ...args)` |
| A `String` outside a Composable | `context.getString(R.string.x, ...args)` |
| A date (long) | `DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)` |
| A time | `DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)` |
| A number | `NumberFormat.getInstance(locale).format(n)` |
| A currency | `NumberFormat.getCurrencyInstance(locale).format(amount)` |
| The current locale | `AppLocale.formatting` (a `Locale` reactive to the choice) |

`AppLocale.formatting` is a `@Composable` extension on
`LocalAppLocale.current`. Outside Compose (services, ViewModels that
read at construction), inject `LanguageManager` and read
`languageManager.current.value.javaLocale`.

## 7. RTL — what we did today, what to do when Arabic lands

### What we already do

- `Modifier.padding(start = ..., end = ...)` — never `left`/`right`.
- `Arrangement.Start` / `Arrangement.End` — never `Left` / `Right`.
- `Alignment.Start` / `Alignment.End`.
- `Modifier.align(Alignment.Start)`.
- `android:supportsRtl="true"` in the manifest (already there).
- `Icons.AutoMirrored.Filled.ArrowBack` instead of `Icons.Filled.ArrowBack`
  (the back arrow mirrors in RTL).
- `TextAlign.Start` / `TextAlign.End` — never `Left` / `Right`.

### What to add when shipping Arabic

1. Create `res/values-ar/...` mirrors.
2. Add `<locale android:name="ar" />` to `locales_config.xml`.
3. Open every `Card` / `Row` and audit hardcoded icon directions
   (e.g. `Icons.Default.ArrowForward` should be `AutoMirrored`).
4. Add a `bidi` formatter for any string mixing Latin and Arabic script
   (none of our current screens do, so we're fine).
5. Manual QA pass on every screen with the device in RTL force mode:
   Settings → Developer options → Force RTL layout direction.

### Test pseudo-locale

Add `res/values-ar-rXB/` (RTL bidi pseudo-locale) and `res/values-en-rXA/`
(LTR pseudo-locale) to preview how every string looks when its text
content is replaced with placeholder accented strings. The Android
emulator has a one-click toggle in Developer options.

## 8. Best practices for future localization

1. **Source of truth is English, in `values/`.** Never edit
   `values-en/` — it doesn't exist, because the un-qualified folder IS
   English.
2. **No `Locale.getDefault()` in code that runs before the first
   composition.** Use `AppLocale.formatting` (or inject
   `LanguageManager` outside Compose).
3. **Format args are positional.** `%1$s`, `%2$d`. Never bare `%s`.
4. **Plurals for every count.** Even for languages that have only
   one/two forms, the system will pick the right category. For Russian,
   Polish, and Arabic the difference is structural.
5. **No sentence fragments in code.** `"Save"` is a fragment;
   `"Save goal"` is the right key (`goals_save`).
6. **Brand names are not translated.** "Unpostpone" stays "Unpostpone"
   in every language. The `app_name` resource uses the same value in
   every locale.
7. **Date / time / number formatting via `Locale`.** Never
   `"$hours:$minutes"` — use a formatter.
8. **Strings in services / accessibility descriptions are not optional.**
   The accessibility service description
   (`accessibility_service_description`) is read aloud by TalkBack — it
   MUST be localized.
9. **Test with at least 2 locales before merging.** PT-BR + en-US is
   the cheap case. If you change a string in English, the PT-BR
   translator sees it on the next export. If you change layout
   assumptions, force RTL and re-test.
10. **Locale changes are async at the platform level.** The
    `AppCompatDelegate` call is synchronous; the activity recreation
    is what makes the new strings visible. Don't rely on reading the
    new locale inside the same composition that triggered the change —
    wait for the recreation.

## 9. Files added or changed in this pass

| File | Status | Role |
|---|---|---|
| `gradle/libs.versions.toml` | updated | added `appcompat 1.7.0` |
| `app/build.gradle.kts` | updated | added `implementation(libs.androidx.appcompat)` |
| `res/values/strings.xml` | rewritten | English common strings |
| `res/values/dashboard_strings.xml` | **new** | English dashboard |
| `res/values/goals_strings.xml` | **new** | English goals |
| `res/values/statistics_strings.xml` | **new** | English statistics |
| `res/values/settings_strings.xml` | **new** | English settings |
| `res/values/blocker_strings.xml` | **new** | English blocker |
| `res/values/onboarding_strings.xml` | **new** | English onboarding |
| `res/values/focus_reminder_strings.xml` | **new** | English focus reminder |
| `res/values/permissions_strings.xml` | **new** | English permission cards |
| `res/values-pt-rBR/*.xml` (×9) | **new** | Portuguese mirrors |
| `res/xml/locales_config.xml` | **new** | per-app language list (API 33+) |
| `AndroidManifest.xml` | updated | `android:localeConfig` reference |
| `core/locale/SupportedLanguage.kt` | **new** | enum + `LocalAppLocale` |
| `core/locale/LanguageManager.kt` | **new** | interface |
| `core/locale/WithAppLocale.kt` | **new** | composable wrapper for `LocalAppLocale` |
| `data/locale/LanguageManagerImpl.kt` | **new** | SharedPreferences + `AppCompatDelegate` |
| `di/LocaleModule.kt` | **new** | Hilt `@Binds` for the Manager |
| `MainActivity.kt` | updated | calls `applyPersistedToAppCompat()`; wraps in `WithAppLocale` |
| `presentation/settings/LanguagePickerDialog.kt` | **new** | the picker UI |
| `presentation/settings/SettingsViewModel.kt` | rewritten | exposes `currentLanguage` + `setLanguage` |
| `presentation/settings/SettingsScreen.kt` | updated | uses `stringResource` + adds language row |
| `presentation/dashboard/DashboardScreen.kt` | updated | `stringResource` + locale-aware date |
| `presentation/goals/GoalsScreen.kt` | updated | `stringResource` |
| `presentation/statistics/StatisticsScreen.kt` | updated | `stringResource` |
| `presentation/blocker/BlockerScreen.kt` | updated | `stringResource` |
| `presentation/onboarding/OnboardingScreen.kt` | updated | `stringResource` |
| `presentation/focusreminder/FocusReminderScreen.kt` | updated | `stringResource` (incl. message rotation) |
| `presentation/splash/SplashScreen.kt` | updated | `stringResource` for tagline |
