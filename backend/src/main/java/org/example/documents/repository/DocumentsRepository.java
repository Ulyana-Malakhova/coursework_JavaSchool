package org.example.documents.repository;

import org.example.documents.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentsRepository extends JpaRepository<Document,Long> {
    @Modifying
    @Query("UPDATE Document d SET d.state = :state WHERE d.id =:id")
    void updateDocumentByIdAndState(@Param("id") Long id, @Param("state") String status);
}
