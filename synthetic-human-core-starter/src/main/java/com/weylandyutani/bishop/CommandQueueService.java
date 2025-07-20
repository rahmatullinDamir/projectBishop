package com.weylandyutani.bishop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CommandQueueService {
    private static final Logger log = LoggerFactory.getLogger(CommandQueueService.class);
    private static final int QUEUE_CAPACITY = 100;
    private final BlockingQueue<CommandDto> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicInteger completedCount = new AtomicInteger(0);
    private final ConcurrentHashMap<String, AtomicInteger> authorTaskCount = new ConcurrentHashMap<>();

    @PostConstruct
    public void startQueueProcessor() {
        executor.submit(() -> {
            while (true) {
                try {
                    CommandDto cmd = queue.take();
                    executeCommand(cmd);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void submitCommand(CommandDto command) {
        if (command.getPriority() == CommandDto.Priority.CRITICAL) {
            executeCommand(command);
        } else {
            if (!queue.offer(command)) {
                throw new CommandQueueOverflowException("Command queue is full");
            }
        }
    }

    private void executeCommand(CommandDto command) {
        log.info("Executing command: {} | Priority: {} | Author: {} | Time: {}", command.getDescription(), command.getPriority(), command.getAuthor(), command.getTime());
        completedCount.incrementAndGet();
        authorTaskCount.computeIfAbsent(command.getAuthor(), k -> new AtomicInteger(0)).incrementAndGet();
    }

    public int getQueueSize() {
        return queue.size();
    }

    public Map<String, Integer> getCompletedByAuthor() {
        return authorTaskCount.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));
    }

    public int getCompletedCount() {
        return completedCount.get();
    }

    public static class CommandQueueOverflowException extends RuntimeException {
        public CommandQueueOverflowException(String message) { super(message); }
    }
} 