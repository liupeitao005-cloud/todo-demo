package com.todo.dto;

import com.todo.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TodoReviewDTO {
    @NotNull(message = "复习ID不能为空", groups = ValidationGroups.IdRequired.class)
    private Long id;

    @NotBlank(message = "复习标题不能为空", groups = ValidationGroups.Create.class)
    private String title;

    private String content;
}
