package com.todo.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoReview {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
