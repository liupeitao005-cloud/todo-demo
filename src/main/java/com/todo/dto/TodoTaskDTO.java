package com.todo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class TodoTaskDTO {
    private Long id;
    private String title;
    private String content;
    private String taskType;
}
