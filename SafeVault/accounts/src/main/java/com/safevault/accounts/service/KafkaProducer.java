package com.safevault.accounts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safevault.accounts.dto.NotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void send(String to, String type, Map<String, String> data, int priority) {
        try {
            NotificationDto dto = new NotificationDto(to, type, data, priority);
            String json = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send("notification-topic", to, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka message serialization failed", e);
        }
    }
}