package com.todo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoTaskDTO {
    private Long id;
    private String title;
    private String content;
    private String taskType;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private Long parentId;
}
