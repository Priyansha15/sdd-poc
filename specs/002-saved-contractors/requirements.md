# 002-saved-contractors: Saved Contractors

Lets a homeowner save a contractor's contact details for later, so they
don't have to re-search for someone they already found and liked.

## User Story 1

As a homeowner, I want to save a contractor's name, phone number, and
trade, so I can look them up again later without re-searching.

### Acceptance criteria

- AC-1: A request with name, phone, and trade returns 201 with the
  created record, including a generated id.
- AC-2: Listing saved contractors returns 200 with all of them, most
  recently added first.
- AC-3: Removing a saved contractor by id returns 204 and it no longer
  appears in the list.
- AC-4: Removing an id that doesn't exist returns 404.
- AC-5: A request missing name, phone, or trade (or blank) returns 400.
- AC-6: A phone number that isn't plausibly a phone number (letters,
  empty, etc.) returns 400.
- AC-7: A request whose phone number matches an already-saved
  contractor's phone number is rejected as a duplicate (409), instead of
  creating a second entry.
