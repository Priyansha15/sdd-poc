package com.example.sddpoc.contractor;

import java.time.Instant;

public record SavedContractorResponse(
        Long id,
        String name,
        String phone,
        String trade,
        Instant createdAt) {

    static SavedContractorResponse from(SavedContractor contractor) {
        return new SavedContractorResponse(
                contractor.getId(),
                contractor.getName(),
                contractor.getPhone(),
                contractor.getTrade(),
                contractor.getCreatedAt());
    }
}
