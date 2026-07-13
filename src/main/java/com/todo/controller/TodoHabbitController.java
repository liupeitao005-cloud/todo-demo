package com.todo.controller;


import com.todo.dto.TodoHabbitDTO;
import com.todo.entity.TodoHabbit;
import com.todo.service.TodoHabbitService;
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
@RequiredArgsConstructor
@RequestMapping("/habbit")
@Tag(name = "习惯接口", description = "习惯创建和列表查询")
@SecurityRequirement(name = "tokenAuth")
public class TodoHabbitController {
    private final TodoHabbitService todoHabbitService;

    @Operation(summary = "创建习惯")
    @PostMapping("/create")
    public Result<String> create (@Validated(ValidationGroups.Create.class) @RequestBody TodoHabbitDTO dto){
        return todoHabbitService.createHabbit(dto);
    }

    @Operation(summary = "查询习惯列表")
    @GetMapping("/select")
    public  Result<List<TodoHabbit>> list(){
        return  todoHabbitService.listHabbit();
    }
}
