package com.adam.docvault.document.repository;
import com.adam.docvault.document.entity.Document;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Optional<Document> findByIdAndOwnerId(UUID id, UUID userId);
}
