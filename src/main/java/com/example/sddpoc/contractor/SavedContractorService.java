package com.example.sddpoc.contractor;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SavedContractorService {

    private final SavedContractorRepository repository;

    public SavedContractor create(SavedContractorRequest request) {
        SavedContractor contractor = SavedContractor.builder()
                .name(request.name())
                .phone(request.phone())
                .trade(request.trade())
                .createdAt(Instant.now())
                .build();
        return repository.save(contractor);
    }

    public List<SavedContractor> list() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new SavedContractorNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
