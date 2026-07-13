package com.todo.service;


import com.todo.dto.TodoCalendarDTO;
import com.todo.mapper.TodoCalendarMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import com.todo.vo.TodoCalendarVO;
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
class TodoCalendarServiceTest {
    @Mock
    private TodoCalendarMapper todoCalendarMapper;
    @InjectMocks
    private TodoCalendarService todoCalendarService;
    @AfterEach
    void tearDown() {
        UserContext.clear();
    }
    @Test
    void selectTodoCalendarFailWhenNotLogin(){
        TodoCalendarDTO dto = new TodoCalendarDTO();
        Result<List<TodoCalendarVO>> result = todoCalendarService.listCalendar(dto);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoCalendarMapper,never()).listByTime(any(TodoCalendarDTO.class));
    }
    @Test
    void selectTodoCalendarSuccess(){
        UserContext.setUserId(1L);
        TodoCalendarDTO dto = new TodoCalendarDTO();
        dto.setStartTime(LocalDateTime.now());
        dto.setFinishTime(LocalDateTime.now().plusDays(1));
        TodoCalendarVO vo = new TodoCalendarVO();
        vo.setId(1L);
        vo.setTitle("测试日程");
        vo.setContent("测试内容");
        vo.setStartTime(dto.getStartTime());
        vo.setFinishTime(dto.getFinishTime());
        vo.setItemType("task");
        List<TodoCalendarVO> list = List.of(vo);
        when(todoCalendarMapper.listByTime(any(TodoCalendarDTO.class))).thenReturn(list);
        Result<List<TodoCalendarVO>> result = todoCalendarService.listCalendar(dto);
        assertEquals(200, result.getCode());
        assertEquals("查询成功", result.getMessage());
        assertEquals(1, result.getData().size());
        assertEquals("测试日程", result.getData().get(0).getTitle());
        verify(todoCalendarMapper).listByTime(any(TodoCalendarDTO.class));
    }
}
