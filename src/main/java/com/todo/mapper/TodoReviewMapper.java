package com.todo.mapper;

import com.todo.entity.TodoReview;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface TodoReviewMapper {
    @Insert("INSERT INTO todo_review_task(user_id, title, content, create_time, update_time) VALUES (#{userId},#{title},#{content},NOW(),NOW())")
    @Options(useGeneratedKeys = true ,keyProperty = "id")
    int insert(TodoReview todoReview);
}
