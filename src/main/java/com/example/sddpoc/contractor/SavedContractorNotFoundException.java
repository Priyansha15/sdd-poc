package com.example.sddpoc.contractor;

public class SavedContractorNotFoundException extends RuntimeException {

    public SavedContractorNotFoundException(Long id) {
        super("Saved contractor not found: " + id);
    }
}
