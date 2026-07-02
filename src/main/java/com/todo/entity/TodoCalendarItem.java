package com.todo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoCalendarItem {
    private Long id;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private String itemType;
}
