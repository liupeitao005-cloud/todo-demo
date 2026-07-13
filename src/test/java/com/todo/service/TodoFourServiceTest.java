package com.todo.service;


import com.todo.dto.TodoFourDTO;
import com.todo.entity.TodoFour;
import com.todo.entity.TodoTask;
import com.todo.mapper.TodoFourMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoFourServiceTest {
    @Mock
    private TodoFourMapper todoFourMapper;
    @Mock
    private TodoTaskMapper todoTaskMapper;
    @InjectMocks
    private  TodoFourService todoFourService;
    @AfterEach
    void tearDown() {
        UserContext.clear();
    }
    @Test
    void moveTodoFourFailWhenNotLogin(){
        TodoFourDTO dto = new TodoFourDTO();
        Result<String> result = todoFourService.moveTodoFour(dto);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoFourMapper,never()).insert(any(TodoFour.class));
        verify(todoTaskMapper,never()).selectByID(any(TodoTask.class));
    }
    @Test
    void moveTodoFourFailWhenNotFound(){
        UserContext.setUserId(1L);
        TodoFourDTO dto = new TodoFourDTO();
        dto.setId(1L);
        dto.setImportance(1);
        dto.setUrgency(1);
        when(todoTaskMapper.selectByID(any(TodoTask.class))).thenReturn(null);
        Result<String> result = todoFourService.moveTodoFour(dto);
        assertEquals(400, result.getCode());
        assertEquals("任务不存在或无权限操作", result.getMessage());
        verify(todoTaskMapper).selectByID(any(TodoTask.class));
        verify(todoFourMapper, never()).insert(any(TodoFour.class));
    }
    @Test
    void  moveTodoFourSuccess(){
        UserContext.setUserId(1L);
        TodoFourDTO dto = new TodoFourDTO();
        dto.setId(1L);
        dto.setImportance(1);
        dto.setUrgency(0);
        TodoTask task =new TodoTask();
        task.setId(1L);
        task.setTitle("测试任务");
        task.setContent("测试内容");
        task.setStartTime(LocalDateTime.now());
        task.setFinishTime(LocalDateTime.now().plusHours(2));
        when(todoFourMapper.insert(any(TodoFour.class))).thenReturn(1);
        when(todoTaskMapper.selectByID(any(TodoTask.class))).thenReturn(task);
        Result<String> result = todoFourService.moveTodoFour(dto);
        assertEquals(200, result.getCode());
        assertEquals("放入四象限成功",result.getMessage());
        verify(todoTaskMapper).selectByID(any(TodoTask.class));
        verify(todoFourMapper).insert(any(TodoFour.class));
    }
    @Test
    void selectTodoFourFailWhenNotLogin(){
        TodoFourDTO dto = new TodoFourDTO();
        Result<List<TodoFour>> result = todoFourService.listTodoFour(dto);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoTaskMapper,never()).selectByID(any(TodoTask.class));
    }
    @Test
    void selectTodoFourSuccess(){
        UserContext.setUserId(1L);
        TodoFourDTO dto = new TodoFourDTO();
        dto.setImportance(1);
        dto.setUrgency(1);
        TodoFour four = new TodoFour();
        four.setId(1L);
        four.setTitle("测试");
        four.setContent("测试内容");
        four.setImportance(1);
        four.setUrgency(1);
        four.setStartTime(LocalDateTime.now());
        four.setFinishTime(LocalDateTime.now().plusHours(2));
        List<TodoFour> list = List.of(four);
        when(todoFourMapper.selectByFour(any(TodoFour.class))).thenReturn(list);
        Result<List<TodoFour>> result = todoFourService.listTodoFour(dto);
        assertEquals(200, result.getCode());
        assertEquals("查询成功",result.getMessage());
        verify(todoFourMapper).selectByFour(any(TodoFour.class));
    }
}
