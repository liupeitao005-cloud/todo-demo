package com.todo.mapper;

import com.todo.dto.TodoCalendarDTO;
import com.todo.vo.TodoCalendarVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TodoCalendarMapper {
    @Select("SELECT id, title, content, location, start_time, finish_time, 'schedule' AS item_type FROM todo_schedule WHERE user_id=#{userId} AND start_time < #{finishTime} AND finish_time > #{startTime} UNION ALL SELECT id, title, content, NULL AS location, start_time, finish_time, 'task' AS item_type FROM todo_task WHERE user_id=#{userId} AND start_time < #{finishTime} AND finish_time > #{startTime} ORDER BY start_time ASC")
    List<TodoCalendarVO> listByTime(TodoCalendarDTO dto);
}
