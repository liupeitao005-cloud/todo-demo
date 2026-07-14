package com.todo.dto;

import com.todo.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TodoHabbitDTO {
    private long userId;

    @NotBlank(message = "习惯标题不能为空", groups = ValidationGroups.Create.class)
    private String title;

    @NotBlank(message = "习惯内容不能为空", groups = ValidationGroups.Create.class)
    private String content;

    private Integer dayMinutes;
    private Integer minMinutes;
    private Integer maxMinutes;
}
