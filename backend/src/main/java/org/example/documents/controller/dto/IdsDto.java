package org.example.documents.controller.dto;

import lombok.Data;

import java.util.Set;

/**
 * Класс с набором идентификаторов
 */
@Data
public class IdsDto {

    private Set<Long> ids;

}
