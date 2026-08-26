package com.example.sddpoc.contractor;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedContractorRepository extends JpaRepository<SavedContractor, Long> {

    List<SavedContractor> findAllByOrderByCreatedAtDesc();
}
