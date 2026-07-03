package com.todo.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoHabbit {
    private long id;
    private long userId;
    private String title;
    private String content;
    private Integer dayMinutes;
    private Integer minMinutes;
    private Integer maxMinutes;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
