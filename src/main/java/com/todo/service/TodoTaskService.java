package com.todo.service;

import com.todo.dto.TodoTaskDTO;
import com.todo.entity.TodoTask;
import com.todo.mapper.TodoTaskMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TodoTaskService {
    private final TodoTaskMapper todoTaskMapper;

    public Result<String> createTodoTask(TodoTaskDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto == null || !StringUtils.hasText(dto.getContent())) {
            return Result.fail("任务内容不能为空");
        }

        TodoTask task = new TodoTask();
        task.setUserId(userId);
        task.setTitle(dto.getTitle());
        task.setContent(dto.getContent());
        task.setTaskType(dto.getTaskType());

        todoTaskMapper.insert(task);
        return Result.success("创建成功");
    }

    public Result<String> updateTodoTask(TodoTaskDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto == null || dto.getId() == null) return Result.fail("缺少任务id");

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

    public Result<String> deleteTodoTask(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (id == null) return Result.fail("缺少任务id");

        TodoTask task = new TodoTask();
        task.setId(id);
        task.setUserId(userId);

        int row = todoTaskMapper.delete(task);
        if (row <= 0) return Result.fail("任务不存在或无权限删除");
        return Result.success("删除成功");
    }

    public Result<String> finishTodoTask(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (id == null) return Result.fail("缺少任务id");

        TodoTask task = new TodoTask();
        task.setId(id);
        task.setUserId(userId);

        int row = todoTaskMapper.finish(task);
        if (row <= 0) return Result.fail("任务不存在或无权限完成");
        return Result.success("完成成功");
    }
}
