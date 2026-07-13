package com.todo.controller;

import com.todo.dto.TodoTaskDTO;
import com.todo.entity.TodoTask;
import com.todo.service.TodoTaskService;
import com.todo.util.Result;
import com.todo.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Tag(name = "任务接口", description = "任务创建、修改、删除、完成和列表查询")
@SecurityRequirement(name = "tokenAuth")
public class TodoTaskController {
    private final TodoTaskService todoTaskService;

    @Operation(summary = "创建任务")
    @PostMapping("/create")
    public Result<String> create(@Validated(ValidationGroups.Create.class) @RequestBody TodoTaskDTO dto) {
        return todoTaskService.createTodoTask(dto);
    }

    @Operation(summary = "修改任务")
    @PutMapping("/update")
    public Result<String> update(@Validated(ValidationGroups.Update.class) @RequestBody TodoTaskDTO dto) {
        return todoTaskService.updateTodoTask(dto);
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/delete")
    public Result<String> delete(@Validated(ValidationGroups.IdRequired.class) @RequestBody TodoTaskDTO dto) {
        return todoTaskService.deleteTodoTask(dto.getId());
    }

    @Operation(summary = "完成任务")
    @PutMapping("/finish")
    public Result<String> finish(@Validated(ValidationGroups.IdRequired.class) @RequestBody TodoTaskDTO dto) {
        return todoTaskService.finishTodoTask(dto.getId());
    }

    @Operation(summary = "拆分任务")
    @PutMapping("/split")
    public Result<String>split(@Validated(ValidationGroups.IdRequired.class) @RequestBody TodoTaskDTO dto){
        return  todoTaskService.qiegehour(dto.getId());
    }

    @Operation(summary = "延期任务")
    @PutMapping("/goout")
    public Result<String>yanqi(@Validated(ValidationGroups.IdRequired.class) @RequestBody TodoTaskDTO dto){
        return  todoTaskService.yanqi(dto.getId());
    }

    @Operation(summary = "设置下一步任务")
    @PutMapping("/next")
    public Result<String>next(@Validated(ValidationGroups.IdRequired.class) @RequestBody TodoTaskDTO dto){
        return  todoTaskService.nextTodoTask(dto.getId());
    }

    @Operation(summary = "查询任务列表")
    @GetMapping("/select")
    public  Result<List<TodoTask>> list(){
        return  todoTaskService.listTodoTask();
    }

}
