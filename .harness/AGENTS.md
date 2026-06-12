# AGENTS.md — Unpostpone Project Memory

This file is the index of project-wide rules and topic docs for the
Unpostpone team. **Read it first** at the start of every session —
both the orchestrator and every rein.

## Standing rules (apply to every agent)

1. **Test commands are the user's job — build commands are
   fair game.** Don't run `./gradlew :app:testDebugUnitTest`,
   `./gradlew :app:connectedDebugAndroidTest`, or any other test
   task (lint, connected checks, etc.) from inside any session —
   workers, orchestrator, doesn't matter. Tests are the user's
   signal, and running them from inside the harness would
   silently burn the user's test budget (CI minutes, flaky-test
   retries, etc.) and would also let the harness "approve" its
   own work. **Compile-only builds are fine for both the
   orchestrator and workers (reins)**: `./gradlew :app:compileDebugKotlin`
   and `./gradlew :app:assembleDebug` are encouraged as a
   self-check for missing imports, unresolved references, and
   KSP / Hilt graph errors before handing the change back.
   Workers should mention the build result in their report; the
   user runs the test commands.
2. **Use the version catalog.** Every new dependency goes into
   `gradle/libs.versions.toml`. No hardcoded
   `implementation("group:artifact:version")` strings in
   `app/build.gradle.kts`.
3. **Clean architecture is enforced.** Presentation package → ViewModel
   → use case → repository interface → repository impl → DAO/Entity.
   Don't introduce a new layer (MVI, Redux, custom abstractions)
   without the user's explicit consent.
4. **Every new screen gets a `Screen` entry** in
   `presentation/navigation/Screen.kt` and a NavGraph composable in
   `presentation/navigation/NavGraph.kt`. Forgetting one breaks
   routing silently.
5. **Hilt everywhere.** ViewModels use `@HiltViewModel` + `@Inject
   constructor`. Repository bindings go in `di/RepositoryModule`
   (or a feature-specific module).
6. **Manifest changes are sensitive.** A wrong `<service>` or
   `<receiver>` name silently breaks the corresponding feature. If
   you rename a class, update the manifest in the same change.
7. **Don't commit or push unless the user asks.** The
   orchestrator decides when to commit; reins never run
   `git commit` on their own. **No one in the harness ever
   runs `git push`** — pushing a branch, opening an MR, or
   triggering CI is an irreversible, network-visible action
   that the user must do themselves. If a rein or the
   orchestrator thinks a push is needed, it ends its report
   with the suggested command (e.g. `git push origin main`
   or `gh pr create`) and the user runs it.
8. **Every string change touches every locale.** When adding or
   editing any user-facing string, update **all** of
   `app/src/main/res/values*/strings.xml` (en = `values/`,
   pt-rBR = `values-pt-rBR/`, and any other locale present).
   Never leave a locale in English when the others are translated,
   and never leave a locale translated when `values/` is updated.
   If a translation is unknown for a locale, mirror the English
   value verbatim (don't delete the entry — Android falls back
   to `values/`, but an explicit copy is auditable).
9. **Almost no comments.** Code should speak for itself; the
   default is **no comment at all**. Add a `//` comment only when
   the reader genuinely cannot infer the intent from the code
   (a non-obvious platform quirk, an OEM-specific workaround,
   a deliberately-surprising value, or a future-must-happen
   contract). KDoc / multi-line `/* ... */` comments on classes
   or functions are **not allowed** unless they document a public
   API that external callers depend on. Every comment must be
   one or two short lines; multi-paragraph "explanations" go in
   the commit message or the changelog, not the source.
   - **Never** restate the function/property name in prose.
   - **Never** describe what the next 1-2 lines literally do.
   - **Never** leave section banners, author tags, or stale
     `// TODO: refactor` notes.
   When in doubt, delete the comment.

## Topic docs

| Topic | File | Owner |
|---|---|---|
| Code style, naming, package layout | [docs/code-standards.md](docs/code-standards.md) | orchestrator |
| Test policy & stack | [docs/test-policy.md](docs/test-policy.md) | tester |
| Build & dependency policy | [docs/build-policy.md](docs/build-policy.md) | orchestrator |

If a doc doesn't exist yet but you need it, create it under
`docs/<topic>.md` and link it from this index in the same change.

## Roster (canonical reference)

| Role | File |
|---|---|
| Orchestrator (Harness) | [.harness/agent.md](agent.md) |
| Android developer (generalist) | [.harness/reins/android-developer/agent.md](reins/android-developer/agent.md) |
| Tester | [.harness/reins/tester/agent.md](reins/tester/agent.md) |
| Blocker expert (accessibility + tempunlock) | [.harness/reins/blocker-expert/agent.md](reins/blocker-expert/agent.md) |
| Pomodoro expert (alarms + session state) | [.harness/reins/pomodoro-expert/agent.md](reins/pomodoro-expert/agent.md) |

> The daemon injects the team roster at runtime — don't hardcode
> reins in any `agent.md` body. The descriptions in each rein's
> frontmatter are what the orchestrator reads for delegation.

## Memory placement

- **Project-wide lesson** (true for every future agent on this
  project) → add to this `AGENTS.md` or a doc it links to.
- **Rein-specific lesson** (true only for one role) → add to
  `.harness/reins/<name>/MEMORY.md`.
- **Cross-project lesson** (true for every Mavis project) → agent
  memory at `~/.mavis/agents/general/memory/MEMORY.md`, sparingly.
- **User preference** (this user's habits, not Mavis's) → user
  memory; only if the conclusion doesn't change across projects.

When in doubt: narrowest scope first.

## Changelogs

Daily changelogs live in **`.harness/changelogs/YYYY-MM-DD.md`** —
one file per day, appended to. The bootstrap changelog is
[`.harness/changelogs/2026-06-10.md`](changelogs/2026-06-10.md).
Never create a `changelogs/` directory at the project root.

## File-creation scope (apply to every worker)

Reins and workers must only create or modify files inside the
following paths. Anything else is out of scope and the orchestrator
will revert it:

- `app/src/main/...` — production code, resources, manifest.
- `app/src/test/...` and `app/src/androidTest/...` — automated
  tests (only when the task explicitly authorizes them; the user
  said "no tests" for `pomodoro-ui-redesign` and the worker
  respected it).
- `.harness/changelogs/YYYY-MM-DD.md` — daily changelog (one
  file per day, append a new `## <task-id>` section).
- `.harness/memory/MEMORY.md` — project-wide lessons (only when
  the orchestrator asks you to record one).
- `.harness/docs/<topic>.md` — new topic docs (must be linked
  from the index table in this file in the same change).

Explicitly out of scope for workers:

- Anything at the project root (`/foo`, `/bar`) other than the
  paths above.
- `changelogs/` (without the `.harness/` prefix) — bootstrap
  left a stale reference here in an earlier draft; the canonical
  location is `.harness/changelogs/`.
- `.mavis/plans/`, `.mavis/scratchpads/` — engine internals; the
  orchestrator writes here, not the workers.
- Build artifacts, IDE files (`build/`, `.gradle/`, `.idea/`),
  local configs (`.env*`, `local.properties`) — never touch,
  never commit.
