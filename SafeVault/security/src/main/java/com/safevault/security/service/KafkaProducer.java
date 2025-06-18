package com.safevault.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safevault.security.dto.NotificationDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void send(String to, String type, String message, int priority) {
        try {
            NotificationDto dto = new NotificationDto(to, type, message, priority);
            String json = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send("notification-topic", to, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka message serialization failed", e);
        }
    }
}
