package com.example.sddpoc.contractor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SavedContractorRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "^[0-9+()\\- ]{7,20}$") String phone,
        @NotBlank String trade) {
}
