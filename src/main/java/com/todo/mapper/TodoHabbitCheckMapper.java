package com.todo.mapper;

import com.todo.entity.TodoHabbitCheck;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TodoHabbitCheckMapper {
    @Insert("INSERT INTO todo_habbit_check(user_id, habbit_id, check_date, create_time) VALUES (#{userId}, #{habbitId}, #{checkDate}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TodoHabbitCheck check);

    @Delete("DELETE FROM todo_habbit_check WHERE user_id=#{userId} AND habbit_id=#{habbitId} AND check_date=#{checkDate}")
    int deleteByUserHabitDate(TodoHabbitCheck check);

    @Select("SELECT COUNT(*) FROM todo_habbit_check WHERE user_id=#{userId} AND habbit_id=#{habbitId} AND check_date=#{checkDate}")
    int countByUserHabitDate(TodoHabbitCheck check);

    @Select("SELECT id,user_id,habbit_id,check_date,create_time FROM todo_habbit_check WHERE user_id=#{userId} AND (#{startDate} IS NULL OR check_date>=#{startDate}) AND (#{endDate} IS NULL OR check_date<=#{endDate}) ORDER BY check_date DESC, id DESC ")
    List<TodoHabbitCheck> listByUserAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
