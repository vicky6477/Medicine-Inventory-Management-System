package com.panda.medicineinventorymanagementsystem.repository;

import com.panda.medicineinventorymanagementsystem.dto.AuditPayload;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    public static final String TOPIC_AUDIT = "audit";

    public String sendPayload(AuditPayload auditPayload) {
        return "success";
    }
}
