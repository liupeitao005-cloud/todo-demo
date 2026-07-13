package com.todo.mapper;

import com.todo.entity.TodoReminder;
import com.todo.vo.TodoReminderVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TodoReminderMapperTest extends MapperTestBase {

    @Autowired
    private TodoReminderMapper todoReminderMapper;

    @Test
    void insertSuccess() {
        Long userId = createUser();
        TodoReminder reminder = newReminder(userId);
        int row = todoReminderMapper.insert(reminder);
        assertEquals(1, row);
        assertNotNull(reminder.getId());
    }

    @Test
    void selectPendingSuccess() {
        Long userId = createUser();
        TodoReminder reminder = newReminder(userId);
        todoReminderMapper.insert(reminder);
        TodoReminder query = new TodoReminder();
        query.setUserId(userId);
        query.setChannel("desktop");
        List<TodoReminderVO> result = todoReminderMapper.selectPending(query);
        assertFalse(result.isEmpty());
        assertEquals(reminder.getId(), result.get(0).getId());
        assertEquals("task", result.get(0).getTargetType());
        assertEquals("test reminder", result.get(0).getTitle());
    }

    @Test
    void updateSuccess() {
        Long userId = createUser();
        TodoReminder reminder = newReminder(userId);
        todoReminderMapper.insert(reminder);
        int row = todoReminderMapper.update(reminder);
        assertEquals(1, row);
    }

    private TodoReminder newReminder(Long userId) {
        TodoReminder reminder = new TodoReminder();
        reminder.setUserId(userId);
        reminder.setTargetType("task");
        reminder.setTargetId(1L);
        reminder.setTitle("test reminder");
        reminder.setContent("test content");
        reminder.setRemindTime(LocalDateTime.now().minusMinutes(1));
        reminder.setChannel("desktop");
        return reminder;
    }
}
