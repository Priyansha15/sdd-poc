# 002-saved-contractors: Design

## API contract

### `POST /api/saved-contractors`

- Request body: `{ "name": "...", "phone": "...", "trade": "..." }`
- Response (201): `{ "id": 1, "name": "...", "phone": "...", "trade": "...", "createdAt": "..." }`
- Error: `400` if `name`/`phone`/`trade` blank, `name`/`trade` contains
  markup/control characters, or `phone` fails the format check; `409` if
  `phone` already belongs to a saved contractor.

Traces: AC-1, AC-5, AC-6, AC-7, AC-8

### `GET /api/saved-contractors`

- Response (200): array of the same shape as above, ordered by
  `createdAt` descending.

Traces: AC-2

### `DELETE /api/saved-contractors/{id}`

- Response: `204` on success, `404` if `id` doesn't exist.

Traces: AC-3, AC-4

## Data model

### `SavedContractor` (JPA entity, table `saved_contractor`)

| field       | type        | notes                          |
|-------------|-------------|----------------------------------|
| `id`        | `Long`      | PK, generated                    |
| `name`      | `String`    | `@NotBlank`, `@Pattern` (safe-character allow-list, AC-8) |
| `phone`     | `String`    | `@NotBlank`, `@Pattern` (digits, spaces, `+`, `-`, `()`) |
| `trade`     | `String`    | `@NotBlank`, `@Pattern` (safe-character allow-list, AC-8) |
| `createdAt` | `Instant`   | set server-side on create        |

`phone` has a unique constraint — it's the natural key for excluding
duplicates (AC-7).

Standalone entity — no relationship to `QuoteListing` or
`ServiceCatalog` from `001-quote-price-comparison`; lives in its own
package (`com.example.sddpoc.contractor`) per package-by-feature.

Traces: AC-1, AC-2

## Error handling

- Bean Validation on the request DTO (`@NotBlank` on name/phone/trade,
  `@Pattern` on phone, `@Pattern` allow-list on name/trade) → `400` via
  the existing `GlobalExceptionHandler`.
- Delete of a non-existent id throws a domain
  `SavedContractorNotFoundException` → `404` via the same handler.
- A create with a `phone` that already exists throws a domain
  `DuplicateContractorException` → `409` via the same handler, checked
  before the insert (not relying on catching a DB constraint violation).

Traces: AC-4, AC-5, AC-6, AC-7, AC-8

## Trade-offs

- Phone format is checked with a permissive regex (`^[0-9+()\- ]{7,20}$`),
  not real phone-number validation (e.g. libphonenumber) — enough to
  reject obvious garbage (AC-6) without pulling in a dependency for a POC.
- No update (`PUT`/`PATCH`) endpoint — requirements only ask for
  save/list/remove; editing a saved entry isn't in scope.
- **AC-8's allow-list (`^[\p{L}0-9 .,'&-]{1,100}$` — letters, digits,
  spaces, and a few punctuation marks) is input validation, not a
  substitute for output encoding.** It's defense-in-depth: reject the
  obviously-dangerous case at the boundary (e.g. `<script>`), on the
  assumption that some future consumer of this data might render it in a
  browser without escaping it. It doesn't replace using an
  auto-escaping template engine or `textContent`-style rendering
  wherever this data is actually displayed — that's still required at
  the render site.
- **This is not a SQL-injection concern**: `SavedContractorRepository`
  is a Spring Data JPA repository — every query is a parameterized
  `PreparedStatement` under the hood, with no string-concatenated SQL
  anywhere in this feature. AC-8 exists purely for the XSS/stored-markup
  risk, not because raw input reaches a query.
