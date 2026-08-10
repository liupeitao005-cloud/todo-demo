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

    @Insert("INSERT INTO todo_four (user_id,task_id,title,content,importance,urgency,start_time,finish_time,create_time,update_time) VALUES (#{userId},#{taskId},#{title},#{content},#{importance},#{urgency},#{startTime},#{finishTime},NOW(),NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TodoFour todoFour);

    @Update("UPDATE todo_four SET title=#{title}, content=#{content}, importance=#{importance}, urgency=#{urgency}, start_time=#{startTime}, finish_time=#{finishTime}, update_time=NOW() WHERE user_id=#{userId} AND task_id=#{taskId}")
    int updateQuadrantByTaskId(TodoFour todoFour);

    @Select("SELECT f.id,f.user_id,f.task_id,t.title,t.content,f.importance,f.urgency,t.start_time,t.finish_time,f.create_time,f.update_time FROM todo_four f INNER JOIN todo_task t ON t.id=f.task_id AND t.user_id=f.user_id WHERE f.user_id=#{userId} AND f.importance=#{importance} AND f.urgency=#{urgency} ORDER BY COALESCE(t.start_time, f.create_time) ASC, f.id ASC")
    List<TodoFour> selectByFour (TodoFour todoFour);

    @Delete("DELETE FROM todo_four WHERE id=#{id} AND user_id=#{userId}")
    int delete(TodoFour todoFour);
}
