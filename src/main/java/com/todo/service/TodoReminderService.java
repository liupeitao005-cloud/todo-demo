package com.todo.service;

import com.todo.dto.TodoReminderDTO;
import com.todo.entity.TodoReminder;
import com.todo.mapper.TodoReminderMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import com.todo.vo.TodoReminderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoReminderService {
    private final TodoReminderMapper todoReminderMapper;

    public Result<String> createReminder(TodoReminderDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        String channel = StringUtils.hasText(dto.getChannel()) ? dto.getChannel() : "desktop";

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
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        TodoReminder reminder = new TodoReminder();
        reminder.setId(dto.getId());
        reminder.setUserId(userId);
        int row = todoReminderMapper.update(reminder);
        if (row <= 0) return Result.fail("提醒不存在或无权限");
        return Result.success("已提醒");
    }

    public int processDueServerReminders() {
        List<TodoReminder> reminders = todoReminderMapper.selectDueAll();
        int processedCount = 0;
        for (TodoReminder reminder : reminders) {
            if (isClientDeliveredChannel(reminder.getChannel())) {
                continue;
            }
            if (sendServerReminder(reminder)) {
                int row = todoReminderMapper.update(reminder);
                if (row > 0) {
                    processedCount++;
                }
            }
        }
        return processedCount;
    }

    private boolean isClientDeliveredChannel(String channel) {
        return "desktop".equals(channel) || "app".equals(channel);
    }

    private boolean sendServerReminder(TodoReminder reminder) {
        if ("telegramBot".equals(reminder.getChannel())) {
            log.info("Telegram reminder due. userId={}, reminderId={}, title={}",
                    reminder.getUserId(), reminder.getId(), reminder.getTitle());
            return true;
        }
        log.warn("Unsupported reminder channel. reminderId={}, channel={}",
                reminder.getId(), reminder.getChannel());
        return false;
    }
}
