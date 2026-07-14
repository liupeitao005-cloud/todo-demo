package com.todo.dto;

import com.todo.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoScheduleDTO {
    @NotNull(message = "日程ID不能为空", groups = {ValidationGroups.Update.class, ValidationGroups.IdRequired.class})
    private Long id;

    @NotBlank(message = "日程标题不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String title;

    @NotBlank(message = "日程内容不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String content;

    private String location;

    @NotNull(message = "日程开始时间不能为空", groups = ValidationGroups.Create.class)
    private LocalDateTime startTime;

    @NotNull(message = "日程结束时间不能为空", groups = ValidationGroups.Create.class)
    private LocalDateTime finishTime;
}
