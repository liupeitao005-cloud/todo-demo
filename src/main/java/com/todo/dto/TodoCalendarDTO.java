package com.todo.dto;

import com.todo.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoCalendarDTO {
    private Long userId;

    @NotNull(message = "查询开始时间不能为空", groups = ValidationGroups.Query.class)
    private LocalDateTime startTime;

    @NotNull(message = "查询结束时间不能为空", groups = ValidationGroups.Query.class)
    private LocalDateTime finishTime;
}
