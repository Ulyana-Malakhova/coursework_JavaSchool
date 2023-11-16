package org.example.documents.repository;

import org.example.documents.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentsRepository extends JpaRepository<Document,Long> {
}
