package com.todo.controller;


import com.todo.dto.TodoFourDTO;
import com.todo.entity.TodoFour;
import com.todo.service.TodoFourService;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/four")
@RequiredArgsConstructor
public class TodoFourController {
    private final TodoFourService todoFourService;
    @PostMapping("/move")
    public Result<String> moveTodoFour (@RequestBody TodoFourDTO dto){
        return todoFourService.moveTodoFour(dto);
    }
    @GetMapping("/select")
    public Result<List<TodoFour>> selectTodoFour (TodoFourDTO dto){
        return todoFourService.listTodoFour(dto);
    }
}
