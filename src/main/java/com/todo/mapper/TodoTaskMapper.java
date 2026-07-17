package com.todo.mapper;

import com.todo.entity.TodoTask;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TodoTaskMapper {

        @Insert("INSERT INTO todo_task(user_id, title, content, task_type,parent_id, create_time, update_time,start_time,finish_time, is_finish,is_next) VALUES (#{userId}, #{title}, #{content}, #{taskType}, #{parentId},NOW(), NOW(), #{startTime},#{finishTime},0,0)")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert (TodoTask task);

        @Update("UPDATE todo_task SET title=#{title}, content=#{content}, task_type=#{taskType}, update_time=NOW() WHERE id=#{id} AND user_id=#{userId}")
        int update (TodoTask task);

        @Delete("DELETE FROM todo_task WHERE id=#{id} AND user_id=#{userId}")
        int delete (TodoTask task);

        @Update("UPDATE todo_task SET is_finish=1, is_next=0, finish_time=NOW(), update_time=NOW() WHERE id=#{id} AND user_id=#{userId}")
        int finish (TodoTask task);

        @Update("UPDATE todo_task SET is_next=1, update_time=NOW() WHERE id=#{id} AND user_id=#{userId} AND is_finish=0")
        int next (TodoTask task);

        @Select("SELECT id,user_id,title,content,task_type,start_time,finish_time,is_finish,is_next,create_time,update_time,parent_id FROM todo_task WHERE id = #{id} AND user_id=#{userId}")
        TodoTask selectByID(TodoTask Task);

        @Select("SELECT id,user_id,title,content,task_type,start_time,finish_time,is_finish,is_next,create_time,update_time,parent_id FROM todo_task WHERE user_id=#{userId} ORDER BY COALESCE(start_time, create_time) ASC, id ASC")
        List<TodoTask> listByUserId(Long userId);

        @Update("UPDATE todo_task SET start_time=#{startTime}, finish_time=#{finishTime}, update_time=NOW() WHERE id=#{id} AND user_id=#{userId} AND is_finish=0")
        int yanqi(TodoTask task);
    }
