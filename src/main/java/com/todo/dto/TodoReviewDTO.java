package com.todo.dto;


import com.todo.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TodoReviewDTO {
    @NotNull(message = "复习计划id不能为空", groups = ValidationGroups.IdRequired.class)
    private Long id;

    @NotBlank(message = "复习标题不能为空", groups = ValidationGroups.Create.class)
    private String title;

    @NotBlank(message = "复习内容不能为空", groups = ValidationGroups.Create.class)
    private String content;
}
