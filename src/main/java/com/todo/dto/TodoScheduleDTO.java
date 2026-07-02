package com.todo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoScheduleDTO {
    private Long id;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;

}
