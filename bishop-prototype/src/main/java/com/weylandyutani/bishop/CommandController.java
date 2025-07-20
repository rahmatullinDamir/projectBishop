package com.weylandyutani.bishop;

import com.weylandyutani.bishop.CommandDto;
import com.weylandyutani.bishop.CommandQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import com.weylandyutani.bishop.DemoAuditService;

@RestController
@RequestMapping("/api/commands")
@Validated
public class CommandController {
    private final CommandQueueService commandQueueService;
    private final DemoAuditService demoAuditService;

    @Autowired
    public CommandController(CommandQueueService commandQueueService, DemoAuditService demoAuditService) {
        this.commandQueueService = commandQueueService;
        this.demoAuditService = demoAuditService;
    }

    @PostMapping
    public void submitCommand(@Valid @RequestBody CommandDto command) {
        commandQueueService.submitCommand(command);
        demoAuditService.markCommandHandled(command);
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("queueSize", commandQueueService.getQueueSize());
        stats.put("completedByAuthor", commandQueueService.getCompletedByAuthor());
        return stats;
    }
} 