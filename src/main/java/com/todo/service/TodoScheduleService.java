package com.todo.service;

import com.todo.dto.TodoReminderDTO;
import com.todo.dto.TodoScheduleDTO;
import com.todo.entity.TodoReminder;
import com.todo.entity.TodoSchedule;
import com.todo.mapper.TodoReminderMapper;
import com.todo.mapper.TodoScheduleMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoScheduleService {
    private final TodoScheduleMapper todoScheduleMapper;
    private final TodoReminderService todoReminderService;
    private final TodoReminderMapper todoReminderMapper;

    @Transactional
    public Result<String> createTodoSchedule(TodoScheduleDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (!dto.getStartTime().isBefore(dto.getFinishTime())) {
            return Result.fail("开始时间必须早于结束时间");
        }
        TodoSchedule schedule = new TodoSchedule();
        schedule.setUserId(userId);
        schedule.setTitle(dto.getTitle());
        schedule.setContent(dto.getContent());
        schedule.setLocation(dto.getLocation());
        schedule.setStartTime(dto.getStartTime());
        schedule.setFinishTime(dto.getFinishTime());
        todoScheduleMapper.insert(schedule);
        createScheduleReminder(schedule);
        return Result.success("创建成功");
    }

    @Transactional
    public Result<String> updateTodoSchedule(TodoScheduleDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto.getStartTime() != null && dto.getFinishTime() != null && !dto.getStartTime().isBefore(dto.getFinishTime())) {
            return Result.fail("开始时间必须早于结束时间");
        }
        TodoSchedule schedule = new TodoSchedule();
        schedule.setId(dto.getId());
        schedule.setUserId(userId);
        schedule.setTitle(dto.getTitle());
        schedule.setContent(dto.getContent());
        schedule.setLocation(dto.getLocation());
        schedule.setStartTime(dto.getStartTime());
        schedule.setFinishTime(dto.getFinishTime());
        int row = todoScheduleMapper.update(schedule);
        if (row <= 0) return Result.fail("行程不存在或无权限修改");
        if (schedule.getStartTime() != null) {
            TodoReminder reminder = new TodoReminder();
            reminder.setUserId(userId);
            reminder.setTargetType("schedule");
            reminder.setTargetId(schedule.getId());
            reminder.setTitle("行程提醒：" + schedule.getTitle());
            reminder.setContent("你的行程还有 5 分钟开始：" + schedule.getTitle());
            reminder.setRemindTime(schedule.getStartTime().minusMinutes(5));
            reminder.setChannel("desktop");
            todoReminderMapper.updateByTarget(reminder);
        }
        return Result.success("修改成功");
    }
    @Transactional
    public Result<String> deleteTodoSchedule(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        TodoSchedule schedule = new TodoSchedule();
        schedule.setId(id);
        schedule.setUserId(userId);
        int row = todoScheduleMapper.delete(schedule);
        if (row <= 0) return Result.fail("行程不存在或无权限删除");
        todoReminderService.cancelByTarget(userId, "schedule", id);
        return Result.success("删除成功");
    }

    public Result<List<TodoSchedule>> listTodoSchedule() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        return Result.success("查询成功", todoScheduleMapper.listByUserId(userId));
    }
    private void createScheduleReminder(TodoSchedule schedule) {
        if (schedule == null || schedule.getId() == null || schedule.getStartTime() == null) {
            return;
        }
        TodoReminderDTO reminderDTO = new TodoReminderDTO();
        reminderDTO.setTargetType("schedule");
        reminderDTO.setTargetId(schedule.getId());
        reminderDTO.setTitle("行程提醒：" + schedule.getTitle());
        reminderDTO.setContent("你的行程还有 5 分钟开始：" + schedule.getTitle());
        reminderDTO.setRemindTime(schedule.getStartTime().minusMinutes(5));
        reminderDTO.setChannel("desktop");
        todoReminderService.createReminder(reminderDTO);
    }
}
