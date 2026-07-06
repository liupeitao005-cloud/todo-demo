package com.todo.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoBacklog {
    public Long id;
    public Long userId;
    public String title;
    public String content;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}
