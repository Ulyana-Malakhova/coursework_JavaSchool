package org.example.kafka;

import org.example.documents.controller.dto.DocumentDto;
import org.example.documents.repository.DocumentsRepository;
import org.example.documents.service.DocumentServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final DocumentServiceImpl documentService;

    public KafkaConsumer( DocumentServiceImpl documentService) {
        this.documentService = documentService;
    }

    @KafkaListener(topics = "documents", groupId = "group_id", containerFactory = "kafkaListenerContainerFactory")
    public void consume(@Payload String message) {
        System.out.println("message = " + message);
        Long id = Long.parseLong(message);
        DocumentDto documentDto = documentService.get(id);
        documentService.updateFromKafka(documentDto,"Принят");
        //documentsRepository.updateDocumentByIdAndState(id,"");
        /*String[] result = message.split(":");
        Long id = Long.parseLong(result[0]);
        if(result[1].equals("Принят")){
            documentsRepository.updateFromKafka(id,"Принят");
        }else {
            documentsRepository.updateFromKafka(id,"Отклонен");
        }*/
    }


}
