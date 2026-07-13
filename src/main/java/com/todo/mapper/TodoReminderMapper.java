package com.todo.mapper;

import com.todo.entity.TodoReminder;
import com.todo.vo.TodoReminderVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TodoReminderMapper {
    @Insert("INSERT INTO todo_reminder(user_id, target_type, target_id, title, content, remind_time, channel, is_sent, create_time, update_time) VALUES(#{userId}, #{targetType}, #{targetId}, #{title}, #{content}, #{remindTime}, #{channel}, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TodoReminder reminder);

    @Select("SELECT id, target_type, target_id, title, content, remind_time, channel FROM todo_reminder WHERE user_id=#{userId} AND channel=#{channel} AND is_sent=0 AND remind_time<=NOW() ORDER BY remind_time ASC")
    List<TodoReminderVO> selectPending(TodoReminder reminder);

    @Select("SELECT id,user_id,target_type,target_id,title,content,remind_time,channel,is_sent,create_time,update_time FROM todo_reminder WHERE is_sent=0 AND remind_time<=NOW() ORDER BY remind_time ASC")
    List<TodoReminder> selectDueAll();

    @Update("UPDATE todo_reminder SET is_sent=1, update_time=NOW() WHERE id=#{id} AND user_id=#{userId}")
    int update(TodoReminder reminder);
}
