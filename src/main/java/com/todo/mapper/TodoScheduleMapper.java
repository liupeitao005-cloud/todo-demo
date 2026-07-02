package com.todo.mapper;

import com.todo.entity.TodoSchedule;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TodoScheduleMapper {
    @Insert("INSERT INTO todo_schedule(user_id, title, content, location, start_time, finish_time, create_time, update_time) VALUES (#{userId}, #{title}, #{content}, #{location}, #{startTime}, #{finishTime}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TodoSchedule todoSchedule);

    @Update("UPDATE todo_schedule SET title=#{title}, content=#{content}, location=#{location}, start_time=#{startTime}, finish_time=#{finishTime}, update_time=NOW() WHERE id=#{id} AND user_id=#{userId}")
    int update(TodoSchedule todoSchedule);

    @Delete("DELETE FROM todo_schedule WHERE id=#{id} AND user_id=#{userId}")
    int delete(TodoSchedule todoSchedule);
}
