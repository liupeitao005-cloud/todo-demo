package com.todo.service;


import com.todo.dto.TodoHabbitDTO;
import com.todo.entity.TodoHabbit;
import com.todo.mapper.TodoHabbitMapper;
import com.todo.util.Result;
import com.todo.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;




@Service
@RequiredArgsConstructor
public class TodoHabbitService {
    private final TodoHabbitMapper todoHabbitMapper;

    public Result<String> createHabbit (TodoHabbitDTO dto){
        Long userID = UserContext.getUserId();
        if(userID == null) return Result.fail("未登录");
        if(dto == null || dto.getTitle() ==null || dto.getTitle().equals("")){
            return Result.fail("习惯标题不能为空");
        }
        TodoHabbit habbit = new TodoHabbit();
        habbit.setUserId(userID);
        habbit.setTitle(dto.getTitle());
        habbit.setContent(dto.getContent());
        habbit.setDayMinutes(dto.getDayMinutes());
        habbit.setMinMinutes(dto.getMinMinutes());
        habbit.setMaxMinutes(dto.getMaxMinutes());
        todoHabbitMapper.insert(habbit);
        return Result.success("创建成功");
    }
}
