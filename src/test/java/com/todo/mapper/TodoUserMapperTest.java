package com.todo.mapper;

import com.todo.entity.TodoUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TodoUserMapperTest extends MapperTestBase {

    @Test
    void insertAndSelectByUsername() {
        TodoUser user = newUser();
        int row = todoUserMapper.insert(user);
        assertEquals(1, row);
        assertNotNull(user.getId());
        TodoUser dbUser = todoUserMapper.selectByUsername(user.getUsername());
        assertNotNull(dbUser);
        assertEquals(user.getId(), dbUser.getId());
        assertEquals(user.getUsername(), dbUser.getUsername());
    }
}
