package com.todo.dto;

import com.todo.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TodoHabbitCheckDTO {
    @NotNull(message = "习惯ID不能为空", groups = ValidationGroups.Update.class)
    private Long habbitId;

    @NotNull(message = "打卡日期不能为空", groups = ValidationGroups.Update.class)
    private LocalDate checkDate;

    private LocalDate startDate;
    private LocalDate endDate;
}
