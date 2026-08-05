package com.todo.service;

import com.todo.dto.TodoHabbitCheckDTO;
import com.todo.dto.TodoHabbitDTO;
import com.todo.entity.TodoHabbit;
import com.todo.entity.TodoHabbitCheck;
import com.todo.mapper.TodoHabbitCheckMapper;
import com.todo.mapper.TodoHabbitMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoHabbitService {
    private final TodoHabbitMapper todoHabbitMapper;
    private final TodoHabbitCheckMapper todoHabbitCheckMapper;

    public Result<String> createHabbit(TodoHabbitDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        TodoHabbit habbit = new TodoHabbit();
        habbit.setUserId(userId);
        habbit.setTitle(dto.getTitle());
        habbit.setContent(dto.getContent());
        habbit.setDayMinutes(dto.getDayMinutes());
        habbit.setMinMinutes(dto.getMinMinutes());
        habbit.setMaxMinutes(dto.getMaxMinutes());
        todoHabbitMapper.insert(habbit);
        return Result.success("创建成功");
    }

    public Result<List<TodoHabbit>> listHabbit() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        return Result.success("查询成功", todoHabbitMapper.listByUserId(userId));
    }

    public Result<List<TodoHabbitCheck>> listChecks(TodoHabbitCheckDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        return Result.success("查询成功", todoHabbitCheckMapper.listByUserAndDateRange(
                userId,
                dto == null ? null : dto.getStartDate(),
                dto == null ? null : dto.getEndDate()
        ));
    }

    public Result<String> toggleCheck(TodoHabbitCheckDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (todoHabbitMapper.countByIdAndUserId(dto.getHabbitId(), userId) <= 0) {
            return Result.fail("习惯不存在或无权操作");
        }

        TodoHabbitCheck check = new TodoHabbitCheck();
        check.setUserId(userId);
        check.setHabbitId(dto.getHabbitId());
        check.setCheckDate(dto.getCheckDate());
        if (todoHabbitCheckMapper.countByUserHabitDate(check) > 0) {
            todoHabbitCheckMapper.deleteByUserHabitDate(check);
            return Result.success("已取消打卡");
        }
        todoHabbitCheckMapper.insert(check);
        return Result.success("打卡成功");
    }
}
