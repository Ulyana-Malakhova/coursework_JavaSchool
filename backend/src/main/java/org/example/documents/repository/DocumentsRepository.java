package org.example.documents.repository;

import org.example.documents.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Интерфейс с методами репозитория JpaRepository
 */
public interface DocumentsRepository extends JpaRepository<Document, Long> {
    /**
     * Метод для изменения статуса элемента таблицы
     *
     * @param id     - идентификатор элемента, который нужно обновить
     * @param status - новый статус, который необходимо записать
     */
    @Modifying
    @Query("UPDATE Document d SET d.state = :state WHERE d.id =:id")
    void updateDocumentByIdAndState(@Param("id") Long id, @Param("state") String status);
}
