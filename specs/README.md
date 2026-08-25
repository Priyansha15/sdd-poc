# specs/

One folder per feature, numbered in implementation order: `NNN-short-name/`.

Each feature folder contains, in the order they're written:

- `requirements.md` — user stories + numbered acceptance criteria (`AC-1`, `AC-2`, ...)
- `design.md` — API contract, data model, error handling; traces back to `AC-n`
- `tasks.md` — ordered implementation checklist; each task references `AC-n`

See `../CLAUDE.md` for the full workflow. Start a new feature by copying
`_template/`. `001-book-catalog/` is a complete worked example.
