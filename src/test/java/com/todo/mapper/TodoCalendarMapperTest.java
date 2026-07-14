package com.todo.mapper;

import com.todo.dto.TodoCalendarDTO;
import com.todo.entity.TodoSchedule;
import com.todo.entity.TodoTask;
import com.todo.vo.TodoCalendarVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TodoCalendarMapperTest extends MapperTestBase {

    @Autowired
    private TodoCalendarMapper todoCalendarMapper;
    @Autowired
    private TodoScheduleMapper todoScheduleMapper;
    @Autowired
    private TodoTaskMapper todoTaskMapper;

    @Test
    void listByTimeSuccess() {
        Long userId = createUser();
        TodoSchedule schedule = newSchedule(userId);
        todoScheduleMapper.insert(schedule);
        TodoTask task = newTask(userId);
        todoTaskMapper.insert(task);
        TodoCalendarDTO query = new TodoCalendarDTO();
        query.setUserId(userId);
        query.setStartTime(LocalDateTime.now().minusHours(1));
        query.setFinishTime(LocalDateTime.now().plusHours(2));
        List<TodoCalendarVO> result = todoCalendarMapper.listByTime(query);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(item -> item.getId().equals(schedule.getId()) && item.getItemType().equals("schedule")));
        assertTrue(result.stream().anyMatch(item -> item.getId().equals(task.getId()) && item.getItemType().equals("task")));
    }

    private TodoSchedule newSchedule(Long userId) {
        TodoSchedule schedule = new TodoSchedule();
        schedule.setUserId(userId);
        schedule.setTitle("测试标题");
        schedule.setContent("测试内容");
        schedule.setLocation("测试地点");
        schedule.setStartTime(LocalDateTime.now());
        schedule.setFinishTime(LocalDateTime.now().plusHours(1));
        return schedule;
    }

    private TodoTask newTask(Long userId) {
        TodoTask task = new TodoTask();
        task.setUserId(userId);
        task.setTitle("测试标题");
        task.setContent("测试内容");
        task.setTaskType("task");
        task.setParentId(0L);
        task.setStartTime(LocalDateTime.now());
        task.setFinishTime(LocalDateTime.now().plusHours(1));
        return task;
    }
}
