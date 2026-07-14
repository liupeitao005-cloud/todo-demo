package com.todo.mapper;

import com.todo.entity.TodoFour;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TodoFourMapperTest extends MapperTestBase {

    @Autowired
    private TodoFourMapper todoFourMapper;

    @Test
    void insertSuccess() {
        Long userId = createUser();
        TodoFour four = newFour(userId);
        int row = todoFourMapper.insert(four);
        assertEquals(1, row);
        assertNotNull(four.getId());
    }

    @Test
    void selectByFourSuccess() {
        Long userId = createUser();
        TodoFour four = newFour(userId);
        todoFourMapper.insert(four);
        TodoFour query = new TodoFour();
        query.setUserId(userId);
        query.setImportance(1);
        query.setUrgency(1);
        List<TodoFour> result = todoFourMapper.selectByFour(query);
        assertFalse(result.isEmpty());
        assertEquals(four.getId(), result.get(0).getId());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals("测试标题", result.get(0).getTitle());
        assertEquals("测试内容", result.get(0).getContent());
    }

    private TodoFour newFour(Long userId) {
        TodoFour four = new TodoFour();
        four.setUserId(userId);
        four.setTitle("测试标题");
        four.setContent("测试内容");
        four.setImportance(1);
        four.setUrgency(1);
        four.setStartTime(LocalDateTime.now());
        four.setFinishTime(LocalDateTime.now().plusHours(1));
        return four;
    }
}
