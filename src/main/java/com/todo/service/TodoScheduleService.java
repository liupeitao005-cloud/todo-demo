package com.todo.service;

import com.todo.dto.TodoScheduleDTO;
import com.todo.entity.TodoSchedule;
import com.todo.mapper.TodoScheduleMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoScheduleService {
    private final TodoScheduleMapper todoScheduleMapper;

    public Result<String> createTodoSchedule(TodoScheduleDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (!dto.getStartTime().isBefore(dto.getFinishTime())) {
            return Result.fail("开始时间必须早于结束时间");
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
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto.getStartTime() != null && dto.getFinishTime() != null && !dto.getStartTime().isBefore(dto.getFinishTime())) {
            return Result.fail("开始时间必须早于结束时间");
        }
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

    public Result<String> deleteTodoSchedule(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        TodoSchedule schedule = new TodoSchedule();
        schedule.setId(id);
        schedule.setUserId(userId);
        int row = todoScheduleMapper.delete(schedule);
        if (row <= 0) return Result.fail("行程不存在或无权限删除");
        return Result.success("删除成功");
    }

    public Result<List<TodoSchedule>> listTodoSchedule() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        return Result.success("查询成功", todoScheduleMapper.listByUserId(userId));
    }
}
