package com.todo.mapper;

import com.todo.entity.TodoSchedule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TodoScheduleMapperTest extends MapperTestBase{
    @Autowired
    private TodoScheduleMapper todoScheduleMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void insertSuccess() {
        Long userId = createUser();
        TodoSchedule schedule = new TodoSchedule();
        schedule.setUserId(userId);
        schedule.setTitle("测试标题");
        schedule.setContent("测试内容");
        schedule.setLocation("测试地点");
        schedule.setStartTime(LocalDateTime.now());
        schedule.setFinishTime(LocalDateTime.now().plusHours(3));
        int row = todoScheduleMapper.insert(schedule);
        assertEquals(1,row);
        assertNotNull(schedule.getId());
    }

    @Test
    void updateSuccess() {
        Long userId = createUser();
        TodoSchedule schedule = newSchedule(userId);
        todoScheduleMapper.insert(schedule);
        schedule.setTitle("修改标题");
        schedule.setContent("修改内容");
        schedule.setLocation("修改地点");
        schedule.setStartTime(LocalDateTime.now().plusDays(1));
        schedule.setFinishTime(LocalDateTime.now().plusDays(1).plusHours(2));
        int row = todoScheduleMapper.update(schedule);
        assertEquals(1, row);
    }

    @Test
    void deleteSuccess() {
        Long userId = createUser();
        TodoSchedule schedule = newSchedule(userId);
        todoScheduleMapper.insert(schedule);
        int row = todoScheduleMapper.delete(schedule);
        assertEquals(1, row);
    }

    private TodoSchedule newSchedule(Long userId) {
        TodoSchedule schedule = new TodoSchedule();
        schedule.setUserId(userId);
        schedule.setTitle("测试标题");
        schedule.setContent("测试内容");
        schedule.setLocation("测试地点");
        schedule.setStartTime(LocalDateTime.now());
        schedule.setFinishTime(LocalDateTime.now().plusHours(3));
        return schedule;
    }
}
