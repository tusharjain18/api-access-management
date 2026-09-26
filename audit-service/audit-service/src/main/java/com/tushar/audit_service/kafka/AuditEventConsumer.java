package com.tushar.audit_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tushar.audit_service.entity.AuditLog;
import com.tushar.audit_service.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "audit-events",
            groupId = "audit-service-group"
    )
    public void consume(String message) {

        try {
            AuditLog auditLog =
                    objectMapper.readValue(message, AuditLog.class);

            auditLogRepository.save(auditLog);

            System.out.println(
                    "Audit event saved: " + auditLog.getEventType()
            );

        } catch (Exception e) {
            System.err.println(
                    "Failed to process audit event: " + e.getMessage()
            );
        }
    }
}