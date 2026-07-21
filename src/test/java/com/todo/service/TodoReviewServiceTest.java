package com.todo.service;

import com.todo.dto.TodoReviewDTO;
import com.todo.entity.TodoReview;
import com.todo.entity.TodoReviewplan;
import com.todo.mapper.TodoReviewMapper;
import com.todo.mapper.TodoReviewplanMapper;
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
class TodoReviewServiceTest {
    @Mock
    private  TodoReviewMapper todoReviewMapper;
    @Mock
    private  TodoReviewplanMapper todoReviewplanMapper;
    @Mock
    private TodoReminderService todoReminderService;
    @InjectMocks
    private TodoReviewService todoReviewService;
    @AfterEach
    void tearDown() {
        UserContext.clear();
    }
    @Test
    void createTodoReviewFailWhenNotLogin() {
        TodoReviewDTO dto = new TodoReviewDTO();
        Result<String> result = todoReviewService.createReview(dto);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoReviewMapper, never()).insert(any(TodoReview.class));
        verify(todoReviewplanMapper, never()).insert(any(TodoReviewplan.class));
    }
    @Test
    void createReviewSuccess() {
        UserContext.setUserId(1L);
        TodoReviewDTO dto = new TodoReviewDTO();
        dto.setTitle("英语单词");
        dto.setContent("复习 Unit 1");
        when(todoReviewMapper.insert(any(TodoReview.class))).thenAnswer(invocation -> {
            TodoReview review = invocation.getArgument(0);
            review.setId(1L);
            return 1;
        });
        when(todoReviewplanMapper.insert(any(TodoReviewplan.class))).thenReturn(1);
        Result<String> result = todoReviewService.createReview(dto);
        assertEquals(200, result.getCode());
        assertEquals("创建复习任务成功，已生成复习计划", result.getMessage());
        verify(todoReviewMapper).insert(any(TodoReview.class));
        verify(todoReviewplanMapper, times(6)).insert(any(TodoReviewplan.class));
    }
    @Test
    void finishReviewPlanFailWhenNotLogin() {
        Result<String> result = todoReviewService.finishReviewPlan(1L);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoReviewplanMapper,never()).finish(anyLong(),anyLong());
    }
    @Test
    void finishReviewPlanFailWhenNotFound() {
        UserContext.setUserId(1L);
        when(todoReviewplanMapper.finish(anyLong(),anyLong())).thenReturn(0);
        Result<String> result = todoReviewService.finishReviewPlan(1L);
        assertEquals(400, result.getCode());
        assertEquals("复习计划不存在或无权限", result.getMessage());
        verify(todoReviewplanMapper).finish(anyLong(),anyLong());
    }
    @Test
    void finishReviewPlanSuccess() {
        UserContext.setUserId(1L);
        when(todoReviewplanMapper.finish(anyLong(),anyLong())).thenReturn(1);
        Result<String> result = todoReviewService.finishReviewPlan(1L);
        assertEquals(200, result.getCode());
        assertEquals("复习完成记录成功", result.getMessage());
        verify(todoReviewplanMapper).finish(anyLong(),anyLong());
    }

    @Test
    void createReviewSuccessCreatesReviewPlanReminders() {
        UserContext.setUserId(1L);
        TodoReviewDTO dto = new TodoReviewDTO();
        dto.setTitle("review");
        dto.setContent("content");
        when(todoReviewMapper.insert(any(TodoReview.class))).thenAnswer(invocation -> {
            TodoReview review = invocation.getArgument(0);
            review.setId(1L);
            return 1;
        });
        when(todoReviewplanMapper.insert(any(TodoReviewplan.class))).thenAnswer(invocation -> {
            TodoReviewplan plan = invocation.getArgument(0);
            plan.setId(1L);
            return 1;
        });

        todoReviewService.createReview(dto);

        verify(todoReminderService, times(6)).createReminder(argThat(reminder ->
                "review".equals(reminder.getTargetType())
                        && Long.valueOf(1L).equals(reminder.getTargetId())
                        && "desktop".equals(reminder.getChannel())
        ));
    }

    @Test
    void finishReviewPlanSuccessCancelsReminder() {
        UserContext.setUserId(1L);
        when(todoReviewplanMapper.finish(anyLong(), anyLong())).thenReturn(1);

        todoReviewService.finishReviewPlan(1L);

        verify(todoReminderService).cancelByTarget(1L, "review", 1L);
    }
}
