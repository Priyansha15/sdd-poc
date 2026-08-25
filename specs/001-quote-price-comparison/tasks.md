# 001-quote-price-comparison: Tasks

Ordered implementation checklist. Check items off (`[x]`) as completed;
don't batch-check at the end. Each task references the AC-n / design
section it implements. Package: `com.example.sddpoc.quote`.

- [ ] 1. `QuoteListing` JPA entity (`trade`, `serviceItem`, `market`,
      `stateCode`, `price`, `postedTime`) (design § Data model — `QuoteListing`; AC-1, AC-6)
- [ ] 2. `ServiceCatalog` JPA entity (`trade`, `serviceItem`, unique
      constraint) (design § Data model — `ServiceCatalog`; AC-7)
- [ ] 3. `QuoteListingRepository` with derived/`@Query` methods for the
      three fallback levels (by market, by stateCode, by trade+serviceItem
      only), each filtered to `postedTime >= now - 12 months` (design §
      Fallback resolution; AC-5, AC-6)
- [ ] 4. `ServiceCatalogRepository` with an `existsByTradeAndServiceItem`
      lookup (design § Data model — `ServiceCatalog`; AC-7)
- [ ] 5. `data.sql` seed data: a handful of `ServiceCatalog` rows, plus
      `QuoteListing` rows covering — a market with >=20 recent listings, a
      market with <20 but its state has >=20, a trade/serviceItem where
      even national is <20, and some listings older than 12 months (to
      prove they're excluded) (design § Data model; AC-4, AC-5, AC-6)
- [ ] 6. `QuoteComparisonRequest` (query-param DTO) with Bean Validation
      (`@NotBlank` on trade/serviceItem/market, `@Positive` on
      quotedPrice) (design § API contract; AC-1)
- [ ] 7. Response DTOs: `QuoteComparisonResponse`, `EstimatedPrice`
      (p25/p50/p75), `ComparableListing` (design § API contract; AC-1,
      AC-4, AC-8)
- [ ] 8. `ServiceNotFoundException` + mapping in `GlobalExceptionHandler`
      to `404` (design § Error handling; AC-7)
- [ ] 9. Percentile utility: linear interpolation over a sorted
      `List<BigDecimal>` (design § Trade-offs — percentile method; AC-2,
      AC-3)
- [ ] 10. `QuoteComparisonService`:
      - validate trade/serviceItem exist via `ServiceCatalogRepository`,
        else throw `ServiceNotFoundException` (AC-7)
      - run MARKET → STATE → NATIONAL fallback, stopping at first level
        with `sampleSize >= 20` (design § Fallback resolution; AC-5)
      - compute p25/p50/p75 and band (`<=p25` GREAT_PRICE, `>p75`
        OVERPRICED, else REASONABLE) when sample size is sufficient (AC-2,
        AC-3)
      - set `reason: INSUFFICIENT_DATA` and null band/estimatedPrice when
        even NATIONAL is `< 20` (AC-4)
      - cap `comparableListings` to 50, set `truncated` flag (AC-8)
- [ ] 11. `QuoteComparisonController`: `GET /api/quote-comparisons`,
      `@Valid` request binding, delegates to service (design § API
      contract; AC-1)
- [ ] 12. Tests, one per acceptance criterion:
      - AC-1: happy path returns 200 with p25/p50/p75 + band
      - AC-2: quotedPrice below/above band thresholds
      - AC-3: quotedPrice exactly at p25 and exactly at p75 (boundary)
      - AC-4: <20 matching documents → 200, no band, reason
        `INSUFFICIENT_DATA`
      - AC-5: market-insufficient-but-state-sufficient, and
        state-insufficient-but-national-sufficient, assert `matchedLevel`
      - AC-6: a listing older than 12 months is excluded from the count
        and percentiles
      - AC-7: unknown trade/serviceItem → 404
      - AC-8: >50 comparable listings → capped at 50 with `truncated: true`
