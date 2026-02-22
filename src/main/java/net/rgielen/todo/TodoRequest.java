package net.rgielen.todo;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalTime;

public record TodoRequest(
        @NotBlank String topic,
        String details,
        LocalDate dueDate,
        LocalTime dueTime,
        Boolean completed
) {
}
