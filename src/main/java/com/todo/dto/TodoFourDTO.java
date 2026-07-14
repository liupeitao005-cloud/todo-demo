package com.todo.dto;

import com.todo.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TodoFourDTO {
    @NotNull(message = "任务ID不能为空", groups = {ValidationGroups.Move.class, ValidationGroups.IdRequired.class})
    private Long id;

    @NotNull(message = "重要性不能为空", groups = {ValidationGroups.Move.class, ValidationGroups.Query.class})
    private Integer importance;

    @NotNull(message = "紧急性不能为空", groups = {ValidationGroups.Move.class, ValidationGroups.Query.class})
    private Integer urgency;
}
