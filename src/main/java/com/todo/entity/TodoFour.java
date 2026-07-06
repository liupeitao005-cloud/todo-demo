package com.todo.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoFour {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer importance;
    private Integer urgency;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
