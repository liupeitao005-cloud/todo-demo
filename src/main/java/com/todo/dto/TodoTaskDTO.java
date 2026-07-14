package com.todo.dto;

import com.todo.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoTaskDTO {
    @NotNull(message = "任务ID不能为空", groups = {ValidationGroups.Update.class, ValidationGroups.IdRequired.class})
    private Long id;

    @NotBlank(message = "任务标题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String title;

    @NotBlank(message = "任务内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String content;

    private String taskType;

    @NotNull(message = "任务开始时间不能为空", groups = ValidationGroups.Create.class)
    private LocalDateTime startTime;

    @NotNull(message = "任务结束时间不能为空", groups = ValidationGroups.Create.class)
    private LocalDateTime finishTime;

    private Long parentId;
}
