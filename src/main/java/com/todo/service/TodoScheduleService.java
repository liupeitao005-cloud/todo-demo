package com.todo.service;

import com.todo.dto.TodoScheduleDTO;
import com.todo.entity.TodoSchedule;
import com.todo.mapper.TodoScheduleMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TodoScheduleService {
    private final TodoScheduleMapper todoScheduleMapper;

    public Result<String> createtodoSchedule(TodoScheduleDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto == null || !StringUtils.hasText(dto.getContent())) {
            return Result.fail("行程内容不能为空");
        }
        if (dto.getStartTime() == null || dto.getFinishTime() == null) {
            return Result.fail("行程时间不能为空");
        }

        TodoSchedule schedule = new TodoSchedule();
        schedule.setUserId(userId);
        schedule.setTitle(dto.getTitle());
        schedule.setContent(dto.getContent());
        schedule.setLocation(dto.getLocation());
        schedule.setStartTime(dto.getStartTime());
        schedule.setFinishTime(dto.getFinishTime());
        todoScheduleMapper.insert(schedule);
        return Result.success("创建成功");
    }

    public Result<String> updateTodoSchedule(TodoScheduleDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto == null || dto.getId() == null) return Result.fail("缺少行程id");

        TodoSchedule schedule = new TodoSchedule();
        schedule.setId(dto.getId());
        schedule.setUserId(userId);
        schedule.setTitle(dto.getTitle());
        schedule.setContent(dto.getContent());
        schedule.setLocation(dto.getLocation());
        schedule.setStartTime(dto.getStartTime());
        schedule.setFinishTime(dto.getFinishTime());

        int row = todoScheduleMapper.update(schedule);
        if (row <= 0) return Result.fail("行程不存在或无权限修改");
        return Result.success("修改成功");
    }

    public Result<String> deleteTodoSchedule(TodoScheduleDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto == null || dto.getId() == null) return Result.fail("缺少行程id");

        TodoSchedule schedule = new TodoSchedule();
        schedule.setId(dto.getId());
        schedule.setUserId(userId);

        int row = todoScheduleMapper.delete(schedule);
        if (row <= 0) return Result.fail("行程不存在或无权限删除");
        return Result.success("删除成功");
    }
}
