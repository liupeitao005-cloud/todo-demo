package com.todo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoCalendarVO {
    private Long id;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private String itemType;
}
