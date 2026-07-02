package com.todo.mapper;

import com.todo.entity.TodoCalendarItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TodoCalendarMapper {
    @Select("SELECT id, title, content, location, start_time, finish_time, 'schedule' AS item_type FROM todo_schedule WHERE user_id=#{userId} AND start_time < #{finishTime} AND finish_time > #{startTime} UNION ALL SELECT id, title, content, NULL AS location, create_time AS start_time, COALESCE(finish_time, create_time) AS finish_time, 'task' AS item_type FROM todo_task WHERE user_id=#{userId} AND create_time >= #{startTime} AND create_time < #{finishTime} ORDER BY start_time ASC")
    List<TodoCalendarItem> listByTime(@Param("userId") Long userId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("finishTime") LocalDateTime finishTime);
}
