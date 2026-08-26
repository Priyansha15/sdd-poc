# 002-saved-contractors: Tasks

Package: `com.example.sddpoc.contractor`.

- [ ] 1. `SavedContractor` JPA entity (design § Data model; AC-1, AC-2)
- [ ] 2. `SavedContractorRepository` (Spring Data `JpaRepository`, default
      `findAllByOrderByCreatedAtDesc`, `existsByPhone`) (design § Data
      model; AC-2, AC-7)
- [ ] 3. Request DTO `SavedContractorRequest` with Bean Validation
      (`@NotBlank` name/phone/trade, `@Pattern` on phone, `@Pattern`
      safe-character allow-list on name/trade) (design § API contract;
      AC-5, AC-6, AC-8)
- [ ] 4. Response DTO `SavedContractorResponse` (design § API contract;
      AC-1, AC-2)
- [ ] 5. `SavedContractorNotFoundException` and
      `DuplicateContractorException` + mapping in `GlobalExceptionHandler`
      to `404`/`409` (design § Error handling; AC-4, AC-7)
- [ ] 6. `SavedContractorService`: create (checks `existsByPhone` first,
      sets `createdAt`), list (ordered), delete (throws if missing)
      (design § API contract; AC-1, AC-2, AC-3, AC-4, AC-7)
- [ ] 7. `SavedContractorController`: `POST`/`GET`/`DELETE
      /api/saved-contractors[/{id}]` (design § API contract; AC-1, AC-2,
      AC-3)
- [ ] 8. Tests, one per acceptance criterion:
      - AC-1: create returns 201 with generated id
      - AC-2: list returns most-recent-first
      - AC-3: delete removes it from a subsequent list
      - AC-4: delete of unknown id returns 404
      - AC-5: blank name/phone/trade returns 400
      - AC-6: malformed phone returns 400
      - AC-7: reusing an already-saved phone returns 409, no second row
        created
      - AC-8: a name/trade containing `<script>` (or other markup/control
        characters) returns 400
