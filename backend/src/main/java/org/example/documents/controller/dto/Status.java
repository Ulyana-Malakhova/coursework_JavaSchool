package org.example.documents.controller.dto;

import lombok.Data;

/**
 * Класс статуса записи таблицы
 */
@Data
public class Status {
    private String code;
    private String name;

    /**
     * Метод для получения экземпляра класса с необходимым статусом
     *
     * @param code - код статуса
     * @param name - статус
     * @return - элемент класса
     */
    public static Status of(String code, String name) {
        Status codeName = new Status();
        codeName.setCode(code);
        codeName.setName(name);
        return codeName;
    }
}
