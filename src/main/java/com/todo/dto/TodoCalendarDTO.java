package com.todo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoCalendarDTO {
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
}
