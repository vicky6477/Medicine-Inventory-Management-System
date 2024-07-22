package com.panda.medicineinventorymanagementsystem.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AuditPayload {
    private String methodName;

    private LocalDateTime startDateTime;

    private Long elapsedTime;

    private String className;

    private String user;

    private String srcIpAddress;

    private Object[] methodArgs;

    private Object methodResult;
}
