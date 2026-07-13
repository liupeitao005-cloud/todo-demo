package com.todo.mapper;

import com.todo.entity.TodoTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoTaskMapperTest extends MapperTestBase{
    @Autowired
    private TodoTaskMapper todoTaskMapper;
    @Test
    void insertSuccess() {
        Long userId = createUser();
        TodoTask task = newTask(userId);
        int row = todoTaskMapper.insert(task);
        assertEquals(1,row);
        assertNotNull(task.getId());
    }
    @Test
    void updateSuccess() {
        Long userId = createUser();
        TodoTask task = newTask(userId);
        todoTaskMapper.insert(task);
        task.setTitle("测试");
        task.setContent("测试");
        task.setTaskType("long");
        int row = todoTaskMapper.update(task);
        assertEquals(1,row);
    }
    @Test
    void deleteSuccess() {
        Long userId = createUser();
        TodoTask task = newTask(userId);
        todoTaskMapper.insert(task);
        task.setTitle("测试");
        task.setContent("测试");
        task.setTaskType("long");
        int row = todoTaskMapper.delete(task);
        assertEquals(1,row);
    }
    @Test
    void finishSuccess() {
        Long userId = createUser();
        TodoTask task = newTask(userId);
        todoTaskMapper.insert(task);
        int row = todoTaskMapper.finish(task);
        assertEquals(1,row);
    }
    @Test
    void nextSuccess() {
        Long userId = createUser();
        TodoTask task = newTask(userId);
        todoTaskMapper.insert(task);
        int row = todoTaskMapper.next(task);
        assertEquals(1, row);
    }
    @Test
    void yanqiSuccess() {
        Long userId = createUser();
        TodoTask task = newTask(userId);
        todoTaskMapper.insert(task);
        int row = todoTaskMapper.yanqi(task);
        assertEquals(1, row);
    }
    @Test
    void selectSuccess() {
        Long userId = createUser();
        TodoTask task = newTask(userId);
        todoTaskMapper.insert(task);
        TodoTask query = new TodoTask();
        query.setId(task.getId());
        query.setUserId(userId);
        TodoTask result = todoTaskMapper.selectByID(query);
        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals("测试标题", result.getTitle());
        assertEquals("测试内容", result.getContent());
    }
    private TodoTask newTask(Long userId) {
        TodoTask task = new TodoTask();
        task.setUserId(userId);
        task.setTitle("测试标题");
        task.setContent("测试内容");
        task.setTaskType("task");
        task.setParentId(0L);
        task.setStartTime(LocalDateTime.now());
        task.setFinishTime(LocalDateTime.now().plusHours(1));
        return task;
    }
}
