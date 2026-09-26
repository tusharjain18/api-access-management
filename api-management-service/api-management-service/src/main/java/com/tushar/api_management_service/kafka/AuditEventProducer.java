package com.tushar.api_management_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "audit-events";

    public void sendAuditEvent(
            String eventType,
            String username,
            String resource,
            String details
    ) {

        try {
            AuditEvent event = new AuditEvent(
                    eventType,
                    username,
                    resource,
                    details,
                    java.time.LocalDateTime.now()
            );

            String message = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(TOPIC, message);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create audit event", e);
        }
    }

    private record AuditEvent(
            String eventType,
            String username,
            String resource,
            String details,
            java.time.LocalDateTime timestamp
    ) {}
}