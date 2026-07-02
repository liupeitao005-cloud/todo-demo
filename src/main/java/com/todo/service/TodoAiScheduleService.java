package com.todo.service;

import com.todo.dto.TodoAiScheduleDTO;
import com.todo.dto.TodoScheduleDTO;
import com.todo.util.Result;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class TodoAiScheduleService {
    public Result<TodoScheduleDTO> draftSchedule(TodoAiScheduleDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getText())) {
            return Result.fail("请输入排程内容");
        }

        LocalDateTime startTime = LocalDateTime.now();

        TodoScheduleDTO schedule = new TodoScheduleDTO();
        schedule.setTitle("AI排程草稿");
        schedule.setContent(dto.getText());
        schedule.setStartTime(startTime);
        schedule.setFinishTime(startTime.plusHours(2));

        return Result.success("生成草稿成功", schedule);
    }
}
