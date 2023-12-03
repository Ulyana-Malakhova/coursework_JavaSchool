package org.example.documents.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomUtils;
import org.example.configuration.JacksonConfiguration;
import org.example.documents.controller.dto.DocumentDto;
import org.example.documents.controller.dto.IdsDto;
import org.example.documents.controller.dto.Status;
import org.example.documents.service.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith({ SpringExtension.class, MockitoExtension.class })
public class DocumentControllerTest {
    private static final String BASE_PATH = "/documents";

    private final ObjectMapper mapper = new JacksonConfiguration().objectMapper();
    private MockMvc mockMvc;
    @MockBean
    private DocumentServiceImpl service;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void successWhenSaveTest() throws Exception {
        var organization = randomAlphabetic(100);
        when(service.save(any())).thenReturn(any());
        var documentDto = new DocumentDto();
        documentDto.setId(5L);
        documentDto.setOrganization(organization);
        mockMvc.perform(postAction(BASE_PATH, documentDto)).andExpect(status().isOk());
        Mockito.verify(service, Mockito.times(1)).save(documentDto);
    }

    @Test
    public void errorWhenSaveTest() throws Exception {
        var organization = randomAlphabetic(1000);
        when(service.save(any())).thenThrow(new IllegalStateException("Это слишком!"));
        var documentDto = new DocumentDto();
        documentDto.setId(5L);
        documentDto.setOrganization(organization);
        mockMvc.perform(postAction(BASE_PATH, documentDto)).andExpect(status().is5xxServerError());
    }

    @Test
    public void getTest() throws Exception {
        DocumentDto documentDto = new DocumentDto(RandomUtils.nextLong(0L, 999L),"type","organization","description","patient",new Date(), Status.of("NEW","Новый"));
        DocumentDto documentDto1 = new DocumentDto(RandomUtils.nextLong(0L, 999L),"type1","organization1","description1","patient1",new Date(), Status.of("NEW","Новый"));
        service.save(documentDto);
        service.save(documentDto1);
        Mockito.when(service.findAll()).thenReturn(Arrays.asList(documentDto1,documentDto));
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk());
    }

    @Test
    public void sendTest() {
        DocumentDto documentDto = new DocumentDto(RandomUtils.nextLong(0L, 999L),"type","organization","description","patient",new Date(), Status.of("NEW","Новый"));
        service.save(documentDto);
        when(service.update(documentDto)).thenAnswer(e->{
            documentDto.setStatus(Status.of("IN_PROCESS", "В обработке"));
            return documentDto;
        });
    }

    @Test
    public void deleteTest() throws Exception {
        DocumentDto documentDto = new DocumentDto(RandomUtils.nextLong(0L, 999L),"type","organization","description","patient",new Date(), Status.of("NEW","Новый"));
        service.save(documentDto);
        Mockito.when(service.get(Mockito.any())).thenReturn(documentDto);
        mockMvc.perform(
                        delete("/documents/{id}",documentDto.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteAllTest() throws Exception {
        DocumentDto documentDto = new DocumentDto(RandomUtils.nextLong(0L, 999L),"type","organization","description","patient",new Date(), Status.of("NEW","Новый"));
        DocumentDto documentDto1 = new DocumentDto(RandomUtils.nextLong(0L, 999L),"type1","organization1","description1","patient1",new Date(), Status.of("NEW","Новый"));
        service.save(documentDto);
        service.save(documentDto1);
        Mockito.when(service.get(Mockito.any())).thenReturn(documentDto);
        Mockito.when(service.get(Mockito.any())).thenReturn(documentDto1);
        IdsDto dtoId = new IdsDto();
        dtoId.setIds(Set.of(documentDto1.getId(),documentDto.getId()));
        mockMvc.perform(
                        delete(BASE_PATH)
                                .contentType(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dtoId)))
                .andExpect(status().isOk());
    }

    private MockHttpServletRequestBuilder postAction(String uri, Object dto) throws JsonProcessingException {
        return post(uri)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));
    }
}
