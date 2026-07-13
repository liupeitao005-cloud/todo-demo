package com.todo.service;

import com.todo.dto.TodoCalendarDTO;
import com.todo.mapper.TodoCalendarMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import com.todo.vo.TodoCalendarVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoCalendarService {
    private final TodoCalendarMapper todoCalendarMapper;

    public Result<List<TodoCalendarVO>> listCalendar(TodoCalendarDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (!dto.getStartTime().isBefore(dto.getFinishTime())) {
            return Result.fail("开始时间必须早于结束时间");
        }
        dto.setUserId(userId);
        List<TodoCalendarVO> list = todoCalendarMapper.listByTime(dto);
        return Result.success("查询成功",list);
    }
}
