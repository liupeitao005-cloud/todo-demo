package com.todo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoTask {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String taskType;
    private LocalDateTime finishTime;
    private Integer isFinish;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
