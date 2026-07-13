package com.todo.mapper;


import com.todo.entity.TodoReviewplan;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TodoReviewplanMapper {

    @Insert("INSERT INTO todo_reviewplan(user_id, review_task_id, review_time, is_finish, create_time, update_time) VALUES (#{userId}, #{reviewTaskId}, #{reviewTime}, #{isFinish}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TodoReviewplan todoReviewplan);
    @Update("UPDATE todo_reviewplan SET is_finish = 1, finish_time = NOW(), update_time = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int finish(@Param("id") Long id, @Param("userId") Long userId);
    @Select("SELECT id,user_id,review_task_id,review_time,is_finish,finish_time,create_time,update_time FROM todo_reviewplan WHERE user_id=#{userId} ORDER BY review_time ASC")
    List<TodoReviewplan> listByUserId(Long userId);
}
