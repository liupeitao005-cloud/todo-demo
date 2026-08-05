package com.todo.mapper;

import com.todo.entity.TodoUser;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@SpringBootTest(classes = MapperTestBase.MapperTestApplication.class)
@ActiveProfiles("test")
@Transactional
abstract class MapperTestBase {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected TodoUserMapper todoUserMapper;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @MapperScan("com.todo.mapper")
    static class MapperTestApplication {
    }

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
