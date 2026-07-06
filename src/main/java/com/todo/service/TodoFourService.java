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

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoFourService {
    private final TodoTaskMapper todoTaskMapper;
    private  final TodoFourMapper todoFourMapper;

    public Result<String>moveTodoFour(TodoFourDTO dto){
        Long userID = UserContext.getUserId();
        if(userID == null) return Result.fail("未登录");
        if(dto == null || dto.getId() == null) {
            return Result.fail("缺少任务id");
        }
        TodoTask query = new TodoTask();
        query.setId(dto.getId());
        query.setUserId(userID);
        TodoTask task = todoTaskMapper.selectByID(query);
        if (task == null) {
            return Result.fail("任务不存在或无权限操作");
        }
        TodoFour todoFour = new TodoFour();
        todoFour.setUserId(userID);
        todoFour.setTitle(task.getTitle());
        todoFour.setContent(task.getContent());
        todoFour.setImportance(dto.getImportance());
        todoFour.setUrgency(dto.getUrgency());
        todoFour.setStartTime(task.getStartTime());
        todoFour.setFinishTime(task.getFinishTime());
        todoFourMapper.insert(todoFour);
        return Result.fail("放入四象限成功");
    }
  public Result<List<TodoFour>> listTodoFour(TodoFourDTO dto){
        Long userID = UserContext.getUserId();
        if (userID == null) return Result.fail("未登录");
        if(dto == null || dto.getImportance() == null || dto.getUrgency() == null) {
            return Result.fail("缺少重要性或者紧急性");
        }
        TodoFour four = new TodoFour();
        four.setUserId(userID);
        four.setImportance(dto.getImportance());
        four.setUrgency(dto.getUrgency());
      List<TodoFour> list = todoFourMapper.selectByFour(four);

      return Result.success("查询成功", list);
  }
  }


