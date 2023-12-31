package org.example.documents.service;

import org.example.documents.controller.dto.DocumentDto;

import java.util.List;
import java.util.Set;

/**
 * Сервис по работе с документами
 */
public interface DocumentService {
    /**
     * Сохранить документ
     *
     * @param documentDto документ
     * @return сохраненный документ
     */
    DocumentDto save(DocumentDto documentDto);

    /**
     * Удалить документ
     *
     * @param ids идентификаторы документов
     */
    void deleteAll(Set<Long> ids);

    /**
     * Удалить документ по id
     *
     * @param id идентификатор документа
     */
    void delete(Long id);

    /**
     * Обновить документ
     *
     * @param documentDto документ
     * @return обновленный документ
     */
    DocumentDto update(DocumentDto documentDto);

    /**
     * Получить все документы
     *
     * @return список документов
     */
    List<DocumentDto> findAll();

    /**
     * Получить документ по id
     *
     * @param id идентификатор
     * @return документ
     */
    DocumentDto get(Long id);
}
