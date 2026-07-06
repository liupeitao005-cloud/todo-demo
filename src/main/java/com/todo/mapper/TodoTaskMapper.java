package com.todo.mapper;

import com.todo.entity.TodoTask;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TodoTaskMapper {

        @Insert("INSERT INTO todo_task(user_id, title, content, task_type,parent_id, create_time, update_time,start_time,finish_time, is_finish,is_next) VALUES (#{userId}, #{title}, #{content}, #{taskType}, #{parentId},NOW(), NOW(), #{startTime},#{finishTime},0,0)")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert (TodoTask task);

        @Update("UPDATE todo_task SET title=#{title}, content=#{content}, task_type=#{taskType}, update_time=NOW() WHERE id=#{id} AND user_id=#{userId}")
        int update (TodoTask task);

        @Delete("DELETE FROM todo_task WHERE id=#{id} AND user_id=#{userId}")
        int delete (TodoTask task);

        @Update("UPDATE todo_task SET is_finish=1, finish_time=NOW(), update_time=NOW() WHERE id=#{id} AND user_id=#{userId}")
        int finish (TodoTask task);

        @Update("UPDATE todo_task SET is_next=1, finish_time=NOW(), update_time=NOW() WHERE id=#{id} AND user_id=#{userId}")
        int next (TodoTask task);

        @Select("SELECT id,user_id,title,content,task_type,start_time,finish_time,is_finish,create_time,update_time,parent_id FROM todo_task WHERE id = #{id} AND user_id=#{userId}")
        TodoTask selectByID(TodoTask Task);

        @Update("UPDATE todo_task SET start_time=#{startTime}, finish_time=#{finishTime}, update_time=NOW() WHERE id=#{id} AND user_id=#{userId} AND is_finish=0")
        int yanqi(TodoTask task);
    }
