package org.example.documents.service;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.lang3.RandomUtils;
import org.example.documents.controller.dto.DocumentDto;
import org.example.documents.controller.dto.Status;
import org.example.documents.entity.Document;
import org.example.documents.repository.DocumentsRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentsRepository documentsRepository;
    private final MapperFacade mapperFacade = new DefaultMapperFactory.Builder()
            .build().getMapperFacade();

    public DocumentServiceImpl(DocumentsRepository documentsRepository) {
        this.documentsRepository = documentsRepository;
    }


    public DocumentDto save(DocumentDto documentDto) {
        if (documentDto.getId() == null) {
            documentDto.setId(RandomUtils.nextLong(0L, 999L));
        }
        documentDto.setDate(new Date());
        if (documentDto.getStatus() == null) {
            documentDto.setStatus(Status.of("NEW", "Новый"));
        }
        Document entityDocument = mapperFacade.map(documentDto,Document.class);
        Document document=documentsRepository.save(entityDocument);
        return documentDto;
    }


    public DocumentDto update(DocumentDto documentDto) {
        List<DocumentDto> documentDtos = mapperFacade.mapAsList(documentsRepository.findAll(),DocumentDto.class);
        Optional<DocumentDto> dto = documentDtos.stream()
                .filter(d -> d.getId().equals(documentDto.getId())).findFirst();
        if (dto.isPresent()) {
            delete(documentDto.getId());
            save(documentDto);
        }
        return documentDto;
    }

    public void delete(Long id) {
        List<DocumentDto> documentDtos = mapperFacade.mapAsList(documentsRepository.findAll(),DocumentDto.class);
        List<DocumentDto> newList = documentDtos.stream()
                .filter(d -> !d.getId().equals(id)).collect(Collectors.toList());
        documentDtos.clear();
        documentDtos.addAll(newList);
    }

    public void deleteAll(Set<Long> ids) {
        List<DocumentDto> documentDtos = mapperFacade.mapAsList(documentsRepository.findAllById(ids), DocumentDto.class);
        List<DocumentDto> newList = documentDtos.stream()
                .filter(d -> !ids.contains(d.getId())).collect(Collectors.toList());
        documentDtos.clear();
        documentDtos.addAll(newList);
    }

    public List<DocumentDto> findAll() {
        return mapperFacade.mapAsList(documentsRepository.findAll(),DocumentDto.class);
    }

    public DocumentDto get(Long id) {
        List<DocumentDto> documentDtos = mapperFacade.mapAsList(documentsRepository.findAll(),DocumentDto.class);
        return documentDtos.stream()
                .filter(d -> d.getId().equals(id)).findFirst().orElseThrow(() -> new IllegalStateException("cannot find " + id));
    }
}
