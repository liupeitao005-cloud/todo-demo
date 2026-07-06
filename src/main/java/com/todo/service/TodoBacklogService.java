package com.todo.service;


import com.todo.dto.TodoBacklogDTO;
import com.todo.entity.TodoBacklog;
import com.todo.entity.TodoTask;
import com.todo.mapper.TodoBacklogMapper;
import com.todo.mapper.TodoTaskMapper;
import com.todo.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.todo.util.Result;

@Service
@RequiredArgsConstructor
public class TodoBacklogService {
    private final TodoBacklogMapper todoBacklogMapper;
    private final TodoTaskMapper todoTaskMapper;
    public Result<String> createtodoBacklog(TodoBacklogDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto==null||dto.getContent() == null || dto.getContent().equals("")) {
            return Result.fail("待办内容不能为空");
        }
        TodoBacklog backlog = new TodoBacklog();
        backlog.setUserId(userId);
        backlog.setContent(dto.getContent());
        backlog.setTitle(dto.getTitle());
        todoBacklogMapper.insert(backlog);
        return Result.success("创建成功");
    }

    public Result<String> updateBacklog(TodoBacklogDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto.getId() == null) return Result.fail("缺少待办id");
        TodoBacklog backlog = new TodoBacklog();
        backlog.setId(dto.getId());
        backlog.setUserId(userId);
        backlog.setContent(dto.getContent());
        backlog.setTitle(dto.getTitle());
        int row = todoBacklogMapper.update(backlog);
        if (row <= 0) return Result.fail("待办不存在或无权限修改");
        return Result.success("修改成功");
    }
    public Result<String>moveTodoBacklog(TodoBacklogDTO dto){
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        if (dto.getId() == null) return Result.fail("缺少待办id");
        if (dto==null||dto.getStartTime() == null || dto.getFinishTime() == null) {
            return Result.fail("开始时间和结束时间不能为空");
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
        return Result.success("拖拽到日程成功");
    }

}