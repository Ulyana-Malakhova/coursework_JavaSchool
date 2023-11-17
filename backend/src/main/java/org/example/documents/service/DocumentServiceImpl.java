package org.example.documents.service;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.lang3.RandomUtils;
import org.example.documents.controller.dto.DocumentDto;
import org.example.documents.controller.dto.Status;
import org.example.documents.entity.Document;
import org.example.documents.repository.DocumentsRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentsRepository documentsRepository;
    private final MapperFacade mapperFacade = new DefaultMapperFactory.Builder()
            .build().getMapperFacade();

    public DocumentServiceImpl(DocumentsRepository documentsRepository) {
        this.documentsRepository = documentsRepository;
    }

    private List<DocumentDto> toDto(){
        List<Document> documents = documentsRepository.findAll();
        List<DocumentDto> documentDtos = new ArrayList<>();
        for (Document document : documents) {
            documentDtos.add(new DocumentDto(document.getId(), document.getType(), document.getOrganization(), document.getDescription(), document.getPatient(),
                    document.getDate(), Status.of("NEW", "Новый")));
        }
        return documentDtos;
    }

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
        Document document=documentsRepository.save(entityDocument);
        return documentDto;
    }


    public DocumentDto update(DocumentDto documentDto) {
        List<DocumentDto> documentDtos = toDto();
        Optional<DocumentDto> dto = documentDtos.stream()
                .filter(d -> d.getId().equals(documentDto.getId())).findFirst();
        if (dto.isPresent()) {
            delete(documentDto.getId());
            save(documentDto);
        }
        return documentDto;
    }

    public void delete(Long id) {
        documentsRepository.deleteById(id);
    }

    public void deleteAll(Set<Long> ids) {
        for (Long id:ids){
            documentsRepository.deleteById(id);
        }
    }

    public List<DocumentDto> findAll() {
        return toDto();
    }

    public DocumentDto get(Long id) {
        Optional<Document> document=documentsRepository.findById(id);
        return mapperFacade.map(document, DocumentDto.class);
    }
}
