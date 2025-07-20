package com.weylandyutani.bishop;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class AndroidMetrics {
    private final CommandQueueService commandQueueService;
    private final MeterRegistry meterRegistry;

    @Autowired
    public AndroidMetrics(CommandQueueService commandQueueService, MeterRegistry meterRegistry) {
        this.commandQueueService = commandQueueService;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void registerMetrics() {
        meterRegistry.gauge("android.queue.size", commandQueueService, CommandQueueService::getQueueSize);
        meterRegistry.gauge("android.tasks.completed", commandQueueService, CommandQueueService::getCompletedCount);
    }
} 