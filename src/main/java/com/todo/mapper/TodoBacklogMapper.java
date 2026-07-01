package com.todo.mapper;


import com.todo.entity.TodoBacklog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TodoBacklogMapper {
    @Insert("INSERT INTO todo_backlog(user_id,title,content,create_time,update_time) " +
            "VALUES(#{userId},#{title},#{content},NOW(),NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TodoBacklog backlog);
    @Update("UPDATE todo_backlog SET title=#{t},content=#{c},update_time=NOW() WHERE id=#{id} AND user_id=#{uid}")
    int update(@Param("id") Long id, @Param("uid") Long uid, @Param("t") String title, @Param("c") String content);
}
