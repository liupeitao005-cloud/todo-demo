package com.todo.service;

import com.todo.dto.TodoFourDTO;
import com.todo.entity.TodoFour;
import com.todo.entity.TodoTask;
import com.todo.mapper.TodoFourMapper;
import com.todo.mapper.TodoTaskMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoFourService {
    private final TodoTaskMapper todoTaskMapper;
    private final TodoFourMapper todoFourMapper;

    @Transactional
    public Result<String> moveTodoFour(TodoFourDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");

        TodoTask query = new TodoTask();
        query.setId(dto.getId());
        query.setUserId(userId);
        TodoTask task = todoTaskMapper.selectByID(query);
        if (task == null) {
            return Result.fail("任务不存在或无权限操作");
        }

        TodoFour todoFour = new TodoFour();
        todoFour.setUserId(userId);
        todoFour.setTitle(task.getTitle());
        todoFour.setContent(task.getContent());
        todoFour.setImportance(dto.getImportance());
        todoFour.setUrgency(dto.getUrgency());
        todoFour.setStartTime(task.getStartTime());
        todoFour.setFinishTime(task.getFinishTime());
        int updated = todoFourMapper.updateQuadrantBySnapshot(todoFour);
        if (updated <= 0) {
            todoFourMapper.insert(todoFour);
        }
        return Result.success("放入四象限成功");
    }

    public Result<List<TodoFour>> listTodoFour(TodoFourDTO dto) {
        if (dto == null) return Result.fail("请求参数不能为空");
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");

        TodoFour four = new TodoFour();
        four.setUserId(userId);
        four.setImportance(dto.getImportance());
        four.setUrgency(dto.getUrgency());
        return Result.success("查询成功", todoFourMapper.selectByFour(four));
    }

    public Result<String> deleteTodoFour(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");

        TodoFour four = new TodoFour();
        four.setId(id);
        four.setUserId(userId);
        int row = todoFourMapper.delete(four);
        if (row <= 0) return Result.fail("四象限任务不存在或无权删除");
        return Result.success("移出四象限成功");
    }
}
