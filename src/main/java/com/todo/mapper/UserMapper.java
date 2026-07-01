package com.todo.mapper;

import com.todo.entity.TodoUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT id, username, password, create_time, update_time FROM todo_user WHERE username = #{username} LIMIT 1")
    TodoUser findByUsername(String username);

    @Insert("INSERT INTO todo_user(username, password, create_time, update_time) VALUES(#{username}, #{password}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TodoUser user);
}
