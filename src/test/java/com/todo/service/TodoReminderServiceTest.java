package com.todo.service;


import com.todo.dto.TodoReminderDTO;
import com.todo.entity.TodoReminder;
import com.todo.mapper.TodoReviewplanMapper;
import com.todo.mapper.TodoReminderMapper;
import com.todo.mapper.TodoScheduleMapper;
import com.todo.mapper.TodoTaskMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import com.todo.vo.TodoReminderVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoReminderServiceTest {
    @Mock
    private  TodoReminderMapper todoReminderMapper;
    @Mock
    private TodoTaskMapper todoTaskMapper;
    @Mock
    private TodoScheduleMapper todoScheduleMapper;
    @Mock
    private TodoReviewplanMapper todoReviewplanMapper;
    @InjectMocks
    private  TodoReminderService todoReminderService;
    @AfterEach
    void tearDown() {UserContext.clear();}
    @Test
    void createReminderFailWhenNotLogin(){
        TodoReminderDTO dto = new TodoReminderDTO();
        Result<String> result = todoReminderService.createReminder(dto);
        assertEquals(400,result.getCode());
        assertEquals("未登录",result.getMessage());
        verify(todoReminderMapper,never()).insert(any(TodoReminder.class));
    }
    @Test
    void createReminderSuccess(){
        UserContext.setUserId(1L);
        TodoReminderDTO dto = new TodoReminderDTO();
        dto.setId(1L);
        dto.setTargetType("task");
        dto.setTargetId(1L);
        dto.setTitle("测试提醒");
        dto.setContent("测试提醒内容");
        dto.setChannel("app");
        dto.setRemindTime(LocalDateTime.now());
        when(todoReminderMapper.insert(any(TodoReminder.class))).thenReturn(1);
        Result<String> result = todoReminderService.createReminder(dto);
        assertEquals(200,result.getCode());
        assertEquals("创建提醒成功",result.getMessage());
        verify(todoReminderMapper).insert(any(TodoReminder.class));
    }
    @Test
    void pendingReminderFailWhenNotLogin() {
        TodoReminderDTO dto = new TodoReminderDTO();
        Result<List<TodoReminderVO>> result = todoReminderService.pendingDesktopReminder();
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoReminderMapper, never()).insert(any(TodoReminder.class));
    }
    @Test
    void pendingReminderSuccess() {
        UserContext.setUserId(1L);
        TodoReminderVO vo = new TodoReminderVO();
        vo.setId(1L);
        vo.setTargetType("task");
        vo.setTargetId(1L);
        vo.setTitle("任务提醒");
        vo.setContent("该做任务了");
        vo.setRemindTime(LocalDateTime.now());
        vo.setChannel("desktop");
        List<TodoReminderVO> list = List.of(vo);
        when(todoReminderMapper.selectPending(any(TodoReminder.class))).thenReturn(list);
        Result<List<TodoReminderVO>> result = todoReminderService.pendingDesktopReminder();
        assertEquals(200, result.getCode());
        assertEquals("查询成功", result.getMessage());
        assertEquals(1, result.getData().size());
        assertEquals("任务提醒", result.getData().get(0).getTitle());
        verify(todoReminderMapper).selectPending(any(TodoReminder.class));
    }
    @Test
    void readReminderFailWhenNotLogin() {
        TodoReminderDTO dto = new TodoReminderDTO();
        Result<String> result = todoReminderService.readReminder(dto);
        assertEquals(400,result.getCode());
        assertEquals("未登录",result.getMessage());
        verify(todoReminderMapper,never()).update(any(TodoReminder.class));
    }
    @Test
    void pendingReminderFailWhenNotFound() {
        UserContext.setUserId(1L);
        TodoReminderDTO dto = new TodoReminderDTO();
        when(todoReminderMapper.update(any(TodoReminder.class))).thenReturn(0);
        Result<String> result = todoReminderService.readReminder(dto);
        assertEquals(400,result.getCode());
        assertEquals("提醒不存在或无权限",result.getMessage());
        verify(todoReminderMapper).update(any(TodoReminder.class));
    }
    @Test
    void readReminderSuccess() {
        UserContext.setUserId(1L);
        TodoReminderDTO dto = new TodoReminderDTO();
        when(todoReminderMapper.update(any(TodoReminder.class))).thenReturn(1);
        Result<String> result = todoReminderService.readReminder(dto);
        assertEquals(200,result.getCode());
        assertEquals("已提醒",result.getMessage());
        verify(todoReminderMapper).update(any(TodoReminder.class));
    }

    @Test
    void processDueServerRemindersMarksOnlyServerDeliveredChannels() {
        TodoReminder desktopReminder = new TodoReminder();
        desktopReminder.setId(1L);
        desktopReminder.setUserId(1L);
        desktopReminder.setChannel("desktop");

        TodoReminder appReminder = new TodoReminder();
        appReminder.setId(2L);
        appReminder.setUserId(1L);
        appReminder.setChannel("app");

        TodoReminder telegramReminder = new TodoReminder();
        telegramReminder.setId(3L);
        telegramReminder.setUserId(1L);
        telegramReminder.setChannel("telegramBot");
        telegramReminder.setTitle("test reminder");

        when(todoReminderMapper.selectDueAll()).thenReturn(List.of(desktopReminder, appReminder, telegramReminder));
        when(todoReminderMapper.update(any(TodoReminder.class))).thenReturn(1);

        int count = todoReminderService.processDueServerReminders();

        assertEquals(1, count);
        verify(todoReminderMapper).update(argThat(reminder -> reminder.getId().equals(3L)));
        verify(todoReminderMapper, never()).update(argThat(reminder -> reminder.getId().equals(1L)));
        verify(todoReminderMapper, never()).update(argThat(reminder -> reminder.getId().equals(2L)));
    }

    @Test
    void cancelByTargetSuccess() {
        todoReminderService.cancelByTarget(1L, "task", 2L);

        verify(todoReminderMapper).cancelByTarget(argThat(reminder ->
                Long.valueOf(1L).equals(reminder.getUserId())
                        && "task".equals(reminder.getTargetType())
                        && Long.valueOf(2L).equals(reminder.getTargetId())
        ));
    }
}
