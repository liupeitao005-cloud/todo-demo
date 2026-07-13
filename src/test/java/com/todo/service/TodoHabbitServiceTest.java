package com.todo.service;


import com.todo.dto.TodoHabbitDTO;
import com.todo.entity.TodoHabbit;
import com.todo.mapper.TodoHabbitMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoHabbitServiceTest {
    @Mock
    private TodoHabbitMapper todoHabbitMapper;
    @InjectMocks
    private TodoHabbitService todoHabbitService;
    @AfterEach
    void tearDown() {UserContext.clear();}

    @Test
    void createHabbitFailWhenNotLogin(){
        TodoHabbitDTO dto =new TodoHabbitDTO();
        Result<String> result = todoHabbitService.createHabbit(dto);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoHabbitMapper,never()).insert(any(TodoHabbit.class));
    }
    @Test
    void createHabbitSuccess(){
        UserContext.setUserId(1L);
        TodoHabbitDTO dto = new TodoHabbitDTO();
        dto.setUserId(1L);
        dto.setTitle("习惯标题");
        dto.setContent("习惯内容");
        dto.setDayMinutes(60);
        dto.setMinMinutes(20);
        dto.setMaxMinutes(30);
        when(todoHabbitMapper.insert(any(TodoHabbit.class))).thenReturn(1);
        Result<String> result = todoHabbitService.createHabbit(dto);
        assertEquals(200, result.getCode());
        assertEquals("创建成功", result.getMessage());
        verify(todoHabbitMapper).insert(any(TodoHabbit.class));
    }
}
