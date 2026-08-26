# 001-quote-price-comparison: Design

## API contract

### `GET /api/quote-comparisons`

Read-only comparison — no state is mutated — so it's a `GET` with query
params rather than a `POST` with a body.

- Query params:
  - `trade` (string, required)
  - `serviceItem` (string, required)
  - `market` (string, required)
  - `quotedPrice` (decimal, required, > 0)

- Response (200) — sufficient data: 

  ```json
  {
    "trade": "plumbing",
    "serviceItem": "water-heater-installation",
    "market": "austin-tx",
    "quotedPrice": 850.00,
    "matchedLevel": "STATE",
    "sampleSize": 42,
    "estimatedPrice": { "p25": 700.00, "p50": 800.00, "p75": 950.00 },
    "band": "REASONABLE",
    "comparableListings": [
      { "market": "austin-tx", "price": 820.00, "postedTime": "2026-05-01T00:00:00Z" }
    ],
    "truncated": false
  }
  ```

  Traces: AC-1, AC-2, AC-3, AC-5, AC-6, AC-8

- Response (200) — insufficient data even after fallback:

  ```json
  {
    "trade": "plumbing",
    "serviceItem": "water-heater-installation",
    "market": "austin-tx",
    "quotedPrice": 850.00,
    "matchedLevel": "NATIONAL",
    "sampleSize": 7,
    "estimatedPrice": null,
    "band": null,
    "reason": "INSUFFICIENT_DATA",
    "comparableListings": [ ... ],
    "truncated": false
  }
  ```

  `matchedLevel` reports the last level attempted (always `NATIONAL` here,
  since that's the final fallback). Traces: AC-4, AC-5

- Error responses:
  - `400` — missing/blank `trade`, `serviceItem`, `market`, or a
    non-positive/non-numeric `quotedPrice`. Traces: AC-1 (implicit input
    validity)
  - `404` — `trade`/`serviceItem` combination is not a recognized service
    offering (see `ServiceCatalog` below). Traces: AC-7

Traces: AC-1, AC-2, AC-3, AC-4, AC-5, AC-6, AC-7, AC-8

## Data model

### `QuoteListing` (JPA entity, table `quote_listing`)

| field         | type          | notes                                   |
|---------------|---------------|------------------------------------------|
| `id`          | `Long`        | PK                                       |
| `trade`       | `String`      |                                           |
| `serviceItem` | `String`      |                                           |
| `market`      | `String`      | e.g. `"austin-tx"`                       |
| `stateCode`   | `String`      | e.g. `"tx"` — denormalized onto the row so state-level fallback needs no join |
| `price`       | `BigDecimal`  |                                           |
| `postedTime`  | `Instant`     | filtered to last 12 months (AC-6)        |

Indexed on `(trade, serviceItem, market, postedTime)` and
`(trade, serviceItem, stateCode, postedTime)` to support the fallback
queries below without a full scan.

Traces: AC-1, AC-5, AC-6

### `ServiceCatalog` (JPA entity, table `service_catalog`)

| field         | type     | notes                        |
|---------------|----------|------------------------------|
| `id`          | `Long`   | PK                           |
| `trade`       | `String` |                              |
| `serviceItem` | `String` |                              |

Unique on `(trade, serviceItem)`. This is the source of truth for "does
this trade/serviceItem exist at all" (AC-7), kept separate from
`QuoteListing` on purpose: a recognized service with too few *recent*
listings is `INSUFFICIENT_DATA` (AC-4), not `404`. Seeded via `data.sql`
for the POC, per the in-memory-H2 convention.

Traces: AC-7

### Fallback resolution (AC-5)

Given a request, resolve in order, stopping at the first level with
`sampleSize >= 20` documents (last 12 months, AC-6):

1. **MARKET** — `QuoteListing` rows matching `trade`, `serviceItem`,
   `market`.
2. **STATE** — rows matching `trade`, `serviceItem`, and the `stateCode`
   of the requested market. The state is read off any `QuoteListing` row
   for that `market` (regardless of trade/serviceItem); if the market is
   unrecognized (no such row exists at all), this level is skipped.
3. **NATIONAL** — rows matching `trade`, `serviceItem` only.

If `NATIONAL` also has `< 20` matching documents, respond with
`matchedLevel: "NATIONAL"`, `sampleSize` set to that count, and
`reason: "INSUFFICIENT_DATA"` (AC-4).

Traces: AC-4, AC-5, AC-6

## Error handling

- Bean Validation (`@NotBlank`, `@Positive`, etc.) on the request DTO;
  failures surface as `400` via the existing `GlobalExceptionHandler`.
- An unrecognized `trade`/`serviceItem` pair (not present in
  `ServiceCatalog`) throws a domain `ServiceNotFoundException`, mapped to
  `404` by `GlobalExceptionHandler`. This is checked *before* querying
  `QuoteListing`, so it's never confused with "recognized but sparse
  data" (AC-4).
- All other cases return `200`, differing only in whether `estimatedPrice`
  /`band` are populated or `reason: "INSUFFICIENT_DATA"` is set.

Traces: AC-4, AC-7

## Trade-offs

- **GET with query params, not POST with a body**: the request has no
  side effects and is naturally cacheable/idempotent; `quotedPrice` as a
  decimal query param is a minor ergonomic cost we accept for that.
- **Percentile method**: requirements don't specify how p25/p50/p75 are
  computed. Using linear interpolation on the sorted price list (the
  common "R-7"/Excel `PERCENTILE.INC`/numpy-default convention) for a
  deterministic, well-known definition. AC-2/AC-3's `<=`/`>` banding logic
  is independent of this choice.
- **State derived from existing listings, not a separate `Market`
  reference table**: keeps the data model to two tables for this POC.
  The cost: a real market with no listings at all (of any
  trade/service) can't be resolved to a state and skips straight to
  `NATIONAL`, even if a `STATE`-level match would otherwise exist. Judged
  acceptable given seeded POC data volumes; would need a dedicated
  `Market` table if this became a real concern.
- **1 MB response cap (AC-8)** is enforced structurally by capping
  `comparableListings` to 50 entries (with `truncated: true` when more
  exist) rather than measuring serialized byte size at runtime — capping
  the one unbounded field is sufficient since every other field is fixed
  size.
- **No caching of computed percentiles**: recomputed on every request.
  Fine at POC scale; would need revisiting if `QuoteListing` grows large
  or traffic increases.
