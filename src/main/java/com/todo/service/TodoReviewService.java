package com.todo.service;

import com.todo.dto.TodoReminderDTO;
import com.todo.dto.TodoReviewDTO;
import com.todo.entity.TodoReview;
import com.todo.entity.TodoReviewplan;
import com.todo.mapper.TodoReviewMapper;
import com.todo.mapper.TodoReviewplanMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoReviewService {
    private final TodoReviewMapper todoReviewMapper;
    private final TodoReviewplanMapper todoReviewplanMapper;
    private final TodoReminderService todoReminderService;

    @Transactional
    public Result<String> createReview(TodoReviewDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");

        TodoReview review = new TodoReview();
        review.setUserId(userId);
        review.setTitle(dto.getTitle());
        review.setContent(dto.getContent() == null || dto.getContent().isBlank() ? dto.getTitle() : dto.getContent());
        todoReviewMapper.insert(review);

        Result<Integer> planResult = createReviewPlans(userId, review.getId());
        if (planResult.getCode() != 200) {
            return Result.fail(planResult.getMessage());
        }
        return Result.success("创建复习任务成功，已生成复习计划");
    }

    @Transactional
    public Result<Integer> createReviewPlans(Long userId, Long reviewTaskId) {
        if (userId == null) return Result.fail("未登录");
        if (reviewTaskId == null) return Result.fail("复习任务id不能为空");

        LocalDateTime now = LocalDateTime.now();
        List<Duration> durations = List.of(
                Duration.ofMinutes(20),
                Duration.ofDays(1),
                Duration.ofDays(2),
                Duration.ofDays(4),
                Duration.ofDays(7),
                Duration.ofDays(15)
        );

        int count = 0;
        for (Duration duration : durations) {
            LocalDateTime reviewTime = now.plus(duration);
            TodoReviewplan plan = new TodoReviewplan();
            plan.setUserId(userId);
            plan.setReviewTaskId(reviewTaskId);
            plan.setReviewTime(reviewTime);
            plan.setIsFinish(0);
            todoReviewplanMapper.insert(plan);
            createReviewReminder(plan);
            count++;
        }
        return Result.success("复习计划生成成功", count);
    }
    @Transactional
    public Result<String> finishReviewPlan(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        int row = todoReviewplanMapper.finish(id, userId);
        if (row <= 0) {
            return Result.fail("复习计划不存在或无权限");
        }
        todoReminderService.cancelByTarget(userId, "review", id);
        return Result.success("复习完成记录成功");
    }

    public Result<List<TodoReviewplan>> listReviewPlan() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        return Result.success("查询成功", todoReviewplanMapper.listByUserId(userId));
    }
    private void createReviewReminder(TodoReviewplan plan) {
        if (plan == null || plan.getId() == null || plan.getReviewTime() == null) {
            return;
        }
        TodoReminderDTO reminderDTO = new TodoReminderDTO();
        reminderDTO.setTargetType("review");
        reminderDTO.setTargetId(plan.getId());
        reminderDTO.setTitle("复习提醒");
        reminderDTO.setContent("你的复习计划还有 5 分钟开始");
        reminderDTO.setRemindTime(plan.getReviewTime().minusMinutes(5));
        reminderDTO.setChannel("desktop");
        todoReminderService.createReminder(reminderDTO);
    }
}
