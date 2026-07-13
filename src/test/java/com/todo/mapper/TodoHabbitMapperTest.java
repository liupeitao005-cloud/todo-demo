package com.todo.mapper;

import com.todo.entity.TodoHabbit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TodoHabbitMapperTest extends MapperTestBase {

    @Autowired
    private TodoHabbitMapper todoHabbitMapper;

    @Test
    void insertSuccess() {
        Long userId = createUser();
        TodoHabbit habbit = newHabbit(userId);
        int row = todoHabbitMapper.insert(habbit);
        assertEquals(1, row);
        assertTrue(habbit.getId() > 0);
    }

    private TodoHabbit newHabbit(Long userId) {
        TodoHabbit habbit = new TodoHabbit();
        habbit.setUserId(userId);
        habbit.setTitle("test habbit");
        habbit.setContent("test content");
        habbit.setDayMinutes(30);
        habbit.setMinMinutes(10);
        habbit.setMaxMinutes(60);
        return habbit;
    }
}
