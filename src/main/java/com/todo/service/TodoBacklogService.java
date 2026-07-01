package com.todo.service;


import com.todo.dto.TodoBacklogDTO;
import com.todo.entity.TodoBacklog;
import com.todo.mapper.TodoBacklogMapper;
import com.todo.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.todo.util.Result;

@Service
@RequiredArgsConstructor
public class TodoBacklogService {
    private final TodoBacklogMapper todoBacklogMapper;

    public Result<String> createtodoBacklog(TodoBacklogDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto.getContent() == null || dto.getContent().equals("")) {
            return Result.fail("待办内容不能为空");
        }
        TodoBacklog backlog = new TodoBacklog();
        backlog.setUserId(userId);
        backlog.setContent(dto.getContent());
        backlog.setTitle(dto.getTitle());
        todoBacklogMapper.insert(backlog);
        return Result.success("创建成功");
    }

    // 修改待办
    public Result<String> updateBacklog(TodoBacklogDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto.getId() == null) return Result.fail("缺少待办id");
        int row = todoBacklogMapper.update(dto.getId(), userId, dto.getContent(), dto.getTitle());
        if (row <= 0) return Result.fail("待办不存在或无权限修改");
        return Result.success("修改成功");
    }
}