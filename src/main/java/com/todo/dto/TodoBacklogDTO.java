package com.todo.dto;

import com.todo.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoBacklogDTO {
    @NotNull(message = "待办ID不能为空", groups = {ValidationGroups.Update.class, ValidationGroups.Move.class})
    private Long id;

    @NotBlank(message = "待办标题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String title;

    @NotBlank(message = "待办内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String content;

    @NotNull(message = "开始时间不能为空", groups = ValidationGroups.Move.class)
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空", groups = ValidationGroups.Move.class)
    private LocalDateTime finishTime;
}
