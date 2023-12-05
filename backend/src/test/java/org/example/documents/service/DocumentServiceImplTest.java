package org.example.documents.service;

import org.example.documents.controller.dto.DocumentDto;
import org.example.documents.controller.dto.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс с тестами методов класса DocumentServiceImpl
 */
@SpringBootTest
@ActiveProfiles("test")
public class DocumentServiceImplTest {
    @Autowired
    private DocumentServiceImpl documentService;

    @BeforeEach
    public void before() {
        List<DocumentDto> dto = documentService.findAll();
        if (!dto.isEmpty()) {
            for (DocumentDto documentDto : dto) {
                documentService.delete(documentDto.getId());
            }
        }
    }

    @Test
    public void saveGetTest() {
        DocumentDto documentDto = new DocumentDto();
        documentDto.setType("type");
        documentDto.setDescription("description");
        documentDto.setOrganization("organization");
        documentDto.setPatient("patient");
        documentService.save(documentDto);
        List<DocumentDto> documentDtos = documentService.findAll();
        assertEquals(1, documentDtos.size());
        Long id = documentDtos.get(0).getId();
        DocumentDto dto = documentService.get(id);
        assertEquals(id, dto.getId());
    }

    @Test
    void update() {
        DocumentDto documentDto = new DocumentDto();
        documentDto.setType("type");
        documentDto.setDescription("description");
        documentDto.setOrganization("organization");
        documentDto.setPatient("patient");
        documentService.save(documentDto);
        DocumentDto dto = documentService.findAll().get(0);
        Long id = dto.getId();
        documentService.update(dto);
        DocumentDto documentDtoFromSystem = documentService.get(id);
        assertEquals(Status.of("IN_PROCESS", "В обработке"), documentDtoFromSystem.getStatus());
    }

    @Test
    void delete() {
        DocumentDto documentDto = new DocumentDto();
        documentDto.setType("type");
        documentDto.setDescription("description");
        documentDto.setOrganization("organization");
        documentDto.setPatient("patient");
        documentService.save(documentDto);
        Long id = documentService.findAll().get(0).getId();
        documentService.delete(id);
        assertEquals(0, documentService.findAll().size());
    }

    @Test
    void deleteAll() {
        DocumentDto documentDto = new DocumentDto();
        documentDto.setType("type");
        documentDto.setDescription("description");
        documentDto.setOrganization("organization");
        documentDto.setPatient("patient");
        documentService.save(documentDto);
        Long id = documentService.findAll().get(0).getId();
        DocumentDto documentDto1 = new DocumentDto();
        documentDto.setType("type1");
        documentDto.setDescription("description1");
        documentDto.setOrganization("organization1");
        documentDto.setPatient("patient1");
        documentService.save(documentDto1);
        Long id1 = documentService.findAll().get(1).getId();
        documentService.deleteAll(Set.of(id, id1));
        assertEquals(0, documentService.findAll().size());
    }

    @Test
    void findAll() {
        DocumentDto documentDto = new DocumentDto();
        documentDto.setType("type");
        documentDto.setDescription("description");
        documentDto.setOrganization("organization");
        documentDto.setPatient("patient");
        documentService.save(documentDto);
        Long id = documentService.findAll().get(0).getId();
        DocumentDto documentDto1 = new DocumentDto();
        documentDto.setType("type1");
        documentDto.setDescription("description1");
        documentDto.setOrganization("organization1");
        documentDto.setPatient("patient1");
        documentService.save(documentDto1);
        Long id1 = documentService.findAll().get(1).getId();
        Map<Long, DocumentDto> allDocumentMap = documentService.findAll()
                .stream()
                .collect(Collectors.toMap(DocumentDto::getId, Function.identity()));
        assertEquals(2, allDocumentMap.size());
        assertNotNull(allDocumentMap.get(id));
        assertNotNull(allDocumentMap.get(id1));
        assertNull(allDocumentMap.get(id1 + 1));
    }
}
