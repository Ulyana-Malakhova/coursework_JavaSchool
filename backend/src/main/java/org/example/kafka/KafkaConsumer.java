package org.example.kafka;

import org.example.documents.controller.dto.DocumentDto;
import org.example.documents.service.DocumentServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Класс для чтения информации из кафки
 */
@Component
public class KafkaConsumer {
    private final DocumentServiceImpl documentService;

    public KafkaConsumer(DocumentServiceImpl documentService) {
        this.documentService = documentService;
    }

    /**
     * Метод выводит отправленное сообщение
     *
     * @param message - отправленное сообщение
     */
    @KafkaListener(topics = "documents", groupId = "group_id", containerFactory = "kafkaListenerContainerFactory")
    public void consume(@Payload String message) {
        System.out.println("message = " + message);
    }

    /**
     * Метод, читающий сообщение из кафки. Основываясь на полученной информации меняет статус элемента таблицы documents
     *
     * @param message - полученное сообщение
     */
    @KafkaListener(topics = "status", groupId = "group_id", containerFactory = "kafkaListenerContainerFactory")
    public void consumeStatus(@Payload String message) {
        System.out.println("Result message = " + message);
        String[] result = message.split(":");
        Long id = Long.parseLong(result[0]);
        DocumentDto documentDto = documentService.get(id);
        if (result[1].equals("Принят")) {
            documentService.updateFromKafka(documentDto, "Принят");
        } else {
            documentService.updateFromKafka(documentDto, "Отклонен");
        }
    }
}
