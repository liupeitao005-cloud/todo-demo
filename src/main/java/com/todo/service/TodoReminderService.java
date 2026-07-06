package com.todo.service;

import com.todo.dto.TodoReminderDTO;
import com.todo.entity.TodoReminder;
import com.todo.mapper.TodoReminderMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import com.todo.vo.TodoReminderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoReminderService {
    private final TodoReminderMapper todoReminderMapper;

    public Result<String> createReminder(TodoReminderDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto == null || dto.getRemindTime() == null) return Result.fail("提醒时间不能为空");
        if (dto.getTargetId() == null || !StringUtils.hasText(dto.getTargetType())) {
            return Result.fail("提醒对象不能为空");
        }
        String channel = StringUtils.hasText(dto.getChannel()) ? dto.getChannel() : "desktop";
        if (channel == null || channel.equals("")) {
            return Result.fail("提醒渠道不能为空");
        }

        TodoReminder reminder = new TodoReminder();
        reminder.setUserId(userId);
        reminder.setTargetType(dto.getTargetType());
        reminder.setTargetId(dto.getTargetId());
        reminder.setTitle(dto.getTitle());
        reminder.setContent(dto.getContent());
        reminder.setRemindTime(dto.getRemindTime());
        reminder.setChannel(channel);
        todoReminderMapper.insert(reminder);
        return Result.success("创建提醒成功");
    }

    public Result<List<TodoReminderVO>> pendingDesktopReminder() {
        return pendingReminderByChannel("desktop");
    }

    public Result<List<TodoReminderVO>> pendingAppReminder() {
        return pendingReminderByChannel("app");
    }
    public Result<List<TodoReminderVO>> pendingTelegramBotReminder() {
        return pendingReminderByChannel("telegramBot");
    }

    private Result<List<TodoReminderVO>> pendingReminderByChannel(String channel) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        TodoReminder reminder = new TodoReminder();
        reminder.setUserId(userId);
        reminder.setChannel(channel);
        return Result.success("查询成功", todoReminderMapper.selectPending(reminder));
    }

    public Result<String> readReminder(TodoReminderDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto == null || dto.getId() == null) return Result.fail("缺少提醒id");
        TodoReminder reminder = new TodoReminder();
        reminder.setId(dto.getId());
        reminder.setUserId(userId);
        int row = todoReminderMapper.update(reminder);
        if (row <= 0) return Result.fail("提醒不存在或无权限");
        return Result.success("已提醒");
    }
}
