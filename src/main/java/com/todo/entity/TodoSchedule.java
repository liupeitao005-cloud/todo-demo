package com.todo.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoSchedule {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
