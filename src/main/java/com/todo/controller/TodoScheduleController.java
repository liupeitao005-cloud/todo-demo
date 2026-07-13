package com.todo.controller;

import com.todo.dto.TodoScheduleDTO;
import com.todo.entity.TodoSchedule;
import com.todo.service.TodoScheduleService;
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
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Tag(name = "行程接口", description = "行程创建、修改、删除和列表查询")
@SecurityRequirement(name = "tokenAuth")
public class TodoScheduleController {
    private final TodoScheduleService todoScheduleService;

    @Operation(summary = "创建行程")
    @PostMapping("/create")
    public Result<String> create(@Validated(ValidationGroups.Create.class) @RequestBody TodoScheduleDTO dto) {
        return todoScheduleService.createTodoSchedule(dto);
    }

    @Operation(summary = "修改行程")
    @PutMapping("/update")
    public Result<String> update(@Validated(ValidationGroups.Update.class) @RequestBody TodoScheduleDTO dto) {
        return todoScheduleService.updateTodoSchedule(dto);
    }

    @Operation(summary = "删除行程")
    @DeleteMapping("/delete")
    public Result<String> delete(@Validated(ValidationGroups.IdRequired.class) @RequestBody TodoScheduleDTO dto) {
        return todoScheduleService.deleteTodoSchedule(dto.getId());
    }

    @Operation(summary = "查询行程列表")
    @GetMapping("/select")
    public  Result<List<TodoSchedule>> list(){
        return  todoScheduleService.listTodoSchedule();
    }
}
