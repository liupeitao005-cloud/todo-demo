package com.todo.service;

import com.todo.dto.TodoReminderDTO;
import com.todo.dto.TodoTaskDTO;
import com.todo.entity.TodoTask;
import com.todo.mapper.TodoTaskMapper;
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
public class TodoTaskService{

    private final TodoTaskMapper todoTaskMapper;
    private final TodoReminderService todoReminderService;

    @Transactional
    public Result<String> createTodoTask(TodoTaskDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (!dto.getStartTime().isBefore(dto.getFinishTime())) {
            return Result.fail("开始时间必须早于结束时间");
        }
        TodoTask task = new TodoTask();
        task.setUserId(userId);
        task.setTitle(dto.getTitle());
        task.setContent(dto.getContent());
        task.setTaskType(dto.getTaskType());
        task.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        task.setStartTime(dto.getStartTime());
        task.setFinishTime(dto.getFinishTime());
        todoTaskMapper.insert(task);
        createTaskReminder(task);
        return Result.success("创建成功");
    }
    public Result<String> updateTodoTask(TodoTaskDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto.getStartTime() != null && dto.getFinishTime() != null && !dto.getStartTime().isBefore(dto.getFinishTime())) {
            return Result.fail("开始时间必须早于结束时间");
        }
        TodoTask task = new TodoTask();
        task.setId(dto.getId());
        task.setUserId(userId);
        task.setTitle(dto.getTitle());
        task.setContent(dto.getContent());
        task.setTaskType(dto.getTaskType());
        int row = todoTaskMapper.update(task);
        if (row <= 0) return Result.fail("任务不存在或无权限修改");
        return Result.success("修改成功");
    }
    public Result<List<TodoTask>> listTodoTask() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        return Result.success("查询成功", todoTaskMapper.listByUserId(userId));
    }
    @Transactional
    public Result<String> deleteTodoTask(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        TodoTask task = new TodoTask();
        task.setId(id);
        task.setUserId(userId);
        int row = todoTaskMapper.delete(task);
        if (row <= 0) return Result.fail("任务不存在或无权限删除");
        todoReminderService.cancelByTarget(userId, "task", id);
        return Result.success("删除成功");
    }
    @Transactional
    public Result<String> finishTodoTask(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        TodoTask task = new TodoTask();
        task.setId(id);
        task.setUserId(userId);
        int row = todoTaskMapper.finish(task);
        if (row <= 0) return Result.fail("任务不存在或无权限完成");
        todoReminderService.cancelByTarget(userId, "task", id);
        return Result.success("完成成功");
    }
    @Transactional
    public Result<String> qiegehour(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        TodoTask task = new TodoTask();
        task.setId(id);
        task.setUserId(userId);
        TodoTask parent = todoTaskMapper.selectByID(task);
        if (parent == null) return Result.fail("任务不存在");
        if (parent.getStartTime() == null || parent.getFinishTime() == null) {
            return Result.fail("任务开始时间和结束时间不能为空");
        }
        Duration duration = Duration.between(parent.getStartTime(), parent.getFinishTime());
        if (duration.toHours() < 4) {
            return Result.fail("时间小于4小时不需要拆分");
        }
        LocalDateTime start = parent.getStartTime();
        LocalDateTime finish= parent.getFinishTime();
        int index = 1;
        while (start.isBefore(finish)) {
            LocalDateTime now = start.plusHours(1);
            if(now.isAfter(finish)) {
                now = finish;
            }
            TodoTask child = new TodoTask();
            child.setUserId(userId);
            child.setTitle(parent.getTitle());
            child.setContent(parent.getContent());
            child.setTaskType(parent.getTaskType());
            child.setParentId(parent.getId());
            child.setStartTime(start);
            child.setFinishTime(now);
            todoTaskMapper.insert(child);
            start = now;
            index++;
        }
           return Result.success("拆分成功");
    }
    public Result<String>yanqi(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        TodoTask query = new TodoTask();
        query.setId(id);
        query.setUserId(userId);
        TodoTask task = todoTaskMapper.selectByID(query);
        if (task == null) return Result.fail("任务不存在或无权限操作");
        if (task.getIsFinish() == 1) {
            return Result.fail("任务已完成，不需要延期");
        }
        if (task.getStartTime() == null || task.getFinishTime() == null) {
            return Result.fail("任务开始时间和结束时间不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        if (!task.getFinishTime().isBefore(now)) {
            return Result.fail("任务还没超时，不需要延期");
        }
        Duration duration = Duration.between(task.getStartTime(), task.getFinishTime());
        task.setStartTime(LocalDateTime.now());
        task.setFinishTime(LocalDateTime.now().plusMinutes(duration.toMinutes()));
        int row = todoTaskMapper.yanqi(task);
        if (row <= 0) return Result.fail("延期失败");
        return Result.success("延期成功");
    }
     public Result<String>nextTodoTask(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        TodoTask task = new TodoTask();
        task.setId(id);
        task.setUserId(userId);
         int row = todoTaskMapper.next(task);
         if (row <= 0) return Result.fail("任务不存在、已完成或无权限操作");
         return Result.success("设置成功");
     }
    private void createTaskReminder(TodoTask task) {
        if (task == null || task.getId() == null || task.getStartTime() == null) {
            return;
        }
        TodoReminderDTO reminderDTO = new TodoReminderDTO();
        reminderDTO.setTargetType("task");
        reminderDTO.setTargetId(task.getId());
        reminderDTO.setTitle("任务提醒：" + task.getTitle());
        reminderDTO.setContent("你的任务还有 5 分钟开始：" + task.getTitle());
        reminderDTO.setRemindTime(task.getStartTime().minusMinutes(5));
        reminderDTO.setChannel("desktop");
        todoReminderService.createReminder(reminderDTO);
    }
}
