package org.example.documents.service;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.lang3.RandomUtils;
import org.example.documents.controller.dto.DocumentDto;
import org.example.documents.controller.dto.Status;
import org.example.documents.entity.Document;
import org.example.documents.repository.DocumentsRepository;
import org.example.kafka.KafkaSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Класс имплементирующий методы сервиса. Содержит методы взаимодействия с базой данных
 */
@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentsRepository documentsRepository;
    private final MapperFacade mapperFacade = new DefaultMapperFactory.Builder()
            .build().getMapperFacade();

    private final KafkaSender kafkaSender;

    public DocumentServiceImpl(DocumentsRepository documentsRepository, KafkaSender kafkaSender) {
        this.documentsRepository = documentsRepository;
        this.kafkaSender = kafkaSender;
    }

    /**
     * Метод преобразования объектов Entity в Dto
     *
     * @return - список всех объектов dto, находящихся в таблице
     */
    private List<DocumentDto> toDto() {
        List<Document> documents = documentsRepository.findAll();
        List<DocumentDto> documentDtos = new ArrayList<>();
        for (Document document : documents) {
            String state = document.getState();
            if (state.equals("Новый")) {
                documentDtos.add(new DocumentDto(document.getId(), document.getType(), document.getOrganization(), document.getDescription(), document.getPatient(),
                        document.getDate(), Status.of("NEW", "Новый")));
            }
            if (state.equals("В обработке")) {
                documentDtos.add(new DocumentDto(document.getId(), document.getType(), document.getOrganization(), document.getDescription(), document.getPatient(),
                        document.getDate(), Status.of("IN_PROCESS", "В обработке")));
            }
            if (state.equals("Принят")) {
                documentDtos.add(new DocumentDto(document.getId(), document.getType(), document.getOrganization(), document.getDescription(), document.getPatient(),
                        document.getDate(), Status.of("Accepted", "Принят")));
            }
            if (state.equals("Отклонен")) {
                documentDtos.add(new DocumentDto(document.getId(), document.getType(), document.getOrganization(), document.getDescription(), document.getPatient(),
                        document.getDate(), Status.of("Declined", "Отклонен")));
            }
        }
        return documentDtos;
    }

    /**
     * Метод, сохраняющий элемент в базу данных
     *
     * @param documentDto - документ который необходимо сохранить
     * @return - сохраненный dto
     */
    @Transactional
    public DocumentDto save(DocumentDto documentDto) {
        int n = 0;
        if (documentDto.getId() == null) {
            documentDto.setId(RandomUtils.nextLong(0L, 999L));
        }
        documentDto.setDate(new Date());
        if (documentDto.getStatus() == null) {
            documentDto.setStatus(Status.of("NEW", "Новый"));
            n = 1;
        }
        Document entityDocument = mapperFacade.map(documentDto, Document.class);
        if (n == 1) {
            entityDocument.setState("Новый");
        }
        documentsRepository.save(entityDocument);
        return documentDto;
    }

    /**
     * Метод, обновляющий статус записи в таблице из "Новый" на "В обработке"
     *
     * @param documentDto документ статус которого нужно изменить
     * @return - измененный элемент
     */
    @Transactional
    public DocumentDto update(DocumentDto documentDto) {
        kafkaSender.sendMessage(documentDto.toString());
        documentsRepository.updateDocumentByIdAndState(documentDto.getId(), "В обработке");
        return documentDto;
    }

    /**
     * Метод, удаляющий одну запись из таблицы
     *
     * @param id идентификатор документа, по которому определяется объект, который необходимо удалить
     */
    @Transactional
    public void delete(Long id) {
        documentsRepository.deleteById(id);
    }

    /**
     * Метод, удаляющий несколько записей из таблицы
     *
     * @param ids идентификаторы документов, которые необходимо удалить
     */
    @Transactional
    public void deleteAll(Set<Long> ids) {
        for (Long id : ids) {
            documentsRepository.deleteById(id);
        }
    }

    /**
     * Метод, возвращающий список всех элементов таблицы
     *
     * @return - список записей таблицы
     */
    @Transactional
    public List<DocumentDto> findAll() {
        return toDto();
    }

    /**
     * Метод, возвращающий один элемент таблицы
     *
     * @param id идентификатор элемента, который нужно получить
     * @return - объект dto, который нужно было получить
     */
    @Transactional
    public DocumentDto get(Long id) {
        Document document = documentsRepository.getOne(id);
        String state = document.getState();
        DocumentDto documentDto = new DocumentDto(document.getId(), document.getType(), document.getOrganization(), document.getDescription(), document.getPatient(),
                document.getDate(), Status.of("NEW", "Новый"));
        if (state.equals("В обработке")) {
            documentDto.setStatus(Status.of("IN_PROCESS", "В обработке"));
        } else if (state.equals("Принят")) {
            documentDto.setStatus(Status.of("Accepted", "Принят"));
        } else if (state.equals("Отклонен")) {
            documentDto.setStatus(Status.of("Declined", "Отклонен"));
        }
        return documentDto;
    }

    /**
     * Метод, обновляющий статус объекта на основе сообщения из кафки
     *
     * @param documentDto - элемент, статус которого нужно обновить
     * @param status      - статус, который должен быть присвоен элементу
     * @return - обновленный dto
     */
    @Transactional
    public DocumentDto updateFromKafka(DocumentDto documentDto, String status) {
        if (status.equals("Принят")) {
            documentsRepository.updateDocumentByIdAndState(documentDto.getId(), "Принят");
        } else {
            documentsRepository.updateDocumentByIdAndState(documentDto.getId(), "Отклонен");
        }
        return documentDto;
    }
}
