package org.example.documents.controller;

import lombok.RequiredArgsConstructor;
import org.example.documents.controller.dto.DocumentDto;
import org.example.documents.controller.dto.IdDto;
import org.example.documents.controller.dto.IdsDto;
import org.example.documents.controller.dto.Status;
import org.example.documents.service.DocumentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Класс-сервлет
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService service;

    /**
     * Метод, сохраняющий элемент в таблицу
     *
     * @param dto - элемент, который нужно сохранить
     * @return - сохраненный элемент
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto save(@RequestBody DocumentDto dto) {
        return service.save(dto);
    }

    /**
     * Метод для получения элементов таблицы
     *
     * @return - список всех элементов
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DocumentDto> get() {
        return service.findAll();
    }

    /**
     * Метод для обновления статуса элемента
     *
     * @param id - идентификатор обновляемого элемента
     * @return - обновленный элемент
     */
    @PostMapping(
            path = "send",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto send(@RequestBody IdDto id) {
        DocumentDto document = service.get(id.getId());
        document.setStatus(Status.of("IN_PROCESS", "В обработке"));
        return service.update(document);
    }

    /**
     * Метод для удаления одного элемента таблицы
     *
     * @param id - идентификатор удаляемого элемента
     */
    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    /**
     * Метод для удаления нескольких элементов таблицы
     *
     * @param idsDto - список идентификаторов элементов
     */
    @DeleteMapping
    public void deleteAll(@RequestBody IdsDto idsDto) {
        service.deleteAll(idsDto.getIds());
    }

}
