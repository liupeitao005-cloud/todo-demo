package com.todo.mapper;

import com.todo.entity.TodoUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TodoUserMapper {
    @Select("SELECT id, username, password, create_time, update_time FROM todo_user WHERE username = #{username} LIMIT 1")
    TodoUser selectByUsername(String username);

    @Insert("INSERT INTO todo_user(username, password, create_time, update_time) VALUES (#{username}, #{password}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TodoUser user);
}
