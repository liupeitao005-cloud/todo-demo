package com.todo.mapper;


import com.todo.entity.TodoBacklog;
import org.apache.ibatis.annotations.*;


@Mapper
public interface TodoBacklogMapper {
    @Insert("INSERT INTO todo_backlog(user_id,title,content,create_time,update_time)  VALUES ( #{userId},#{title},#{content},NOW(),NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TodoBacklog backlog);
    @Update("UPDATE todo_backlog SET title=#{title},content=#{content},update_time=NOW() WHERE id=#{id} AND user_id=#{userId}")
    int update(TodoBacklog backlog);
    @Select("SELECT id,user_id,title,content,create_time,update_time FROM todo_backlog WHERE id=#{id} AND user_id=#{userId}")
    TodoBacklog selectById(TodoBacklog backlog);
}
