package com.todo.dto;

import com.todo.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoReminderDTO {
    @NotNull(message = "提醒ID不能为空", groups = ValidationGroups.IdRequired.class)
    private Long id;

    @NotBlank(message = "提醒对象类型不能为空", groups = ValidationGroups.Create.class)
    private String targetType;

    @NotNull(message = "提醒对象ID不能为空", groups = ValidationGroups.Create.class)
    private Long targetId;

    @NotBlank(message = "提醒标题不能为空", groups = ValidationGroups.Create.class)
    private String title;

    @NotBlank(message = "提醒内容不能为空", groups = ValidationGroups.Create.class)
    private String content;

    @NotNull(message = "提醒时间不能为空", groups = ValidationGroups.Create.class)
    private LocalDateTime remindTime;

    private String channel;
}
