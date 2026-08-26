# 002-saved-contractors: Design

## API contract

### `POST /api/saved-contractors`

- Request body: `{ "name": "...", "phone": "...", "trade": "..." }`
- Response (201): `{ "id": 1, "name": "...", "phone": "...", "trade": "...", "createdAt": "..." }`
- Error: `400` if `name`/`phone`/`trade` blank, or `phone` fails the format
  check; `409` if `phone` already belongs to a saved contractor.

Traces: AC-1, AC-5, AC-6, AC-7

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
| `name`      | `String`    | `@NotBlank`                      |
| `phone`     | `String`    | `@NotBlank`, `@Pattern` (digits, spaces, `+`, `-`, `()`) |
| `trade`     | `String`    | `@NotBlank`                      |
| `createdAt` | `Instant`   | set server-side on create        |

`phone` has a unique constraint — it's the natural key for excluding
duplicates (AC-7).

Standalone entity — no relationship to `QuoteListing` or
`ServiceCatalog` from `001-quote-price-comparison`; lives in its own
package (`com.example.sddpoc.contractor`) per package-by-feature.

Traces: AC-1, AC-2

## Error handling

- Bean Validation on the request DTO (`@NotBlank` on name/phone/trade,
  `@Pattern` on phone) → `400` via the existing `GlobalExceptionHandler`.
- Delete of a non-existent id throws a domain
  `SavedContractorNotFoundException` → `404` via the same handler.
- A create with a `phone` that already exists throws a domain
  `DuplicateContractorException` → `409` via the same handler, checked
  before the insert (not relying on catching a DB constraint violation).

Traces: AC-4, AC-5, AC-6, AC-7

## Trade-offs

- Phone format is checked with a permissive regex (`^[0-9+()\- ]{7,20}$`),
  not real phone-number validation (e.g. libphonenumber) — enough to
  reject obvious garbage (AC-6) without pulling in a dependency for a POC.
- No update (`PUT`/`PATCH`) endpoint — requirements only ask for
  save/list/remove; editing a saved entry isn't in scope.
