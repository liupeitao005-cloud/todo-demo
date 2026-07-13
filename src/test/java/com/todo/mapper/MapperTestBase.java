package com.todo.mapper;

import com.todo.entity.TodoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
@Transactional
abstract class MapperTestBase {

    @Autowired
    protected TodoUserMapper todoUserMapper;

    protected Long createUser() {
        TodoUser user = newUser();
        todoUserMapper.insert(user);
        return user.getId();
    }

    protected TodoUser newUser() {
        TodoUser user = new TodoUser();
        user.setUsername("mapper_" + UUID.randomUUID());
        user.setPassword("123456");
        return user;
    }
}
