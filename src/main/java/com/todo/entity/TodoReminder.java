package com.todo.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoReminder {
    private Long id;
    private Long userId;
    private String targetType;
    private Long targetId;
    private String title;
    private String content;
    private LocalDateTime remindTime;
    private String channel;
    private Integer isSent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
