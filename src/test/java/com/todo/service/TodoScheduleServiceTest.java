package com.todo.service;

import com.todo.dto.TodoScheduleDTO;
import com.todo.entity.TodoSchedule;
import com.todo.mapper.TodoReminderMapper;
import com.todo.mapper.TodoScheduleMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoScheduleServiceTest {
    @Mock
    private TodoScheduleMapper todoScheduleMapper;
    @Mock
    private TodoReminderService todoReminderService;
    @Mock
    private TodoReminderMapper todoReminderMapper;
    @InjectMocks
    private TodoScheduleService todoScheduleService;

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void createTodoScheduleFailWhenNotLogin() {
        TodoScheduleDTO dto = new TodoScheduleDTO();
        Result<String> result = todoScheduleService.createTodoSchedule(dto);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoScheduleMapper, never()).insert(any(TodoSchedule.class));
    }

    @Test
    void createTodoScheduleSuccess() {
        UserContext.setUserId(1L);
        TodoScheduleDTO dto = new TodoScheduleDTO();
        dto.setId(1L);
        dto.setTitle("行程");
        dto.setContent("开会");
        dto.setLocation("上海");
        dto.setStartTime(LocalDateTime.now());
        dto.setFinishTime(LocalDateTime.now().plusHours(3));
        when(todoScheduleMapper.insert(any(TodoSchedule.class))).thenReturn(1);
        Result<String> result = todoScheduleService.createTodoSchedule(dto);
        verify(todoScheduleMapper).insert(any(TodoSchedule.class));
    }

    @Test
    void updateTodoScheduleFailWhenNotLogin() {
        TodoScheduleDTO dto = new TodoScheduleDTO();
        Result<String> result = todoScheduleService.updateTodoSchedule(dto);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoScheduleMapper, never()).update(any(TodoSchedule.class));
    }

    @Test
    void updateTodoScheduleFailWhenNotFound() {
        UserContext.setUserId(1L);
        TodoScheduleDTO dto = new TodoScheduleDTO();
        when(todoScheduleMapper.update(any(TodoSchedule.class))).thenReturn(0);
        Result<String> result = todoScheduleService.updateTodoSchedule(dto);
        assertEquals(400, result.getCode());
        assertEquals("行程不存在或无权限修改", result.getMessage());
        verify(todoScheduleMapper).update(any(TodoSchedule.class));
    }

    @Test
    void updateTodoSchedulSuccess() {
        UserContext.setUserId(1L);
        TodoScheduleDTO dto = new TodoScheduleDTO();
        dto.setId(1L);
        dto.setTitle("修改标题");
        dto.setContent("修改内容");
        dto.setLocation("修改地点");
        dto.setStartTime(LocalDateTime.now());
        dto.setFinishTime(LocalDateTime.now().plusHours(3));
        when(todoScheduleMapper.update(any(TodoSchedule.class))).thenReturn(1);
        Result<String> result = todoScheduleService.updateTodoSchedule(dto);
        assertEquals(200, result.getCode());
        assertEquals("修改成功", result.getMessage());
        verify(todoScheduleMapper).update(any(TodoSchedule.class));
    }

    @Test
    void updateTodoScheduleSuccessUpdatesReminder() {
        UserContext.setUserId(1L);
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        TodoScheduleDTO dto = new TodoScheduleDTO();
        dto.setId(1L);
        dto.setTitle("schedule");
        dto.setContent("content");
        dto.setLocation("location");
        dto.setStartTime(startTime);
        dto.setFinishTime(startTime.plusHours(1));
        when(todoScheduleMapper.update(any(TodoSchedule.class))).thenReturn(1);

        todoScheduleService.updateTodoSchedule(dto);

        verify(todoReminderMapper).updateByTarget(argThat(reminder ->
                "schedule".equals(reminder.getTargetType())
                        && Long.valueOf(1L).equals(reminder.getTargetId())
                        && "desktop".equals(reminder.getChannel())
                        && startTime.minusMinutes(5).equals(reminder.getRemindTime())
        ));
    }

    @Test
    void deleteTodoScheduleFailWhenNotLogin() {
        TodoScheduleDTO dto = new TodoScheduleDTO();
        Result<String> result = todoScheduleService.deleteTodoSchedule(1L);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoScheduleMapper, never()).delete(any(TodoSchedule.class));
    }
    @Test
    void deleteTodoScheduleFailWhenNotFound(){
        UserContext.setUserId(1L);
        TodoScheduleDTO dto = new TodoScheduleDTO();
        when(todoScheduleMapper.delete(any(TodoSchedule.class))).thenReturn(0);
        Result<String> result = todoScheduleService.deleteTodoSchedule(1L);
        assertEquals(400, result.getCode());
        assertEquals("行程不存在或无权限删除", result.getMessage());
        verify(todoScheduleMapper).delete(any(TodoSchedule.class));
    }
    @Test
    void deleteTodoSchedulSuccess(){
        UserContext.setUserId(1L);
        TodoScheduleDTO dto =new TodoScheduleDTO();
        when(todoScheduleMapper.delete(any(TodoSchedule.class))).thenReturn(1);
        Result<String> result = todoScheduleService.deleteTodoSchedule(1L);
        assertEquals(200, result.getCode());
        assertEquals("删除成功", result.getMessage());
        verify(todoScheduleMapper).delete(any(TodoSchedule.class));
    }
    @Test
    void createTodoScheduleSuccessCreatesReminder() {
        UserContext.setUserId(1L);
        TodoScheduleDTO dto = new TodoScheduleDTO();
        dto.setTitle("schedule");
        dto.setContent("content");
        dto.setStartTime(LocalDateTime.now());
        dto.setFinishTime(LocalDateTime.now().plusHours(1));
        when(todoScheduleMapper.insert(any(TodoSchedule.class))).thenAnswer(invocation -> {
            TodoSchedule schedule = invocation.getArgument(0);
            schedule.setId(1L);
            return 1;
        });

        todoScheduleService.createTodoSchedule(dto);

        verify(todoReminderService).createReminder(argThat(reminder ->
                "schedule".equals(reminder.getTargetType())
                        && Long.valueOf(1L).equals(reminder.getTargetId())
                        && "desktop".equals(reminder.getChannel())
                        && dto.getStartTime().minusMinutes(5).equals(reminder.getRemindTime())
        ));
    }

    @Test
    void deleteTodoScheduleSuccessCancelsReminder() {
        UserContext.setUserId(1L);
        when(todoScheduleMapper.delete(any(TodoSchedule.class))).thenReturn(1);

        todoScheduleService.deleteTodoSchedule(1L);

        verify(todoReminderService).cancelByTarget(1L, "schedule", 1L);
    }
}

