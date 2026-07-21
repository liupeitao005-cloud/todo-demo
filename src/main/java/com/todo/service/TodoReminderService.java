package com.todo.service;

import com.todo.dto.TodoReminderDTO;
import com.todo.entity.TodoReminder;
import com.todo.entity.TodoReviewplan;
import com.todo.entity.TodoSchedule;
import com.todo.entity.TodoTask;
import com.todo.mapper.TodoReminderMapper;
import com.todo.mapper.TodoReviewplanMapper;
import com.todo.mapper.TodoScheduleMapper;
import com.todo.mapper.TodoTaskMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import com.todo.vo.TodoReminderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoReminderService {
    private final TodoReminderMapper todoReminderMapper;
    private final TodoTaskMapper todoTaskMapper;
    private final TodoScheduleMapper todoScheduleMapper;
    private final TodoReviewplanMapper todoReviewplanMapper;

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
        if (todoReminderMapper.countByTarget(reminder) > 0) {
            return Result.success("创建提醒成功");
        }
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

    public Result<List<TodoReminder>> listReminder() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        ensureUserAutoReminders(userId);
        return Result.success("查询成功", todoReminderMapper.selectByUserId(userId));
    }

    private Result<List<TodoReminderVO>> pendingReminderByChannel(String channel) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        ensureUserAutoReminders(userId);
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
        TodoReminder savedReminder = todoReminderMapper.selectById(reminder);
        int row = todoReminderMapper.update(reminder);
        if (row > 0 && savedReminder != null) {
            cancelByTarget(userId, savedReminder.getTargetType(), savedReminder.getTargetId());
        }
        if (row <= 0) return Result.fail("提醒不存在或无权限");
        return Result.success("已提醒");
    }

    public void cancelByTarget(Long userId, String targetType, Long targetId) {
        if (userId == null || !StringUtils.hasText(targetType) || targetId == null) {
            return;
        }
        TodoReminder reminder = new TodoReminder();
        reminder.setUserId(userId);
        reminder.setTargetType(targetType);
        reminder.setTargetId(targetId);
        todoReminderMapper.cancelByTarget(reminder);
    }

    private void ensureUserAutoReminders(Long userId) {
        LocalDateTime earliestUsefulRemindTime = LocalDateTime.now().minusDays(1);
        for (TodoTask task : safeList(todoTaskMapper.listByUserId(userId))) {
            if (task.getIsFinish() != null && task.getIsFinish() == 1) {
                continue;
            }
            createMissingAutoReminder(
                    userId,
                    "task",
                    task.getId(),
                    "任务提醒：" + task.getTitle(),
                    "你的任务还有 5 分钟开始：" + task.getTitle(),
                    task.getStartTime(),
                    earliestUsefulRemindTime
            );
        }
        for (TodoSchedule schedule : safeList(todoScheduleMapper.listByUserId(userId))) {
            if (schedule.getFinishTime() != null && schedule.getFinishTime().isBefore(LocalDateTime.now())) {
                continue;
            }
            createMissingAutoReminder(
                    userId,
                    "schedule",
                    schedule.getId(),
                    "行程提醒：" + schedule.getTitle(),
                    "你的行程还有 5 分钟开始：" + schedule.getTitle(),
                    schedule.getStartTime(),
                    earliestUsefulRemindTime
            );
        }
        for (TodoReviewplan plan : safeList(todoReviewplanMapper.listByUserId(userId))) {
            if (plan.getIsFinish() != null && plan.getIsFinish() == 1) {
                continue;
            }
            createMissingAutoReminder(
                    userId,
                    "review",
                    plan.getId(),
                    "复习提醒",
                    "你的复习计划还有 5 分钟开始",
                    plan.getReviewTime(),
                    earliestUsefulRemindTime
            );
        }
    }

    private <T> List<T> safeList(List<T> items) {
        return items == null ? Collections.emptyList() : items;
    }

    private void createMissingAutoReminder(Long userId,
                                           String targetType,
                                           Long targetId,
                                           String title,
                                           String content,
                                           LocalDateTime startTime,
                                           LocalDateTime earliestUsefulRemindTime) {
        if (targetId == null || startTime == null) {
            return;
        }
        LocalDateTime remindTime = startTime.minusMinutes(5);
        if (remindTime.isBefore(earliestUsefulRemindTime)) {
            return;
        }
        TodoReminder reminder = new TodoReminder();
        reminder.setUserId(userId);
        reminder.setTargetType(targetType);
        reminder.setTargetId(targetId);
        reminder.setTitle(title);
        reminder.setContent(content);
        reminder.setRemindTime(remindTime);
        reminder.setChannel("desktop");
        if (todoReminderMapper.countByTarget(reminder) == 0) {
            todoReminderMapper.insert(reminder);
        }
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
