package com.todo.controller;


import com.todo.dto.TodoFourDTO;
import com.todo.entity.TodoFour;
import com.todo.service.TodoFourService;
import com.todo.util.Result;
import com.todo.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/four")
@RequiredArgsConstructor
@Tag(name = "四象限接口", description = "按重要性和紧急性管理任务")
@SecurityRequirement(name = "tokenAuth")
public class TodoFourController {
    private final TodoFourService todoFourService;

    @Operation(summary = "移动任务到四象限")
    @PostMapping("/move")
    public Result<String> moveTodoFour (@Validated(ValidationGroups.Move.class) @RequestBody TodoFourDTO dto){
        return todoFourService.moveTodoFour(dto);
    }

    @Operation(summary = "查询四象限任务")
    @GetMapping("/select")
    public Result<List<TodoFour>> selectTodoFour (@ParameterObject @Validated(ValidationGroups.Query.class) TodoFourDTO dto){
        return todoFourService.listTodoFour(dto);
    }
}
