# 001-quote-price-comparison: Quote Price Comparison

Lets a homeowner check a contractor's quoted price for a service against
recent local market prices, so they can judge whether the quote is fair.

## User Story 1

As a homeowner, I want to see how a quoted price compares to local market
prices for the same service, so I know whether the quote is fair.

### Acceptance criteria

- AC-1: A request with trade, serviceItem, market and quotedPrice returns 200
  with p25/p50/p75 estimatedPrice and a band of GREAT_PRICE | REASONABLE |
  OVERPRICED.
- AC-2: quotedPrice <= p25 is GREAT_PRICE; > p75 is OVERPRICED; otherwise
  REASONABLE.
- AC-3: Boundary values belong to the lower band (quotedPrice == p25 is
  GREAT_PRICE; quotedPrice == p75 is REASONABLE).
- AC-4: If fewer than 20 matching documents exist, no band is returned;
  response is 200 with sampleSize and reason INSUFFICIENT_DATA.
- AC-5: When the requested market has insufficient data, the query falls
  back to state, then national; the response states which level was used.
- AC-6: Only documents with postedTime within the last 12 months are
  counted.
- AC-7: An unknown trade or serviceItem returns 404, not an empty result.
- AC-8: Response payload never exceeds 1 MB; comparable-listing arrays are
  capped at 50 entries with a truncated flag.
