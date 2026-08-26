# 002-saved-contractors: Tasks

Package: `com.example.sddpoc.contractor`.

- [x] 1. `SavedContractor` JPA entity (design § Data model; AC-1, AC-2)
- [x] 2. `SavedContractorRepository` (Spring Data `JpaRepository`, default
      `findAllByOrderByCreatedAtDesc`) (design § Data model; AC-2)
- [x] 3. Request DTO `SavedContractorRequest` with Bean Validation
      (`@NotBlank` name/phone/trade, `@Pattern` on phone) (design § API
      contract; AC-5, AC-6)
- [x] 4. Response DTO `SavedContractorResponse` (design § API contract;
      AC-1, AC-2)
- [x] 5. `SavedContractorNotFoundException` + mapping in
      `GlobalExceptionHandler` to `404` (design § Error handling; AC-4)
- [x] 6. `SavedContractorService`: create (sets `createdAt`), list
      (ordered), delete (throws if missing) (design § API contract; AC-1,
      AC-2, AC-3, AC-4)
- [x] 7. `SavedContractorController`: `POST`/`GET`/`DELETE
      /api/saved-contractors[/{id}]` (design § API contract; AC-1, AC-2,
      AC-3)
- [x] 8. Tests, one per acceptance criterion:
      - AC-1: create returns 201 with generated id
      - AC-2: list returns most-recent-first
      - AC-3: delete removes it from a subsequent list
      - AC-4: delete of unknown id returns 404
      - AC-5: blank name/phone/trade returns 400
      - AC-6: malformed phone returns 400
