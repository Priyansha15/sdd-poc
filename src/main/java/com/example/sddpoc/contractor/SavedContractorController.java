package com.example.sddpoc.contractor;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saved-contractors")
@RequiredArgsConstructor
public class SavedContractorController {

    private final SavedContractorService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavedContractorResponse create(@Valid @RequestBody SavedContractorRequest request) {
        return SavedContractorResponse.from(service.create(request));
    }

    @GetMapping
    public List<SavedContractorResponse> list() {
        return service.list().stream().map(SavedContractorResponse::from).toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
