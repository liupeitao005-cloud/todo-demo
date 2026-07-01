package com.todo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoUser {
    private Long id;
    private String username;
    private String password;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
