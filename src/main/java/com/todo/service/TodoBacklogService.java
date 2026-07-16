package com.todo.service;


import com.todo.dto.TodoBacklogDTO;
import com.todo.entity.TodoBacklog;
import com.todo.entity.TodoTask;
import com.todo.mapper.TodoBacklogMapper;
import com.todo.mapper.TodoTaskMapper;
import com.todo.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.todo.util.Result;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoBacklogService {
    private final TodoBacklogMapper todoBacklogMapper;
    private final TodoTaskMapper todoTaskMapper;
    public Result<String> createtodoBacklog(TodoBacklogDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        TodoBacklog backlog = new TodoBacklog();
        backlog.setUserId(userId);
        backlog.setContent(dto.getContent());
        backlog.setTitle(dto.getTitle());
        todoBacklogMapper.insert(backlog);
        return Result.success("创建成功");
    }
    public Result<String> updateBacklog(TodoBacklogDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        TodoBacklog backlog = new TodoBacklog();
        backlog.setId(dto.getId());
        backlog.setUserId(userId);
        backlog.setContent(dto.getContent());
        backlog.setTitle(dto.getTitle());
        int row = todoBacklogMapper.update(backlog);
        if (row <= 0) return Result.fail("待办不存在或无权限修改");
        return Result.success("修改成功");
    }
    @Transactional
    public Result<String>moveTodoBacklog(TodoBacklogDTO dto){
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (!dto.getStartTime().isBefore(dto.getFinishTime())) {
            return Result.fail("开始时间必须早于结束时间");
        }
        TodoBacklog query = new TodoBacklog();
        query.setId(dto.getId());
        query.setUserId(userId);
        TodoBacklog backlog = todoBacklogMapper.selectById(query);
        if (backlog == null) return Result.fail("待办不存在或无权限操作");
        TodoTask task = new TodoTask();
        task.setUserId(userId);
        task.setTitle(backlog.getTitle());
        task.setContent(backlog.getContent());
        task.setParentId(0L);
        task.setStartTime(dto.getStartTime());
        task.setFinishTime(dto.getFinishTime());
        todoTaskMapper.insert(task);
        int row = todoBacklogMapper.delete(query);
        if (row <= 0) return Result.fail("待办删除失败");
        return Result.success("拖拽到日程成功");
    }

    public Result<List<TodoBacklog>> listBacklog() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        return Result.success("查询成功", todoBacklogMapper.listByUserId(userId));
    }

    public Result<String> deleteBacklog(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (id == null) return Result.fail("待办ID不能为空");
        TodoBacklog backlog = new TodoBacklog();
        backlog.setId(id);
        backlog.setUserId(userId);
        int row = todoBacklogMapper.delete(backlog);
        if (row <= 0) return Result.fail("待办不存在或无权限删除");
        return Result.success("删除成功");
    }

}
