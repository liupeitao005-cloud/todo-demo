package com.todo.mapper;


import com.todo.entity.TodoFour;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TodoFourMapper {

    @Insert("INSERT INTO todo_four(user_id,title,content,importance,urgency,start_time,finish_time,create_time,update_time) VALUES (#{userId},#{title},#{content},#{importance},#{urgency},#{startTime},#{finishTime},NOW(),NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TodoFour todoFour);
    @Select("SELECT id,user_id,title,content,importance,urgency,start_time,finish_time,create_time,update_time FROM todo_four WHERE user_id=#{userId} AND importance=#{importance} AND urgency=#{urgency}")
    List<TodoFour> selectByFour (TodoFour todoFour);
}
