package com.todo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoReminderDTO {
    private Long id;
    private String targetType;
    private Long targetId;
    private String title;
    private String content;
    private LocalDateTime remindTime;
    private String channel;
}
