# sdd-poc

Proof-of-concept Spring Boot project for practicing **Spec-Driven Development (SDD)**:
every feature is specified in full *before* any code is written, and the code is
implemented strictly off that spec rather than off ad-hoc conversation.

## Workflow for every feature

Each feature gets its own numbered folder under `specs/`, e.g. `specs/002-loans/`.
Copy `specs/_template/` to start one. Work through the three documents **in order**;
do not start `tasks.md` until `design.md` is settled, and do not write code until
`tasks.md` exists.

1. **`requirements.md`** — user stories and acceptance criteria only. No mention of
   classes, endpoints, or tables. Each acceptance criterion gets a stable ID
   (`AC-1`, `AC-2`, ...) so later documents and tests can reference it.
2. **`design.md`** — the technical approach that satisfies `requirements.md`: API
   contract (routes, request/response shapes, status codes), data model, error
   handling, and any trade-offs. Every design decision should be traceable back to
   an `AC-n`.
3. **`tasks.md`** — an ordered, checkable implementation checklist derived from
   `design.md`. Each task references the `AC-n` / design section it implements.
   Check tasks off (`[x]`) as they're completed; don't batch-check at the end.

Then implement:

4. Write code task-by-task, in the order given in `tasks.md`.
5. Write tests that assert the acceptance criteria (`AC-n`) from `requirements.md`,
   not just "it compiles" — name tests after the criterion where practical.
6. If implementation reveals the spec was wrong or incomplete, **fix the spec
   first**, then continue implementing. The spec is the source of truth; the code
   should never silently diverge from it.

## Example

`specs/001-book-catalog/` is a fully worked example (requirements → design →
tasks) with the matching implementation in `src/main/java/com/example/sddpoc/book/`
and tests in `src/test/java/com/example/sddpoc/book/`. Use it as the template for
formatting and level of detail.

## Conventions

- Java 21, Spring Boot 4.1.1, Maven.
- In-memory H2 for persistence (POC only — no migrations tooling needed yet).
- Bean Validation (`jakarta.validation`) on request DTOs; validation failures
  surface as `400` via `GlobalExceptionHandler`.
- Domain-not-found conditions surface as `404` via the same handler.
- Package-by-feature (`book/`, next feature gets its own top-level package), not
  package-by-layer.