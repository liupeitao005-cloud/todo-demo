package com.todo.dto;

import com.todo.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoScheduleDTO {
    @NotNull(message = "缺少行程id", groups = {ValidationGroups.Update.class, ValidationGroups.IdRequired.class})
    private Long id;
    private String title;

    @NotBlank(message = "行程内容不能为空", groups = ValidationGroups.Create.class)
    private String content;
    private String location;

    @NotNull(message = "行程开始时间不能为空", groups = ValidationGroups.Create.class)
    private LocalDateTime startTime;

    @NotNull(message = "行程结束时间不能为空", groups = ValidationGroups.Create.class)
    private LocalDateTime finishTime;

}
