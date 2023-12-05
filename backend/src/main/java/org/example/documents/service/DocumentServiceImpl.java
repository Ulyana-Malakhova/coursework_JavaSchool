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
    private List<DocumentDto> toDto(){
        List<Document> documents = documentsRepository.findAll();
        List<DocumentDto> documentDtos = new ArrayList<>();
        for (Document document : documents) {
            String state = document.getState();
            if(state.equals("Новый")) {
                documentDtos.add(new DocumentDto(document.getId(), document.getType(), document.getOrganization(), document.getDescription(), document.getPatient(),
                        document.getDate(), Status.of("NEW", "Новый")));
            }
            if(state.equals("В обработке")) {
                documentDtos.add(new DocumentDto(document.getId(), document.getType(), document.getOrganization(), document.getDescription(), document.getPatient(),
                        document.getDate(), Status.of("IN_PROCESS", "В обработке")));
            }
            if(state.equals("Принят")) {
                documentDtos.add(new DocumentDto(document.getId(), document.getType(), document.getOrganization(), document.getDescription(), document.getPatient(),
                        document.getDate(), Status.of("Accepted", "Принят")));
            }
            if(state.equals("Отклонен")) {
                documentDtos.add(new DocumentDto(document.getId(), document.getType(), document.getOrganization(), document.getDescription(), document.getPatient(),
                        document.getDate(), Status.of("Declined", "Отклонен")));
            }
        }
        return documentDtos;
    }

    @Transactional
    public DocumentDto save(DocumentDto documentDto) {
        int n=0;
        if (documentDto.getId() == null) {
            documentDto.setId(RandomUtils.nextLong(0L, 999L));
        }
        documentDto.setDate(new Date());
        if (documentDto.getStatus() == null) {
            documentDto.setStatus(Status.of("NEW", "Новый"));
            n=1;
        }
        Document entityDocument = mapperFacade.map(documentDto,Document.class);
        if(n==1) {
            entityDocument.setState("Новый");
        }
        documentsRepository.save(entityDocument);
        return documentDto;
    }

    @Transactional
    public DocumentDto update(DocumentDto documentDto) {
        kafkaSender.sendMessage(documentDto.toString());
        documentsRepository.updateDocumentByIdAndState(documentDto.getId(), "В обработке");
        return documentDto;
    }

    @Transactional
    public void delete(Long id) {
        documentsRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll(Set<Long> ids) {
        for (Long id:ids){
            documentsRepository.deleteById(id);
        }
    }

    @Transactional
    public List<DocumentDto> findAll() {
        return toDto();
    }

    @Transactional
    public DocumentDto get(Long id) {
        Document document = documentsRepository.getOne(id);
        String state = document.getState();
        DocumentDto documentDto=new DocumentDto(document.getId(), document.getType(), document.getOrganization(), document.getDescription(), document.getPatient(),
                document.getDate(), Status.of("NEW", "Новый"));
        if(state.equals("В обработке")) {
            documentDto.setStatus(Status.of("IN_PROCESS", "В обработке"));
        } else if (state.equals("Принят")){
            documentDto.setStatus(Status.of("Accepted", "Принят"));
        }else if (state.equals("Отклонен")){
            documentDto.setStatus(Status.of("Declined", "Отклонен"));
        }
        return documentDto;
    }

    @Transactional
    public DocumentDto updateFromKafka(DocumentDto documentDto,String status) {
        if(status.equals("Принят")) {
            documentsRepository.updateDocumentByIdAndState(documentDto.getId(), "Принят");
        } else{
            documentsRepository.updateDocumentByIdAndState(documentDto.getId(), "Отклонен");
        }
        return documentDto;
    }
}
