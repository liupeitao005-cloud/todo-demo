package com.todo.controller;

import com.todo.dto.TodoBacklogDTO;
import com.todo.dto.TodoFourDTO;
import com.todo.dto.TodoTaskDTO;
import com.todo.service.TodoTaskService;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TodoTaskController {
    private final TodoTaskService todoTaskService;

    @PostMapping("/create")
    public Result<String> create(@RequestBody TodoTaskDTO dto) {
        return todoTaskService.createTodoTask(dto);
    }
    @PutMapping("/update")
    public Result<String> update(@RequestBody TodoTaskDTO dto) {
        return todoTaskService.updateTodoTask(dto);
    }
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestBody TodoTaskDTO dto) {
        return todoTaskService.deleteTodoTask(dto.getId());
    }
    @PutMapping("/finish")
    public Result<String> finish(@RequestBody TodoTaskDTO dto) {
        return todoTaskService.finishTodoTask(dto.getId());
    }
    @PutMapping("/split")
    public Result<String>split(@RequestBody TodoTaskDTO dto){
        return  todoTaskService.qiegehour(dto.getId());
    }
    @PutMapping("/goout")
    public Result<String>yanqi(@RequestBody TodoTaskDTO dto){
        return  todoTaskService.yanqi(dto.getId());
    }
    @PutMapping("/next")
    public Result<String>next(@RequestBody TodoTaskDTO dto){
        return  todoTaskService.nextTodoTask(dto.getId());
    }

}

