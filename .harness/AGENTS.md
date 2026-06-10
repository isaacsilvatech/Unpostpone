# AGENTS.md — Unpostpone Project Memory

This file is the index of project-wide rules and topic docs for the
Unpostpone team. **Read it first** at the start of every session —
both the orchestrator and every rein.

## Standing rules (apply to every agent)

1. **Build & test commands are the user's job.** Don't run
   `./gradlew` or `./gradlew test` from inside a rein session. Suggest
   the exact command in your report; the user runs it.
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
7. **Don't commit unless the user asks.** The orchestrator decides
   when to commit; reins never run `git commit` on their own.

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
