package com.todo.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TodoHabbitCheck {
    private Long id;
    private Long userId;
    private Long habbitId;
    private LocalDate checkDate;
    private LocalDateTime createTime;
}
