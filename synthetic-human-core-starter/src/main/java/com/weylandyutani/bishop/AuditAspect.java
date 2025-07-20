package com.weylandyutani.bishop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class AuditAspect {
    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String auditMode;
    private final String topic;

    public AuditAspect(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${audit.mode:console}") String auditMode,
            @Value("${audit.kafka.topic:android-audit}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.auditMode = auditMode;
        this.topic = topic;
    }

    @Around("@annotation(com.weylandyutani.bishop.WeylandWatchingYou)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        Object[] args = joinPoint.getArgs();
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("method", methodName);
        auditData.put("args", Arrays.toString(args));
        Object result = null;
        Throwable error = null;
        try {
            result = joinPoint.proceed();
            auditData.put("result", result);
            return result;
        } catch (Throwable t) {
            auditData.put("error", t.getMessage());
            error = t;
            throw t;
        } finally {
            if ("kafka".equalsIgnoreCase(auditMode)) {
                kafkaTemplate.send(topic, auditData);
            } else {
                log.info("[AUDIT] {}", auditData);
            }
        }
    }
} 