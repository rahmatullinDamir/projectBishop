package com.weylandyutani.bishop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class CommandDto {
    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    private Priority priority;

    @NotBlank
    @Size(max = 100)
    private String author;

    @NotBlank
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})$", message = "Time must be in ISO-8601 format")
    private String time;

    public enum Priority {
        COMMON, CRITICAL
    }

    // getters and setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
} 