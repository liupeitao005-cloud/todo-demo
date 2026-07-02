package com.todo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoCalendarDTO {
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
}
