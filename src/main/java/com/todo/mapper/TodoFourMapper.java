package com.todo.mapper;


import com.todo.entity.TodoFour;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TodoFourMapper {

    @Insert("INSERT INTO todo_four(user_id,title,content,importance,urgency,start_time,finish_time,create_time,update_time) VALUES (#{userId},#{title},#{content},#{importance},#{urgency},#{startTime},#{finishTime},NOW(),NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TodoFour todoFour);

    @Update("UPDATE todo_four SET importance=#{importance}, urgency=#{urgency}, update_time=NOW() WHERE user_id=#{userId} AND title <=> #{title} AND content <=> #{content} AND start_time <=> #{startTime} AND finish_time <=> #{finishTime}")
    int updateQuadrantBySnapshot(TodoFour todoFour);

    @Select("SELECT id,user_id,title,content,importance,urgency,start_time,finish_time,create_time,update_time FROM todo_four WHERE user_id=#{userId} AND importance=#{importance} AND urgency=#{urgency}")
    List<TodoFour> selectByFour (TodoFour todoFour);

    @Delete("DELETE FROM todo_four WHERE id=#{id} AND user_id=#{userId}")
    int delete(TodoFour todoFour);
}
