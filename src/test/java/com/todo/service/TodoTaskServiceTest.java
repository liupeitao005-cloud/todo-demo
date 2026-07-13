package com.todo.service;
import com.todo.dto.TodoTaskDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoTaskServiceTest {
    @Mock
    private  TodoTaskMapper todoTaskMapper;
    @Mock
    private TodoBacklogMapper todoBacklogMapper;
    @InjectMocks
    private  TodoTaskService todoTaskService;
    @AfterEach
    public void tearDown() {
        UserContext.clear();
    }

    @Test
    void createTodoTaskFailWhenNotLogin() {
        TodoTaskDTO dto = new TodoTaskDTO();
        Result<String> result = todoTaskService.createTodoTask(dto);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoTaskMapper, never()).insert(any(TodoTask.class));
    }

    @Test
    void createTaskSuccess(){
        UserContext.setUserId(1L);
        TodoTaskDTO dto = new TodoTaskDTO();
        dto.setTitle("完成任务测试");
        dto.setContent("测试创建任务");
        dto.setTaskType("长期任务");
        dto.setStartTime(LocalDateTime.now());
        dto.setFinishTime(LocalDateTime.now().plusHours(2));
        when(todoTaskMapper.insert(any(TodoTask.class))).thenReturn(1);
        Result<String> result = todoTaskService.createTodoTask(dto);
        assertEquals(200, result.getCode());
        assertEquals("创建成功", result.getMessage());
        verify(todoTaskMapper).insert(any(TodoTask.class));

    }
    @Test
    void updateTodoTaskFailWhenNotLogin() {
        TodoTaskDTO dto = new TodoTaskDTO();
        Result<String> result = todoTaskService.updateTodoTask(dto);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoTaskMapper, never()).update(any(TodoTask.class));
    }
    @Test
    void updateTaskFailWhenNotFound(){
        UserContext.setUserId(1L);
        TodoTaskDTO dto = new TodoTaskDTO();
        dto.setId(1L);
        when(todoTaskMapper.update(any(TodoTask.class))).thenReturn(0);
        Result<String> result = todoTaskService.updateTodoTask(dto);
        assertEquals(400, result.getCode());
        assertEquals("任务不存在或无权限修改", result.getMessage());
        verify(todoTaskMapper).update(any(TodoTask.class));
    }
    @Test
    void updateTaskSuccess(){
        UserContext.setUserId(1L);
        TodoTaskDTO dto = new TodoTaskDTO();
        dto.setId(1L);
        dto.setTitle("修改测试任务");
        dto.setContent("修改测试任务内容");
        dto.setTaskType("修改任务类别");
        when(todoTaskMapper.update(any(TodoTask.class))).thenReturn(1);
        Result<String> result = todoTaskService.updateTodoTask(dto);
        assertEquals(200, result.getCode());
        assertEquals("修改成功", result.getMessage());
        verify(todoTaskMapper).update(any(TodoTask.class));
    }
    @Test
    void deleteTodoTaskFailWhenNotLogin() {
        Result<String> result = todoTaskService.deleteTodoTask(1L);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoTaskMapper, never()).delete(any(TodoTask.class));
    }
    @Test
    void deleteTaskFailWhenNotFound(){
        UserContext.setUserId(1L);
        when(todoTaskMapper.delete(any(TodoTask.class))).thenReturn(0);
        Result<String> result = todoTaskService.deleteTodoTask(1L);
        assertEquals(400, result.getCode());
        assertEquals("任务不存在或无权限删除", result.getMessage());
        verify(todoTaskMapper).delete(any(TodoTask.class));

    }
    @Test
    void deleteTaskSuccess(){
        UserContext.setUserId(1L);
        when(todoTaskMapper.delete(any(TodoTask.class))).thenReturn(1);
        Result<String> result = todoTaskService.deleteTodoTask(1L);
        assertEquals(200, result.getCode());
        assertEquals("删除成功", result.getMessage());
        verify(todoTaskMapper).delete(any(TodoTask.class));
    }
    @Test
    void finishTaskFailWhenNotLogin() {
        Result<String> result = todoTaskService.finishTodoTask(1L);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoTaskMapper,never()).finish(any(TodoTask.class));
    }
    @Test
    void finishTaskFailWhenNotFound() {
        UserContext.setUserId(1L);
        when(todoTaskMapper.finish(any(TodoTask.class))).thenReturn(0);
        Result<String> result = todoTaskService.finishTodoTask(1L);
        assertEquals(400, result.getCode());
        assertEquals("任务不存在或无权限完成", result.getMessage());
        verify(todoTaskMapper).finish(any(TodoTask.class));
    }
    @Test
    void finishTaskSuccess(){
        UserContext.setUserId(1L);
        when(todoTaskMapper.finish(any(TodoTask.class))).thenReturn(1);
        Result<String> result = todoTaskService.finishTodoTask(1L);
        assertEquals(200, result.getCode());
        assertEquals("完成成功", result.getMessage());
        verify(todoTaskMapper).finish(any(TodoTask.class));
    }
    @Test
    void qiegeHourFailWhenNotLogin(){
        Result<String> result = todoTaskService.qiegehour(1L);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoTaskMapper,never()).selectByID(any(TodoTask.class));
        verify(todoTaskMapper,never()).insert(any( TodoTask.class));
    }
    @Test
    void qiegeHourFailWhenNotFound(){
        UserContext.setUserId(1L);
        when(todoTaskMapper.selectByID(any(TodoTask.class))).thenReturn(null);
        Result<String> result = todoTaskService.qiegehour(1L);
        assertEquals(400, result.getCode());
        assertEquals("任务不存在", result.getMessage());
        verify(todoTaskMapper).selectByID(any(TodoTask.class));
        verify(todoTaskMapper,never()).insert(any(TodoTask.class));
    }
    @Test
    void qiegeHourFailWhenNotTime(){
        UserContext.setUserId(1L);
        TodoTask parent = new TodoTask();
        parent.setId(1L);
        parent.setTitle("测试任务");
        parent.setContent("测试内容");
        parent.setTaskType("长期任务");
        parent.setStartTime(null);
        parent.setFinishTime(null);
        when(todoTaskMapper.selectByID(any(TodoTask.class))).thenReturn(parent);
        Result<String> result = todoTaskService.qiegehour(1L);
        assertEquals(400, result.getCode());
        assertEquals("任务开始时间和结束时间不能为空", result.getMessage());
        verify(todoTaskMapper).selectByID(any(TodoTask.class));
        verify(todoTaskMapper,never()).insert(any(TodoTask.class));
    }
    @Test
    void qiegeHourFailWhenTimeLessThanFourHours(){
        UserContext.setUserId(1L);
        TodoTask parent = new TodoTask();
        parent.setId(1L);
        parent.setTitle("测试任务");
        parent.setContent("测试内容");
        parent.setTaskType("长期任务");
        parent.setStartTime(LocalDateTime.now());
        parent.setFinishTime(LocalDateTime.now().plusHours(2));
        when(todoTaskMapper.selectByID(any(TodoTask.class))).thenReturn(parent);
        Result<String> result = todoTaskService.qiegehour(1L);
        assertEquals(400, result.getCode());
        assertEquals("时间小于4小时不需要拆分", result.getMessage());
        verify(todoTaskMapper).selectByID(any(TodoTask.class));
        verify(todoTaskMapper,never()).insert(any(TodoTask.class));
    }
    @Test
    void qiegeHourSuccess(){
        UserContext.setUserId(1L);
        TodoTask parent = new TodoTask();
        parent.setId(1L);
        parent.setTitle("测试任务");
        parent.setContent("测试内容");
        parent.setTaskType("长期任务");
        parent.setStartTime(LocalDateTime.now());
        parent.setFinishTime(LocalDateTime.now().plusHours(4));
        when(todoTaskMapper.selectByID(any(TodoTask.class))).thenReturn(parent);
        when(todoTaskMapper.insert(any(TodoTask.class))).thenReturn(1);
        Result<String> result = todoTaskService.qiegehour(1L);
        assertEquals(200, result.getCode());
        assertEquals("拆分成功", result.getMessage());
        verify(todoTaskMapper).selectByID(any(TodoTask.class));
        verify(todoTaskMapper,times(4)).insert(any(TodoTask.class));
    }
    @Test
    void yanqiFailWhenNotLogin(){
        Result<String> result = todoTaskService.yanqi(1L);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoTaskMapper,never()).selectByID(any(TodoTask.class));
        verify(todoTaskMapper,never()).update(any(TodoTask.class));
    }
    @Test
    void yanqiFailWhenNotFound(){
        UserContext.setUserId(1L);
        when(todoTaskMapper.selectByID(any(TodoTask.class))).thenReturn(null);
        Result<String> result = todoTaskService.yanqi(1L);
        assertEquals(400, result.getCode());
        assertEquals("任务不存在或无权限操作", result.getMessage());
        verify(todoTaskMapper).selectByID(any(TodoTask.class));
        verify(todoTaskMapper,never()).yanqi(any(TodoTask.class));
    }
    @Test
    void yanqiFailWhenIsFinish(){
        UserContext.setUserId(1L);
        TodoTask task = new TodoTask();
        task.setId(1L);
        task.setIsFinish(1);
        when(todoTaskMapper.selectByID(any(TodoTask.class))).thenReturn(task);
        Result<String> result = todoTaskService.yanqi(1L);
        assertEquals(400, result.getCode());
        assertEquals("任务已完成，不需要延期", result.getMessage());
        verify(todoTaskMapper).selectByID(any(TodoTask.class));
        verify(todoTaskMapper,never()).yanqi(any(TodoTask.class));
    }
    @Test
    void yanqiFailWhenNotTime(){
        UserContext.setUserId(1L);
        TodoTask task = new TodoTask();
        task.setId(1L);
        task.setIsFinish(0);
        task.setStartTime(null);
        task.setFinishTime(null);
        when(todoTaskMapper.selectByID(any(TodoTask.class))).thenReturn(task);
        Result<String> result = todoTaskService.yanqi(1L);
        assertEquals(400, result.getCode());
        assertEquals("任务开始时间和结束时间不能为空", result.getMessage());
        verify(todoTaskMapper,never()).yanqi(any(TodoTask.class));
        verify(todoTaskMapper).selectByID(any(TodoTask.class));
    }
    @Test
    void yanqiFailWhenTimeNotTimeout(){
        UserContext.setUserId(1L);
        TodoTask task = new TodoTask();
        task.setId(1L);
        task.setIsFinish(0);
        task.setStartTime(LocalDateTime.now());
        task.setFinishTime(LocalDateTime.now().plusHours(2));
        when(todoTaskMapper.selectByID(any(TodoTask.class))).thenReturn(task);
        Result<String> result = todoTaskService.yanqi(1L);
        assertEquals(400, result.getCode());
        assertEquals("任务还没超时，不需要延期", result.getMessage());
        verify(todoTaskMapper,never()).yanqi(any(TodoTask.class));
        verify(todoTaskMapper).selectByID(any(TodoTask.class));
    }
    @Test
    void yanqiFailWhenUpdateFailWhenNotFound(){
        UserContext.setUserId(1L);
        TodoTask task = new TodoTask();
        task.setId(1L);
        task.setIsFinish(0);
        task.setStartTime(LocalDateTime.now().minusHours(3));
        task.setFinishTime(LocalDateTime.now().minusHours(1));
        when(todoTaskMapper.selectByID(any(TodoTask.class))).thenReturn(task);
        when(todoTaskMapper.yanqi(any(TodoTask.class))).thenReturn(0);
        Result<String> result  =  todoTaskService.yanqi(1L);
        assertEquals(400, result.getCode());
        assertEquals("延期失败", result.getMessage());
        verify(todoTaskMapper).yanqi(any(TodoTask.class));
    }
    @Test
    void yanqiSuccess(){
        UserContext.setUserId(1L);
        TodoTask task = new TodoTask();
        task.setId(1L);
        task.setIsFinish(0);
        task.setStartTime(LocalDateTime.now().minusHours(3));
        task.setFinishTime(LocalDateTime.now().minusHours(1));
        when(todoTaskMapper.selectByID(any(TodoTask.class))).thenReturn(task);
        when(todoTaskMapper.yanqi(any(TodoTask.class))).thenReturn(1);
        Result<String> result = todoTaskService.yanqi(1L);
        assertEquals(200, result.getCode());
        assertEquals("延期成功", result.getMessage());
        verify(todoTaskMapper).selectByID(any(TodoTask.class));
        verify(todoTaskMapper).yanqi(any(TodoTask.class));
    }
    @Test
    void nextFailWhenNotLogin(){
        Result<String> result = todoTaskService.nextTodoTask(1L);
        assertEquals(400, result.getCode());
        assertEquals("未登录", result.getMessage());
        verify(todoTaskMapper,never()).next(any(TodoTask.class));
    }
    @Test
    void nextFailWhenNotFound(){
        UserContext.setUserId(1L);
        when(todoTaskMapper.next(any(TodoTask.class))).thenReturn(0);
        Result<String> result = todoTaskService.nextTodoTask(1L);
        assertEquals(400, result.getCode());
        assertEquals("任务不存在、已完成或无权限操作", result.getMessage());
        verify(todoTaskMapper).next(any(TodoTask.class));
    }
    @Test
    void nextSuccess(){
        UserContext.setUserId(1L);
        TodoTask task = new TodoTask();
        when(todoTaskMapper.next(any(TodoTask.class))).thenReturn(1);
        Result<String> result = todoTaskService.nextTodoTask(1L);
        assertEquals(200, result.getCode());
        assertEquals("设置成功", result.getMessage());
        verify(todoTaskMapper).next(any(TodoTask.class));

    }

}
