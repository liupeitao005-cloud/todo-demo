package com.todo.controller;

import com.todo.dto.TodoTaskDTO;
import com.todo.service.TodoTaskService;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TodoTaskController {
    private final TodoTaskService todoTaskService;

    @PostMapping("/create")
    public Result<String> create(@RequestBody TodoTaskDTO dto) {

        return todoTaskService.createTodoTask(dto);
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody TodoTaskDTO dto) {

        return todoTaskService.updateTodoTask(dto);
    }

    @PostMapping("/delete")
    public Result<String> delete(@RequestBody TodoTaskDTO dto) {
        return todoTaskService.deleteTodoTask(dto.getId());
    }

    @PostMapping("/finish")
    public Result<String> finish(@RequestBody TodoTaskDTO dto) {
        return todoTaskService.finishTodoTask(dto.getId());
    }
}
