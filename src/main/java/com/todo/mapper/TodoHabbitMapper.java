package com.todo.mapper;


import com.todo.entity.TodoHabbit;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface TodoHabbitMapper {
    @Insert("INSERT INTO todo_habbit(user_id, title, content, day_minutes, min_minutes, max_minutes, create_time, update_time) VALUES (#{userId},#{title},#{content},#{dayMinutes},#{minMinutes},#{maxMinutes},NOW(),NOW())")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insert(TodoHabbit habbit);
}
