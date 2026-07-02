package com.todo.service;

import com.todo.dto.TodoCalendarDTO;
import com.todo.entity.TodoCalendarItem;
import com.todo.mapper.TodoCalendarMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoCalendarService {
    private final TodoCalendarMapper todoCalendarMapper;

    public Result<List<TodoCalendarItem>> listCalendar(TodoCalendarDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto == null || dto.getStartTime() == null || dto.getFinishTime() == null) {
            return Result.fail("查询时间不能为空");
        }

        List<TodoCalendarItem> list = todoCalendarMapper.listByTime(
                userId,
                dto.getStartTime(),
                dto.getFinishTime()
        );
        return Result.success("查询成功", list);
    }
}
