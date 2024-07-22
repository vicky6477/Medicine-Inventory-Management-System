package com.panda.medicineinventorymanagementsystem.aop;

import com.panda.medicineinventorymanagementsystem.dto.AuditPayload;
import com.panda.medicineinventorymanagementsystem.repository.KafkaProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class AuditAspect {
    private final HttpServletRequest request;

    private final KafkaProducer kafkaProducer;

    @Autowired
    public AuditAspect(HttpServletRequest request, KafkaProducer kafkaProducer) {
        this.request = request;
        this.kafkaProducer = kafkaProducer;
    }

    @Around("execution(* com.panda.medicineinventorymanagementsystem.controller.*.*(..))")
    public Object auditApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        LocalDateTime timestamp = LocalDateTime.now();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String userName = getCurrentUser(); // Fetch the authenticated user here
        String ipAddress = request.getRemoteAddr();
        Object[] methodArgs = joinPoint.getArgs();

        Object result = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - start;

        log.info("Audit Log: Method: {}.{} | User: {} | Timestamp: {} | Arguments: {} | Result: {} | IP Address: {} | Execution Time: {} ms",
                className, methodName, userName, timestamp, Arrays.toString(methodArgs), result, ipAddress, elapsedTime);
        kafkaProducer.sendPayload(
                AuditPayload.builder()
                        .className(className)
                        .methodArgs(methodArgs)
                        .methodName(methodName)
                        .startDateTime(timestamp)
                        .user(userName)
                        .srcIpAddress(ipAddress)
                        .methodResult(result)
                        .build()
        );
        return result;
    }

    private String getCurrentUser() {
        // Logic to fetch the current authenticated user
        // This could be from a security context, token, etc.
        return "Anonymous";
    }
}

