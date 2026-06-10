# Project Memory — Unpostpone

This file holds project-wide lessons that are true for **every** agent
on this project (not just one rein). Rein-specific lessons live in
`.harness/reins/<name>/MEMORY.md`; this file is for cross-cutting
truths.

> Empty for now. Add entries only when a lesson is genuinely
> project-wide and durable (has come up at least twice, or was
> confirmed by the user on a real device). Don't dump one-off
> discoveries here.

---

## Workers create files only inside their allowed scope (2026-06-10)

The `pomodoro-ui-redesign` worker (cycle 1) created
`changelogs/2026-06-10.md` at the project root because
`AGENTS.md` (older draft) literally said
"`changelogs/YYYY-MM-DD.md`". Canonical location is
**`.harness/changelogs/YYYY-MM-DD.md`**.

Fix landed in `AGENTS.md` under "File-creation scope" — workers
must only touch `app/src/main/**`, `app/src/test/**`,
`app/src/androidTest/**`, `.harness/changelogs/**`,
`.harness/memory/MEMORY.md`, or `.harness/docs/**`. Anything else
at the project root is out of scope and the orchestrator will
revert it.

**Lesson for future worker prompts:** always include the file
path verbatim, with the `.harness/` prefix, when telling a worker
to append a changelog or write a project-memory entry. "Update
the changelog" is not enough — workers do not consult AGENTS.md
to disambiguate paths.
