package com.todo.dto;

import lombok.Data;

@Data
public class TodoHabbitDTO {
    private long userId;
    private String title;
    private String content;
    private Integer dayMinutes;
    private Integer minMinutes;
    private Integer maxMinutes;
}
