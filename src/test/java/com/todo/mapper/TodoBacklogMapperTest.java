package com.todo.mapper;

import com.todo.entity.TodoBacklog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TodoBacklogMapperTest extends MapperTestBase {

    @Autowired
    private TodoBacklogMapper todoBacklogMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void insertSuccess() {
        Long userId = createUser();
        TodoBacklog backlog = newBacklog(userId);
        int row = todoBacklogMapper.insert(backlog);
        assertEquals(1, row);
        assertNotNull(backlog.getId());
    }

    @Test
    void selectByIdSuccess() {
        Long userId = createUser();
        TodoBacklog backlog = newBacklog(userId);
        todoBacklogMapper.insert(backlog);
        TodoBacklog query = new TodoBacklog();
        query.setId(backlog.getId());
        query.setUserId(userId);
        TodoBacklog result = todoBacklogMapper.selectById(query);
        assertNotNull(result);
        assertEquals(backlog.getId(), result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals("测试标题", result.getTitle());
        assertEquals("测试内容", result.getContent());
    }

    @Test
    void updateSuccess() {
        Long userId = createUser();
        TodoBacklog backlog = newBacklog(userId);
        todoBacklogMapper.insert(backlog);
        backlog.setTitle("测试标题");
        backlog.setContent("测试内容");
        int row = todoBacklogMapper.update(backlog);
        assertEquals(1, row);
    }


    private TodoBacklog newBacklog(Long userId) {
        TodoBacklog backlog = new TodoBacklog();
        backlog.setUserId(userId);
        backlog.setTitle("测试标题");
        backlog.setContent("测试内容");
        return backlog;
    }
}
