package com.todo.service;

import com.todo.dto.TodoBacklogDTO;
import com.todo.entity.TodoBacklog;
import com.todo.entity.TodoTask;
import com.todo.mapper.TodoBacklogMapper;
import com.todo.mapper.TodoTaskMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoBacklogServiceTest {

    @Mock
    private TodoBacklogMapper todoBacklogMapper;

    @Mock
    private TodoTaskMapper todoTaskMapper;

    @InjectMocks
    private TodoBacklogService todoBacklogService;

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }


    @Test
    void createBacklogFailWhenNotLogin() {
        TodoBacklogDTO dto = new TodoBacklogDTO();
        Result<String> result = todoBacklogService.createtodoBacklog(dto);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoBacklogMapper, never()).insert(any(TodoBacklog.class));
    }

    @Test
    void createBacklogSuccess() {
        UserContext.setUserId(1L);
        TodoBacklogDTO dto = new TodoBacklogDTO();
        dto.setTitle("整理项目资料");
        dto.setContent("补充资料");
        when(todoBacklogMapper.insert(any(TodoBacklog.class))).thenReturn(1);
        Result<String> result = todoBacklogService.createtodoBacklog(dto);
        assertEquals(200, result.getCode());
        assertEquals("创建成功", result.getMessage());
        verify(todoBacklogMapper).insert(any(TodoBacklog.class));
    }
    @Test
    void updateBacklogFailWhenNotLogin() {
        TodoBacklogDTO dto = new TodoBacklogDTO();
        Result<String> result = todoBacklogService.updateBacklog(dto);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoBacklogMapper, never()).update(any(TodoBacklog.class));
    }

    @Test
    void updateBacklogFailWhenBacklogNotFound(){
        UserContext.setUserId(1L);
        TodoBacklogDTO dto = new TodoBacklogDTO();
        dto.setId(1L);
        when(todoBacklogMapper.update(any(TodoBacklog.class))).thenReturn(0);
        Result<String> result = todoBacklogService.updateBacklog(dto);
        assertEquals(400, result.getCode());
        assertEquals("待办不存在或无权限修改", result.getMessage());
        verify(todoBacklogMapper).update(any(TodoBacklog.class));
    }
    @Test
    void updateBacklogSuccess(){
        UserContext.setUserId(1L);
        TodoBacklogDTO dto = new TodoBacklogDTO();
        dto.setId(1L);
        dto.setTitle("修改");
        dto.setContent("修改代办内容");
        when(todoBacklogMapper.update(any(TodoBacklog.class))).thenReturn(1);
        Result<String> result = todoBacklogService.updateBacklog(dto);
        assertEquals(200, result.getCode());
        assertEquals("修改成功", result.getMessage());
        verify(todoBacklogMapper).update(any(TodoBacklog.class));
    }
    @Test
    void moveBacklogFailWhenNotLogin() {
        TodoBacklogDTO dto = new TodoBacklogDTO();
        Result<String> result = todoBacklogService.moveTodoBacklog(dto);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoBacklogMapper,never()).selectById(any(TodoBacklog.class));
        verify(todoBacklogMapper, never()).insert(any(TodoBacklog.class));
    }
    @Test
    void moveBacklogFailNotFound(){
        UserContext.setUserId(1L);
        TodoBacklogDTO dto = new TodoBacklogDTO();
        dto.setId(1L);
        dto.setStartTime(LocalDateTime.now());
        dto.setFinishTime(LocalDateTime.now().plusHours(2));
        when(todoBacklogMapper.selectById(any(TodoBacklog.class))).thenReturn(null);
        Result<String> result = todoBacklogService.moveTodoBacklog(dto);
        assertEquals(400, result.getCode());
        assertEquals("待办不存在或无权限操作", result.getMessage());
        verify(todoBacklogMapper).selectById(any(TodoBacklog.class));
        verify(todoTaskMapper,never()).insert(any(TodoTask.class));
    }
    @Test
    void moveBacklogSuccess(){
        UserContext.setUserId(1L);
        TodoBacklogDTO dto = new TodoBacklogDTO();
        TodoBacklog backlog = new TodoBacklog();
        dto.setId(1L);
        dto.setStartTime(LocalDateTime.now());
        dto.setFinishTime(LocalDateTime.now().plusHours(2));
        backlog.setTitle("测试标题");
        backlog.setContent("测试内容");
        when(todoBacklogMapper.selectById(any(TodoBacklog.class))).thenReturn(backlog);
        when(todoBacklogMapper.delete(any(TodoBacklog.class))).thenReturn(1);
        when(todoTaskMapper.insert(any(TodoTask.class))).thenReturn(1);
        Result<String> result = todoBacklogService.moveTodoBacklog(dto);
        assertEquals(200,result.getCode());
        assertEquals("拖拽到日程成功", result.getMessage());
        verify(todoTaskMapper).insert(any(TodoTask.class));
        verify(todoBacklogMapper).delete(any(TodoBacklog.class));
    }
    @Test
    void selectFailWhenNotLogin() {
        Result<List<TodoBacklog>> result = todoBacklogService.listBacklog();
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoBacklogMapper,never()).listByUserId(anyLong());
    }
    @Test
    void selectSuccess(){
        UserContext.setUserId(1L);
        TodoBacklog backlog = new TodoBacklog();
        backlog.setId(1L);
        backlog.setUserId(1L);
        backlog.setTitle("测试标题");
        backlog.setContent("测试内容");
        when(todoBacklogMapper.listByUserId(1L)).thenReturn(List.of());
        Result<List<TodoBacklog>> list = todoBacklogService.listBacklog();
        assertEquals(200, list.getCode());
        assertEquals("查询成功", list.getMessage());
        verify(todoBacklogMapper).listByUserId(1L);
    }

}
